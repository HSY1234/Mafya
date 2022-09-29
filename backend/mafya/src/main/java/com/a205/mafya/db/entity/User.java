package com.a205.mafya.db.entity;

import com.a205.mafya.api.request.ModifyUserReq;
import lombok.*;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames={"user_code"}))
public class User extends BaseEntity {

    // [Kim JooHan ADD]
    //@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "id")
    // private int id;
    // BaseEntity에서 가져오기
    // [Kim JooHan END]
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

    //[Park SeHyeon Add]x
    @Column(name = "absent", nullable = false)
    private int absent;

    @Column(name = "tardy", nullable = false)
    private int tardy;

    @Override
    public void prePersist() {
        super.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        super.setUpdatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        this.absent = 0;
        this.tardy = 0;
    }
    //[Park SeHyeon end]

    public void modifyInfo(ModifyUserReq userReq){
        this.name = userReq.getName();
        this.teamCode = userReq.getTeamCode();
        this.classCode = userReq.getClassCode();
        this.phoneNum = userReq.getPhoneNum();
        this.teamLeader = userReq.isTeamLeader();
    }

    // [Kim JooHan ADD]
    /*public void tutorial() {

    }

    public void tutorial(String name, String user_code, String password, int status, String team_code, String class_code,
                         String phone_num, boolean team_leader, String created_at, String updated_at, int absent, int tardy) {
        this.name = name;
        this.userCode = user_code;
        this.password = password;
        this.status = status;
        this.teamCode = team_code;
        this.classCode = class_code;
        this.phoneNum = phone_num;
        this.teamLeader = team_leader;
        //this.created_at = created_at;
        //this.updated_at = updated_at;
        this.absent = absent;
        this.tardy = tardy;
    }*/

    /*public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }*/

    /*public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUserCode() {
        return userCode;
    }
    public void setUserCode(String user_code) {
        this.userCode = user_code;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public int getStatus() {
        return status;
    }
    public void setStatus(int status) { this.status = status; }

    public String getTeamCode() { return teamCode; }
    public void setTeamCode(String team_code) { this.teamCode = team_code; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String class_code) { this.classCode = class_code; }

    public String getPhoneNum() { return phoneNum; }
    public void setPhoneNum(String phone_num) { this.phoneNum = phone_num; }

    public boolean isTeamLeader() { return teamLeader; }
    public void setTeamLeader(boolean isTeamLeader) { this.teamLeader = isTeamLeader(); }*/

    //public String getCreatedAt() { return created_at; }
    //public void setCreatedAt(String created_at) { this.created_at = created_at; }

    /*public String getUpdatedAt() {
        return updated_at;
    }
    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }*/

    /*public int getAbsent() {
        return absent;
    }
    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getTardy() {
        return tardy;
    }
    public void setTardy(int tardy) {
        this.tardy = tardy;
    }*/

    @Override
    public String toString() {
        /*return "Tutorial [id=" + id + ", name=" + name + ", user_code=" + user_code + ", password=" + password +
                ", status=" + status + ", team_code=" + team_code + ", class_code=" + class_code + ", phone_num=" + phone_num +
                ", team_leader=" + team_leader + ", created_at=" + created_at + ", updated_at=" + updated_at + ", absent=" + absent +
                ", tardy=" + tardy +
                "]";*/
        return "Tutorial [name=" + name + ", user_code=" + userCode + ", password=" + password +
                ", status=" + status + ", team_code=" + teamCode + ", class_code=" + classCode + ", phone_num=" + phoneNum +
                ", team_leader=" + teamLeader + ", absent=" + absent +
                ", tardy=" + tardy +
                "]";
    }
    // [Kim JooHan END]

}
