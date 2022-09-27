package com.a205.mafya.api.controller;

import com.a205.mafya.api.request.MmsReq;
import com.a205.mafya.api.service.MmsService;
import com.a205.mafya.api.service.MmsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/mms")
public class MmsController {

    @Autowired
    private MmsService mmsService;

    @PostMapping
    public ResponseEntity<?> sendMms(@RequestBody MmsReq mmsReq){
        String result = mmsService.sendMms(mmsReq);
        System.out.println(result);
        return (new ResponseEntity<String>("success", HttpStatus.OK));
    }
}
