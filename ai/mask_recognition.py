from keras.models import load_model
from PIL import Image, ImageOps
import numpy as np

model = load_model('.\mask_reco_model.h5', compile=False)
data = np.ndarray(shape=(1, 224, 224, 3), dtype=np.float32)


app = Flask(__name__)
CORS(app)

@app.route('/maskreco', methods=['GET'])
def maskreco():
    if request.method == 'GET':
        image = Image.open('')
        size = (224, 224)
        image = ImageOps.fit(image, size, Image.ANTIALIAS)
        image_array = np.asarray(image)
        normalized_image_array = (image_array.astype(np.float32) / 127.0) - 1
        data[0] = normalized_image_array

        prediction = model.predict(data)
        return {'box': prediction}
if __name__ == '__main__':
    app.run(port=1219)