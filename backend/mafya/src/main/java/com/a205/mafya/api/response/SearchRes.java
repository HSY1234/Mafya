package com.a205.mafya.api.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class SearchRes {
    private int id;

    private String name;

    private String userCode;

    private int status;

    private String teamCode;

    private String classCode;

    private String phoneNum;

    private boolean teamLeader;

    private int absent;

    private int trady;

    private String date;
}
