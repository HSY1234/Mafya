package com.a205.mafya.api.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AttendanceSituationRes {
     int trady; //해당 월 지각 수

     int absent; //해당 월 결석 수

     int totalDay;  //전체 수업일수

     int totalAttend; //참여 수업일수

     int money; //교육 지원금
}
