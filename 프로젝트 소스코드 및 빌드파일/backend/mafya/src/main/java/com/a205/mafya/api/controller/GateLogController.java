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

    /**
     * -2: 로그 입력 실패
     * -1: 존재 하지 않는 유저
     * 0: 입실
     * 10: 지각
     * 11: 지각 한 후 조퇴
     * 12: 지각 한 후 정상 퇴실
     * 2: 정상 입실 후 조퇴
     * 3: 정상 퇴실
     * 4: 입실 후 다시 입실 요청(요청 안 받고, 지각 시간 이후부터 다시 체크 가능)
     * 99: 지각 누적 3번으로 결석 처리
     */
    @GetMapping(value = "/{userCode}")
    public ResponseEntity<?> inputLog(@PathVariable String userCode) {
        int result = gateLogService.inputLog(userCode);

        return (new ResponseEntity<Integer>(result, HttpStatus.OK));
    }

}
