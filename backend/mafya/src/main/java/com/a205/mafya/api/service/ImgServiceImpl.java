package com.a205.mafya.api.service;

import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.entity.UserImg;
import com.a205.mafya.db.repository.UserImgRepository;
import com.a205.mafya.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.transaction.Transactional;
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

    @Autowired
    UserImgRepository userImgRepository;

    @Override
    @Transactional
    public boolean saveImg(MultipartFile img, String userCode) {
        String filePath = "/sehyeon";

        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        filePath = filePath + "/" + userCode;
        dir = new File(filePath);
        dir.mkdir();

        try {
            UserImg userImg = new UserImg();
            Optional<User> user = userRepository.findByUserCode(userCode);

            if (user.isPresent()) {
                final String imgFullUrl = imgURL + "/" + userCode + "/" + userCode + ".jpg";
                final String fileFullPath = filePath + "/" + userCode + ".jpg" ;

                //이미지 저장(새로운 저장 혹은 덮어 쓰기)
                img.transferTo(new File(fileFullPath));

                Optional<UserImg> info = userImgRepository.findByUser(user.get());
                if (info.isPresent()) {
                    info.get().setImgUrl(imgFullUrl);
                    userImgRepository.save(info.get());
                }
                else {
                    userImg.setUser(user.get());
                    userImg.setImgUrl(imgFullUrl);
                    userImgRepository.save(userImg);
                }
            }
            else {
                return (false);
            }
            return (true);
        } catch (Exception e) {
            return (false);
        }
    }

    @Override
    public String getUrl(String userCode) {
        Optional<User> user = userRepository.findByUserCode(userCode);

        if (user.isPresent()) {
            Optional<UserImg> userImg = userImgRepository.findByUser(user.get());
            if (userImg.isPresent())    return (userImg.get().getImgUrl());
            else                        return ("");
        }
        else
            return ("");
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
    public Map<String, String> processFace(MultipartFile img) {
        Map<String, String> result = new HashMap<>();

        boolean status = uploadCamImg(img, "face.jpg");
        if (status) {   //cam.jpg 업로드 성공
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            HttpEntity<?> entity = new HttpEntity<>(header);
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(faceURL).build();
            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, String.class);
            String userCode = response.getBody();

            if ("Unknown".equals(userCode) || "no face".equals(userCode)) {
                result.put("status", "1");  //얼굴 인식 안 됨
            }
            else {
                Optional<User> user = userRepository.findByUserCode(userCode);

                if (!user.isPresent())  result.put("name", "[" + userCode + "] DB 검색 불가.");
                else                    result.put("name", user.get().getName());
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
    public Map<String, String> processMask(MultipartFile img, String userCode) {
        Map<String, String> result = new HashMap<>();

        boolean status = uploadCamImg(img, "mask.jpg");
        if (status) {   //cam.jpg 업로드 성공
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


    @Override
    public Map<String, String> processFace2(MultipartFile img) {
        Map<String, String> result = new HashMap<>();

        System.out.println(">>>>>>>test1");

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", img);

            HttpEntity<?> entity = new HttpEntity<>(body, header);

            UriComponents uri = UriComponentsBuilder.fromHttpUrl(faceURL).build();
        System.out.println(">>>>>>>test2");

            ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, String.class);

        System.out.println(">>>>>>>test3: " + response);

            String userCode = response.getBody();

        System.out.println(">>>>>>>test4:" + userCode);


            if ("Unknown".equals(userCode) || "no face".equals(userCode)) {
                System.out.println(">>>>>>>test5-1");
                result.put("status", "1");  //얼굴 인식 안 됨
            }
            else {
                System.out.println(">>>>>>>test5-2");
                Optional<User> user = userRepository.findByUserCode(userCode);

                if (!user.isPresent())  result.put("name", "[" + userCode + "] DB 검색 불가.");
                else                    result.put("name", user.get().getName());
                result.put("status", "0");  //얼굴 인식 됨
                result.put("userCode", userCode);
            }
        return (result);
    }
}
