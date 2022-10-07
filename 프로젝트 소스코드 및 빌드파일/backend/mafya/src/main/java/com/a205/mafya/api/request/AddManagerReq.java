package com.a205.mafya.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddManagerReq {

    private String managerCode;
    private String classCode;

    private String name;
    private String password;
}
