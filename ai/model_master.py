import urllib.request
import json
import time
import cv2
from facebank import load_facebank, prepare_facebank
from face_model import MobileFaceNet, l2_norm
from MTCNN import create_mtcnn_net
from utils.align_trans import *
from utils.util import *
import numpy as np
from flask import Flask, request
from PIL import Image, ImageDraw, ImageFont, ImageOps
from torchvision import transforms as trans
import torch
import argparse
import sys
import os
import io
import json
from torchvision import models
from PIL import Image
from flask import Flask, jsonify, request
from flask_cors import CORS
from keras.models import load_model

os.environ['KMP_DUPLICATE_LIB_OK']='True' # 라이브러리 충돌
# 마스크 인식
mask_model = load_model('mask_reco_model.h5', compile=False)
mask_data = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)

# 얼굴 인식
# device_0 = torch.device("cpu")
device_0 = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")
detect_model = MobileFaceNet(512).to(device_0)
detect_model.load_state_dict(torch.load(
    'Weights/MobileFace_Net', map_location=lambda storage, loc: storage))
detect_model.eval()
target, name = load_facebank(path='facebank')
parser = argparse.ArgumentParser()
parser.add_argument('--miniface', default=20, type=int)
parser.add_argument('--update', default=False, type=bool)
args = parser.parse_args()
if args.update:# 업데이트
    targets, names = prepare_facebank(
        detect_model, path='facebank')
    print('facebank updated')
else:# 업데이트 없음
    targets, names = load_facebank(path='facebank')
    print('facebank loaded')
URL_fr = ''


def image_to_byte_array(image: Image) -> bytes:
  # BytesIO is a fake file stored in memory
  imgByteArr = io.BytesIO()
  # image.save expects a file as a argument, passing a bytes io ins
  image.save(imgByteArr, format=image.format)
  # Turn the BytesIO object back into a bytes object
  imgByteArr = imgByteArr.getvalue()
  return imgByteArr


def URL2Frame(URL):
    #print(type(urllib.request.urlopen(URL)))
    #print(type(urllib.request.urlopen(URL).read()))
    #print(type(image_to_byte_array(Image.open('AR.jpg'))))
    img_arr = np.array(
        bytearray(image_to_byte_array(Image.open('identify/face.jpg'))), dtype=np.uint8)
        #bytearray(urllib.request.urlopen(URL).read()), dtype=np.uint8)
    frame = cv2.imdecode(img_arr, -1)
    # print(frame)
    return frame


def resize_image(img, scale):
    """
        resize image
    """
    height, width, channel = img.shape
    new_height = int(height * scale)     # resized new height
    new_width = int(width * scale)       # resized new width
    new_dim = (new_width, new_height)
    img_resized = cv2.resize(
        img, new_dim, interpolation=cv2.INTER_LINEAR)      # resized image
    return img_resized


def MTCNN_NET(frame, scale, device, p_model_path, r_model_path, o_model_path):
    input = resize_image(frame, scale)
    #print(input)
    bboxes, landmarks = create_mtcnn_net(
        input, args.miniface, device, p_model_path, r_model_path, o_model_path)
    #print(bboxes)
    if bboxes != []:
        bboxes = bboxes / scale
        landmarks = landmarks / scale

    return bboxes, landmarks


def get_face(URL, device, targets=target, names=name):
    student_list = []
    frame = URL2Frame(URL)
    #print("frame:",frame)
    try:
        bboxes, landmarks = MTCNN_NET(frame, 0.5, device, 'MTCNN/weights/pnet_Weights',
                                      'MTCNN/weights/rnet_Weights', 'MTCNN/weights/onet_Weights')
        landmarks = landmarks.tolist()
        for i, b in enumerate(bboxes):
            box = dict()
            box['x1'] = b[0]
            box['y1'] = b[1]
            box['x2'] = b[2]
            box['y2'] = b[3]
            box['landmarks'] = landmarks[i]
            student_list.append(box)
        print(student_list)
        return student_list
    except:
        return []


def get_id(URL, device, targets=target, names=name):
    student_list = []
    frame = URL2Frame(URL)
    try:
        bboxes, landmarks = MTCNN_NET(frame, 0.5, device, 'MTCNN/weights/pnet_Weights',
                                      'MTCNN/weights/rnet_Weights', 'MTCNN/weights/onet_Weights')

        faces = Face_alignment(
            frame, default_square=True, landmarks=landmarks)

        embs = []

        test_transform = trans.Compose([
            trans.ToTensor(),
            trans.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])])

        for img in faces:
            embs.append(detect_model(
                test_transform(img).to(device).unsqueeze(0)))

        if embs != []:
            source_embs = torch.cat(embs)
            diff = source_embs.unsqueeze(-1) - \
                targets.transpose(1, 0).unsqueeze(0)
            dist = torch.sum(torch.pow(diff, 2), dim=1)
            minimum, min_idx = torch.min(dist, dim=1)
            min_idx[minimum > ((75-156)/(-80))] = -1
            results = min_idx

            for i, k in enumerate(bboxes):
                if results[i] == -1:
                    continue
                student_list.append(names[results[i] + 1])
        return student_list
    except:
        return []


def get_info(URL, landmark, device, targets=target, names=name):
    student_list = []
    frame = URL2Frame(URL)
    landmark = np.array(landmark)
    # try:

    faces = []
    landmark = landmark.reshape(2, 5).T

    coord5point = [[38.29459953, 51.69630051],
                    [73.53179932, 51.50139999],
                    [56.02519989, 71.73660278],
                    [41.54930115, 92.3655014],
                    [70.72990036, 92.20410156]]

    pts1 = np.float64(
        np.matrix([[point[0], point[1]] for point in landmark]))
    pts2 = np.float64(np.matrix([[point[0], point[1]]
                                    for point in coord5point]))
    M = transformation_from_points(pts1, pts2)
    aligned_image = cv2.warpAffine(
        frame, M[:2], (frame.shape[1], frame.shape[0]))
    crop_img = aligned_image[0:112, 0:112]
    faces.append(crop_img)

    embs = []

    test_transform = trans.Compose([
        trans.ToTensor(),
        trans.Normalize([0.5, 0.5, 0.5], [0.5, 0.5, 0.5])])

    for img in faces:
        embs.append(detect_model(
            test_transform(img).to(device).unsqueeze(0)))

    if embs != []:
        source_embs = torch.cat(embs)
        diff = source_embs.unsqueeze(-1) - \
            targets.transpose(1, 0).unsqueeze(0)
        dist = torch.sum(torch.pow(diff, 2), dim=1)
        minimum, min_idx = torch.min(dist, dim=1)
        min_idx[minimum > ((75-156)/(-80))] = -1
        results = min_idx

        if results[0] == -1:
            student_list.append('Unknown')
        else:
            student_list.append(names[results[0] + 1])
    return student_list
    # except:
    #     print("문제발생!")
    #     return ['Unknown']


app = Flask(__name__)
CORS(app)


@app.route('/ai/maskreco', methods=['GET'])
def maskreco():
    print("마스크 인식 시작!")
    if request.method == 'GET':
        image = Image.open('identify/mask.jpg')
        size = (224, 224)
        image = ImageOps.fit(image, size, Image.ANTIALIAS)
        image_array = np.asarray(image)
        normalized_image_array = (image_array.astype(np.float32) / 127.0) - 1
        mask_data[0] = normalized_image_array

        prediction = mask_model.predict(mask_data)
        if prediction[0][0]>prediction[0][1] and prediction[0][0]>prediction[0][2]:
            # return {'result': 'mask'}
            return 'mask'
        elif prediction[0][1]>prediction[0][0] and prediction[0][1]>prediction[0][2]:
            # return {'result': 'middle_mask'}
            return 'middle_mask'
        else:
            # return {'result': 'no_mask'}
            return 'no_mask'
        # return {'result': 'undefined'}
        return 'undefined'

@app.route('/ai/modelfr', methods=['GET'])
def modelfr():
    if request.method == 'GET':
        URL_fr = request.args.get('ip', '')
        student_list = get_face(URL_fr, device_0, target, name)
        return {'box': student_list}


@app.route('/ai/modelat', methods=['GET'])
def modelat():
    if request.method == 'GET':
        URL_at = request.args.get('ip', '')
        student_list = get_id(URL_at, device_0, target, name)
        return {'id': student_list}


@app.route('/ai/modelin', methods=['GET'])
def modelin():
    if request.method == 'GET':
        print("찾아볼께요")
        URL_in = request.args.get('ip', '')
        landmark = request.args.get('landmark', '')
        try:
            landmark = list(
                map(float, landmark.rstrip(']').lstrip('[').split(',')))
        except:
            pass
        student_id = get_info(URL_in, landmark, device_0, target, name)
        return {'id': student_id}

@app.route('/ai/modelsearch', methods=['GET'])
def modelsearch():
    if request.method == 'GET':
        print("누군지 찾아볼께요")
        URL_fr = ""
        URL_in = ""
        student_list = get_face(URL_fr, device_0, target, name)
        if not len(student_list):
            return "no face"
        landmark=student_list[0]["landmarks"]
        try:
            landmark = list(
                map(float, landmark.rstrip(']').lstrip('[').split(',')))
        except:
            pass
        student_id = get_info(URL_in, landmark, device_0, target, name)
        
        return student_id[0]
        # return {'id': student_id}


#@app.router('/update')
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8081, debug=True)
