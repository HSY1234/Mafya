package com.a205.mafya.api.controller;

import com.a205.mafya.api.response.AttendanceTeamRes;
import com.a205.mafya.api.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
