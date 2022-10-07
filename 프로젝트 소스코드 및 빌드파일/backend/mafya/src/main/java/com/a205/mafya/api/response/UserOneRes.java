package com.a205.mafya.api.response;

import com.a205.mafya.db.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserOneRes extends BasicRes {

    private UserInfo userInfo;

}
