package com.a205.mafya.db.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {

    private int id;

    private String name;

    private String userCode;

    private int status;

    private String teamCode;

    private String classCode;

    private String phoneNum;

    private boolean teamLeader;

    //[Park SeHyeon Add]
    private int absent;

    private int trady;
    //[Park SeHyeon end];
}
