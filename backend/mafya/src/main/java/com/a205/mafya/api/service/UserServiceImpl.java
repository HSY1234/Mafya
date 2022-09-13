package com.a205.mafya.api.service;

import com.a205.mafya.api.exception.UserCodeOverlapException;
import com.a205.mafya.api.repository.UserRepository;
import com.a205.mafya.api.request.AddUserReq;
import com.a205.mafya.api.request.ModifyUserReq;
import com.a205.mafya.api.response.UserInfoRes;
import com.a205.mafya.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public void addUser(AddUserReq userReq) throws Exception {
        if(userRepository.findByUserCode(userReq.getUserCode()).isPresent()){
            throw new UserCodeOverlapException("userCode already exist");
        }

        User user = User.builder()
                .name(userReq.getName())
                .userCode(userReq.getUserCode())
                .password(userReq.getPassword())
                // 회원가입 시 상태는 기본값으로
                // 기본값 뭐로 할지는 상의ㅁㅁㅁ
                .status(1)
                .teamCode(userReq.getTeamCode())
                .classCode(userReq.getClassCode())
                .phoneNum(userReq.getPhoneNum())
                .teamLeader(userReq.isTeamLeader())
                .build();

        userRepository.save(user);

    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws Exception {

    }

    @Override
    @Transactional
    public void modifyUser(Long id, ModifyUserReq userDto) throws Exception {

    }

    @Override
    @Transactional
    public UserInfoRes findUser(Long id) throws Exception {
        return null;
    }
}
