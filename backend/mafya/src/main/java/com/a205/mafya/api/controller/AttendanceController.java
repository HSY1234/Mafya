package com.a205.mafya.api.controller;

import com.a205.mafya.api.response.AttendanceTeamRes;
import com.a205.mafya.api.service.AttendanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.List;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    @Autowired
    AttendanceService attendanceService;

    @GetMapping("/team/{teamCode}")
    public ResponseEntity<?> getTeamStatus(@PathVariable String teamCode) {
        List<AttendanceTeamRes> attendanceTeamResList = attendanceService.getTeamInfo(teamCode);

        return (new ResponseEntity<List<AttendanceTeamRes>>(attendanceTeamResList, HttpStatus.OK));
    }

    @GetMapping("/absent")
    @ApiOperation(value = "자동 결석 처리(평일 18시 31에 실행)", notes = "직접 실행하지 말 것")
    public ResponseEntity<?> processAbsent() {
        attendanceService.processAbsentScheduler();
        return (new ResponseEntity<Void>(HttpStatus.OK));
    }

    @GetMapping("/trady")
    @ApiOperation(value = "자동 지각 처리(평일 18시 31에 실행)", notes = "직접 실행하지 말 것")
    public ResponseEntity<?> processTrady() {
        attendanceService.processTradyScheduler();
        return (new ResponseEntity<Void>(HttpStatus.OK));
    }

    @GetMapping("/initTrady")
    @ApiOperation(value = "자동 지각 초기화(매월 1일 0시에 실행)", notes = "직접 실행하지 말 것")
    public ResponseEntity<?> processInitTrady() {
        attendanceService.processTradyInitScheduler();
        return (new ResponseEntity<Void>(HttpStatus.OK));
    }
}
