package com.a205.mafya.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LoginRes extends BasicRes{

    private String accessToken;
    private String isManager;
    private String classCode;
    private String teamCode;
}
