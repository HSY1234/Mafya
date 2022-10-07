package com.a205.mafya.api.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceTeamRes {
    private int id;

    private String name;

    private String userCode;

    private int status;

    private String teamCode;

    private String classCode;

    private String phoneNum;

    private boolean teamLeader;

    private int attendanceStatus;
}
