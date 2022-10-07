package com.a205.mafya.api.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchReq {
    String content;

    Boolean absentOrder;

    Boolean tradyOrder;
}
