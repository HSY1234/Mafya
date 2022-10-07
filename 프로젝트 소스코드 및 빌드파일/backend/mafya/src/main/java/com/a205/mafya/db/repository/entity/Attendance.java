package com.a205.mafya.db.repository.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class Attendance extends BaseEntity {
    @Column(name = "type", nullable = false)
    private int type;

    @Column(name = "enter_time", nullable = false, length = 5)
    private String enterTime;

    @Column(name = "exit_time", nullable = false, length = 5)
    private String exitTime;

    @Column(name = "year", nullable = false, length = 4)
    private String year;

    @Column(name = "month", nullable = false, length = 2)
    private String month;

    @Column(name = "day", nullable = false, length = 2)
    private String day;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}

/*
[유저당 하루 한 개 씩 쌓임]
userCode / (User PK) 유저 식별자
type   ex) 0: 입실 10: 지각 11: 지각 한 후 조퇴 12: 지각 한 후 정상 퇴실  2: 조퇴 나가는 기록이 있었음(정상 퇴실x) 3: 정상퇴실
입실시간
퇴실시간
년
월
일
updated_at 단순 로그용
created_at  단순 로그용
 */
