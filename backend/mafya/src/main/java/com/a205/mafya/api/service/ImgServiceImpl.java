package com.a205.mafya.api.service;

import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class ImgServiceImpl implements ImgService {
    @Autowired
    UserRepository userRepository;

    @Override
    public boolean saveImg(MultipartFile img, String userCode) {
        String filePath = "/sehyeon";

        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        filePath = filePath + "/" + userCode;
        dir = new File(filePath);
        dir.mkdir();

        String fileFullPath = filePath + "/" + userCode + ".jpg" ;

        try {
            img.transferTo(new File(fileFullPath));
            return (true);
        } catch (Exception e) {
            return (false);
        }
    }

    @Override
    public String makeUrl(String userCode) {
        String fullFilePath = imgURL + "/" + userCode + "/" + userCode + ".jpg";
        return (fullFilePath);
    }

    private static boolean uploadCamImg(MultipartFile img, String imgName) {
        String filePath = "/identify";

        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        String fileFullPath = filePath + "/" + imgName;

        try {
            img.transferTo(new File(fileFullPath));
            return (true);
        } catch (Exception e) {
            System.out.println("camImg 업로드 실패");
            return (false);
        }
    }

    @Override
    public Map<String, String> processFace(MultipartFile img) throws InterruptedException {
        Map<String, String> result = new HashMap<>();

        boolean status = uploadCamImg(img, "face.jpg");
        if (status) {   //cam.jpg 업로드 성공
            for (int i = 0; i < 1; i++)
                TimeUnit.SECONDS.sleep(2);


            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(faceURL).build();
            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);
            String userCode = response.getBody();

            if ("Unknown".equals(userCode)) {
                result.put("status", "1");  //얼굴 인식 안 됨
            }
            else {
                result.put("status", "0");  //얼굴 인식 됨
                result.put("userCode", userCode);
            }
        }
        else {
            result.put("status", "2");  //cam.jpg 업로드 실패
        }

        return (result);
    }

    @Override
    public Map<String, String> processMask(MultipartFile img, String userCode) throws InterruptedException {
        Map<String, String> result = new HashMap<>();

        boolean status = uploadCamImg(img, "mask.jpg");
        if (status) {   //cam.jpg 업로드 성공
            for (int i = 0; i < 1; i++)
                TimeUnit.SECONDS.sleep(2);

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(maskURL).build();
            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);
            String maskStatus = response.getBody();

            Optional<User> user = userRepository.findByUserCode(userCode);

            if (!user.isPresent())  result.put("name", "Unknown");
            else                    result.put("name", user.get().getName());

            System.out.println(">>> " + user.get().getName());

            if ("undefined".equals(maskStatus))
                result.put("status", "1");  //마스크 인식 안 됨
            else if ("no_mask".equals(maskStatus))
                result.put("status", "1");  //마스크 인식 안 됨
            else if ("middle_mask".equals(maskStatus))
                result.put("status", "2");  //마스크 인식 반만 인식
            else   //"mask"
                result.put("status", "0");  //마스크 인식 됨
        }
        else {
            result.put("status", "3");  //cam.jpg 업로드 실패
        }

        return (result);
    }


}
