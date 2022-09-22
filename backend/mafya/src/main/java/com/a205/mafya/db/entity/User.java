package com.a205.mafya.db.entity;

import com.a205.mafya.api.request.ModifyUserReq;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames={"user_code"}))
public class User extends BaseEntity implements UserDetails {

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


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.userCode;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
