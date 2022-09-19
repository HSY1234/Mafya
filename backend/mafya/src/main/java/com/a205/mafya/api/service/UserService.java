package com.a205.mafya.api.service;

import com.a205.mafya.api.request.AddUserReq;
import com.a205.mafya.api.request.ModifyUserReq;
import com.a205.mafya.db.dto.UserInfo;

import java.util.List;

public interface UserService {
    public void addUser(AddUserReq user) throws Exception;
    public void deleteUser(int id) throws Exception;
    public void modifyUser(int id, ModifyUserReq userDto) throws Exception;
    public UserInfo findUserById(int id) throws Exception;
    public UserInfo findUserByUserCode(String userCode) throws Exception;
    public List<UserInfo> [] findAttendList() throws Exception;
    public List<UserInfo> findAttendUsersByClassCode(String classCode) throws Exception;
    public List<UserInfo> findUserAll() throws Exception;

    public void checkUserCodeOverlap(String userCode) throws Exception;

}
