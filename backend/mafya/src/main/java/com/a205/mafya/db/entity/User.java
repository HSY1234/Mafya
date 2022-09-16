package com.a205.mafya.db.entity;

import com.a205.mafya.api.request.ModifyUserReq;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User extends BaseEntity {
    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 20, name = "user_code")
    private String userCode;

    @Column(length = 200)
    private String password;

    private int status;

    @Column(length = 20, name = "team_code")
    private String teamCode;

    @Column(length = 20, name = "class_code")
    private String classCode;

    @Column(length = 20, name = "phone_num")
    private String phoneNum;

    @Column(name = "team_leader")
    private boolean teamLeader;

    public void modifyInfo(ModifyUserReq userReq){
        this.name = userReq.getName();
        this.teamCode = userReq.getTeamCode();
        this.classCode = userReq.getClassCode();
        this.phoneNum = userReq.getPhoneNum();
        this.teamLeader = userReq.isTeamLeader();
    }


}
