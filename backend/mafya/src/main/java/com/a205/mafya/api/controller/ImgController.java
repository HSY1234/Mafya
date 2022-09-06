package com.a205.mafya.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/img")
public class ImgController {

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> uploadImage(@RequestPart(value = "file") MultipartFile multipartFile) {
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH시 mm분 ss초");
        String formatedNow = now.format(formatter);
        System.out.println(formatedNow + ": " + multipartFile.getContentType() + " " + multipartFile.getOriginalFilename() + " " + multipartFile.getSize());

        return (new ResponseEntity<String>("success", HttpStatus.OK));
    }

}
