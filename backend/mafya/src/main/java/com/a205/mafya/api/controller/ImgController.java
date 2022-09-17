package com.a205.mafya.api.controller;

import com.a205.mafya.api.service.ImgService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
        boolean result = imgService.saveImg(multipartFile, userCode);

        //DB에 저장해야함 JPA 안했음

        if (result) return (new ResponseEntity<String>(SUCCESS, HttpStatus.OK));
        else        return (new ResponseEntity<String>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping(value = "/{userCode}")
    public ResponseEntity<?> requestimgURL(@PathVariable String userCode) {
        String imgUrl = imgService.makeUrl(userCode);

        //DB통해서 URL가져오도록 변경해야함

        if ("".equals(imgUrl) || imgUrl == null)    return (new ResponseEntity<String>(FAIL, HttpStatus.OK));
        else                                        return (new ResponseEntity<String>(imgUrl, HttpStatus.OK));
    }

    @PostMapping(value = "/face", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> recognizeFace(@RequestPart(value = "file") MultipartFile multipartFile) {
        Map<String, String> result = imgService.processFace(multipartFile);

        return (new ResponseEntity<Map<String, String>>(result, HttpStatus.OK));
    }

    @PostMapping(value = "/mask", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> recognizeMask(@RequestPart(value = "file") MultipartFile multipartFile, String userCode) {
        Map<String, String> result = imgService.processMask(multipartFile, userCode);

        //User Repository을 이용하여 학번 -> 이름 구해야함

        return (new ResponseEntity<Map<String, String>>(result, HttpStatus.OK));
    }

//    @PostMapping(value = "/uploadCam", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
//    public ResponseEntity<?> uploadCamImg(@RequestPart(value = "file") MultipartFile multipartFile) {
//        LocalTime now = LocalTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
//        String formatedNow = now.format(formatter);
//        System.out.println("[uploadCam] " + formatedNow + ": " + multipartFile.getContentType() + " " + multipartFile.getOriginalFilename() + " " + multipartFile.getSize());
//
//        boolean result =imgService.uploadCamImg(multipartFile);
//
//        if (result) return (new ResponseEntity<String>(SUCCESS, HttpStatus.OK));
//        else        return (new ResponseEntity<String>(FAIL, HttpStatus.INTERNAL_SERVER_ERROR));
//    }

}
