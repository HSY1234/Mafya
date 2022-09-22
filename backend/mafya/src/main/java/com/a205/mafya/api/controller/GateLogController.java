package com.a205.mafya.api.controller;

import com.a205.mafya.api.service.GateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gatelog")
public class GateLogController {
    static final String SUCCESS = "success";
    static final String FAIL = "fail";

    @Autowired
    private GateLogService gateLogService;

    @GetMapping(value = "/{userCode}")
    public ResponseEntity<?> inputLog(@PathVariable String userCode) {
        Boolean result = gateLogService.inputLog(userCode);

        if (result) return (new ResponseEntity<String>(SUCCESS, HttpStatus.OK));
        else        return (new ResponseEntity<String>(FAIL, HttpStatus.OK));
    }

}
