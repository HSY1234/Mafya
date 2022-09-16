package com.a205.mafya.api.response;

import com.a205.mafya.db.dto.UserInfo;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class UserListRes extends BasicRes {

    private List<UserInfo> userList;
}
