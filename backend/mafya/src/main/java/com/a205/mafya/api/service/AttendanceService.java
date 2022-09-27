package com.a205.mafya.api.service;

import com.a205.mafya.api.response.AttendanceSituationRes;
import com.a205.mafya.api.response.AttendanceTeamRes;
import com.a205.mafya.api.response.CalendarDataRes;
import com.a205.mafya.db.dto.UserInfo;

import java.util.List;

public interface AttendanceService {
    static final int ENTERTIME = 9;  //오전 9시
    static final int EXITTIME = 18; //오후 6시

    static final int NO_USER = -1;
    static final int ENTRANCE = 0;
    static final int TRADY = 10;
    static final int TRADY_AND_EARLYLEAVE = 11;
    static final int TRADY_AND_NORMALEXIT = 12;
    static final int ENTRANCE_AND_EARLYLEAVE = 2;
    static final int ENTRANCE_AND_NORMALEXIT = 3;
    static final int RE_REQUEST = 4;
    static final int ABSENT = 99;

    static final int TRANSFORM_ABSENT = 4;

    static final int DANGER = 3;

    static final int SALARY = 1000000;

    int record(String userCode);

    List<AttendanceTeamRes> getTeamInfo(String teamCode);

    void processAbsentScheduler();

    public void processTradyInitScheduler();

    void processTradyScheduler();

    List<CalendarDataRes> getCalendarData(String userCode);

    AttendanceSituationRes getSituationData(String userCode, String month);

    List<UserInfo> getDangerList(String classCode);

    List<UserInfo> getDangerClassInfo(String classCode);
}
