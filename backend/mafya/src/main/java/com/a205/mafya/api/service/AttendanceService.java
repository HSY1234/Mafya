package com.a205.mafya.api.service;

import com.a205.mafya.api.response.AttendanceTeamRes;

import java.util.List;

public interface AttendanceService {
    static final int enterTime = 9;  //오전 9시
    static final int exitTime = 18; //오후 6시

    int record(String userCode);

    List<AttendanceTeamRes> getTeamInfo(String teamCode);
}
