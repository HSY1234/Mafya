package com.a205.mafya.api.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CalendarDataRes {
    String date;

    int type;

    String enterTime;

    String exitTime;
}
