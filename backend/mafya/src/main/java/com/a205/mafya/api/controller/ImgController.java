package com.a205.mafya.api.controller;

import com.a205.mafya.api.service.ImgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/img")
public class ImgController {
    static final String SUCCESS = "success";
    static final String FAIL = "fail";

    @Autowired
    private ImgService imgService;

    @PostMapping(value = "/register/{userCode}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> registFace(@RequestPart(value = "file") MultipartFile multipartFile, @PathVariable String userCode) {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        String formatedNow = now.format(formatter);
        System.out.println("[register] " + formatedNow + ": " + multipartFile.getContentType() + " " + multipartFile.getOriginalFilename() + " " + multipartFile.getSize());
        System.out.println("[register] userCode = " + userCode);

        boolean result = imgService.saveImg(multipartFile, userCode);

        if (result) return (new ResponseEntity<String>(SUCCESS, HttpStatus.OK));
        else        return (new ResponseEntity<String>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @PostMapping(value = "/uploadCam", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> uploadCamImg(@RequestPart(value = "file") MultipartFile multipartFile) {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        String formatedNow = now.format(formatter);
        System.out.println("[uploadCam] " + formatedNow + ": " + multipartFile.getContentType() + " " + multipartFile.getOriginalFilename() + " " + multipartFile.getSize());

        boolean result =imgService.uploadCamImg(multipartFile);

        if (result) return (new ResponseEntity<String>(SUCCESS, HttpStatus.OK));
        else        return (new ResponseEntity<String>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/{userCode}")
    public ResponseEntity<?> requestimgURL(@PathVariable String userCode) {
        String imgUrl = imgService.makeUrl(userCode);

        if ("".equals(imgUrl) || imgUrl == null)
            return (new ResponseEntity<String>(FAIL, HttpStatus.OK));
        else
            return (new ResponseEntity<String>(imgUrl, HttpStatus.OK));
    }

    @PostMapping(value = "/face", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> recognizeFace(@RequestPart(value = "file") MultipartFile multipartFile) {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        String formatedNow = now.format(formatter);
        System.out.println("[Face] " + formatedNow + ": " + multipartFile.getContentType() + " " + multipartFile.getOriginalFilename() + " " + multipartFile.getSize());

        int status = 0;
        //int status = 1;   //얼굴 인식 안 됨
        String codeName = "0743111";

        Map<String, String> result = new HashMap<>();
        result.put("status", "" + status);
        result.put("code_name", codeName);

        return (new ResponseEntity<Map<String, String>>(result, HttpStatus.OK));
    }

    @PostMapping(value = "/mask", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> recognizeMask(@RequestPart(value = "file") MultipartFile multipartFile, String codeName) {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        String formatedNow = now.format(formatter);
        System.out.println("[Mask] " + formatedNow + ": " + multipartFile.getContentType() + " " + multipartFile.getOriginalFilename() + " " + multipartFile.getSize());

        int status = 0;
//        int status = 1; //얼굴 인식 안 됨
        //int status = 2; //등록된 사용자 아님
        String name = "기사 김무종";

        Map<String, String> result = new HashMap<>();
        result.put("status", "" + status);
        result.put("name", name);

        return (new ResponseEntity<Map<String, String>>(result, HttpStatus.OK));
    }
}
