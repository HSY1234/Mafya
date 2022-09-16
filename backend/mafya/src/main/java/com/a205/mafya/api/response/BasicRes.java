package com.a205.mafya.api.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BasicRes {
    private String msg;
    private int resultCode;
}

