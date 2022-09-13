package com.a205.mafya.api.service;

import com.a205.mafya.api.request.AddUserReq;
import com.a205.mafya.api.request.ModifyUserReq;
import com.a205.mafya.api.response.UserInfoRes;

public interface UserService {
    public void addUser(AddUserReq user) throws Exception;
    public void deleteUser(Long id) throws Exception;
    public void modifyUser(Long id, ModifyUserReq userDto) throws Exception;
    public UserInfoRes findUser(Long id) throws Exception;
}
