package com.a205.mafya.api.service;

import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.entity.UserImg;
import com.a205.mafya.db.repository.UserImgRepository;
import com.a205.mafya.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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

    @Autowired
    AwsS3Service awsS3Service;

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
                String url = "";

                //이미지 저장(새로운 저장 혹은 덮어 쓰기)
                img.transferTo(new File(fileFullPath));
                url = awsS3Service.uploadFileOnlyOne(img);

                if ("fail".equals(url)) return (false);

                Optional<UserImg> info = userImgRepository.findByUser(user.get());
                if (info.isPresent()) {
                    String target = info.get().getImgUrl();

                    awsS3Service.deleteFile(target);    //과거 파일 지우기

                    info.get().setImgUrl(url);      //새로운 url 등록
                    userImgRepository.save(info.get());
                }
                else {
                    userImg.setUser(user.get());
                    userImg.setImgUrl(url);
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

    @Override
    public Map<String, String> processFace(MultipartFile img) {
        Map<String, String> result = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", img.getResource());

        HttpEntity<?> entity = new HttpEntity<>(body, header);
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(faceURL).build();
        ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, String.class);
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

        return (result);
    }

    @Override
    public Map<String, String> processMask(MultipartFile img, String userCode) {
        Map<String, String> result = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", img.getResource());

        HttpEntity<?> entity = new HttpEntity<>(body, header);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(maskURL).build();
        ResponseEntity<String> response = restTemplate.exchange(uri.toString(), HttpMethod.POST, entity, String.class);

        String maskStatus = response.getBody();

        Optional<User> user = userRepository.findByUserCode(userCode);

        if (!user.isPresent())  result.put("name", "[" + userCode + "] DB 검색 불가.");
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

        return (result);
    }
}
