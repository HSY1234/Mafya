package com.a205.mafya.api.model;

import javax.persistence.*;
//import java.util.Date;

@Entity
@Table(name = "test_user")
public class Tutorial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "user_code")
    private String user_code;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private int status;

    @Column(name = "team_code")
    private String team_code;

    @Column(name = "class_code")
    private String class_code;

    @Column(name = "phone_num")
    private String phone_num;

    @Column(name = "team_leader")
    private boolean team_leader;

    @Column(name = "created_at")
    //@Temporal(TemporalType.TIMESTAMP)
    private String created_at;

    @Column(name = "updated_at")
    //@Temporal(TemporalType.TIMESTAMP)
    private String updated_at;

    @Column(name = "absent")
    private int absent;

    @Column(name = "tardy")
    private int tardy;

    public Tutorial() {

    }

    public Tutorial(String name, String user_code, String password, int status, String team_code, String class_code,
                    String phone_num, boolean team_leader, String created_at, String updated_at, int absent, int tardy) {
        this.name = name;
        this.user_code = user_code;
        this.password = password;
        this.status = status;
        this.team_code = team_code;
        this.class_code = class_code;
        this.phone_num = phone_num;
        this.team_leader = team_leader;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.absent = absent;
        this.tardy = tardy;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUserCode() {
        return user_code;
    }
    public void setUserCode(String user_code) {
        this.user_code = user_code;
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

    public String getTeamCode() { return team_code; }
    public void setTeamCode(String team_code) { this.team_code = team_code; }

    public String getClassCode() { return class_code; }
    public void setClassCode(String class_code) { this.class_code = class_code; }

    public String getPhoneNum() { return phone_num; }
    public void setPhoneNum(String phone_num) { this.phone_num = phone_num; }

    public boolean isTeamLeader() { return team_leader; }
    public void setTeamLeader(boolean isTeamLeader) { this.team_leader = isTeamLeader(); }

    public String getCreatedAt() { return created_at; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }

    public String getUpdatedAt() {
        return updated_at;
    }
    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getAbsent() {
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
    }

    @Override
    public String toString() {
        return "Tutorial [id=" + id + ", name=" + name + ", user_code=" + user_code + ", password=" + password +
                ", status=" + status + ", team_code=" + team_code + ", class_code=" + class_code + ", phone_num=" + phone_num +
                ", team_leader=" + team_leader + ", created_at=" + created_at + ", updated_at=" + updated_at + ", absent=" + absent +
                ", tardy=" + tardy +
                "]";
    }
}
