package com.a205.mafya.db.entity;

import com.a205.mafya.api.request.ModifyUserReq;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames={"user_code"}))
public class User extends BaseEntity {

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "user_code", nullable = false, length = 20)
    private String userCode;

    @Column(name = "password", nullable = false, length = 200)
    private String password;

    @Column(name = "status", length = 200)
    private int status;

    @Column( name = "team_code", length = 20)
    private String teamCode;

    @Column(name = "class_code", length = 20)
    private String classCode;

    @Column(name = "phone_num", length = 20)
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
