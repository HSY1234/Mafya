package com.a205.mafya.api.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionRes {
    private String msg;
    private int codeNum;
}
