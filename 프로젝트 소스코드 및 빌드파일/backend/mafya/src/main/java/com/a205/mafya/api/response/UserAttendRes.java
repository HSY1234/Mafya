package com.a205.mafya.api.response;

import com.a205.mafya.db.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserAttendRes extends BasicRes{

    private List<UserInfo> attList;
    private List<UserInfo> notAttList;
}
