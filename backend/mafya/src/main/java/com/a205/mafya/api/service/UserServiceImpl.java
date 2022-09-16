package com.a205.mafya.api.service;

import com.a205.mafya.api.exception.UserCodeOverlapException;
import com.a205.mafya.db.repository.UserRepository;
import com.a205.mafya.api.request.AddUserReq;
import com.a205.mafya.api.request.ModifyUserReq;
import com.a205.mafya.db.dto.UserInfo;
import com.a205.mafya.db.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public void addUser(AddUserReq userReq) throws Exception {
        checkUserCodeOverlap(userReq.getUserCode());

        User user = User.builder()
                .name(userReq.getName())
                .userCode(userReq.getUserCode())
                // 회원가입 시 상태는 기본값으로
                // status(0) : 입실
                // status(1) : 입실안함
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
    public void deleteUser(int id) throws Exception {
        if(!userRepository.findById(id).isPresent()){
            throw new NoSuchElementException("Not existent id");
        }

        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void modifyUser(int id, ModifyUserReq userReq) throws Exception {
        Optional<User> opUser = userRepository.findById(id);
        if(!opUser.isPresent()){
            throw new NoSuchElementException("Not existent id");
        }
        User user = opUser.get();

        user.modifyInfo(userReq);

        userRepository.save(user);

    }

    @Override
    @Transactional
    public UserInfo findUser(int id) throws Exception {
        Optional<User> opUser =userRepository.findById(id);
        if(!opUser.isPresent()){
            throw new NoSuchElementException("Not existent id");
        }

        User user = opUser.get();


        return UserToUserInfo(user);
    }

    @Override
    public List<UserInfo>[] findAttendList() throws Exception {
        List<User> userList = userRepository.findAll();
        List<UserInfo> userAttend = new ArrayList<>();
        List<UserInfo> userNotAttend = new ArrayList<>();
        for(User user : userList){
            // 출석
            if(user.getStatus() == 0){
                userAttend.add(UserToUserInfo(user));
            }else if(user.getStatus() == 1){
                userNotAttend.add(UserToUserInfo(user));
            }
        }

        // 미완ㅁㅁㅁㅁ





        return new List[0];
    }

    @Override
    public List<UserInfo> findAttendUsersByClassCode(String classCode) throws Exception {
        List<User> userList = userRepository.findAllByClassCodeAndStatus(classCode, 0);
        if(userList.isEmpty()){
            throw new NoSuchElementException("No Student exist");
        }
        List<UserInfo> uirList = new ArrayList<>();
        for( User user : userList) {

            uirList.add(UserToUserInfo(user));
        }

        return uirList;
    }

    @Override
    public List<UserInfo> findUserAll() throws Exception {
        List<User> userList = userRepository.findAll();
        if(userList.isEmpty()){
            throw new NoSuchElementException("No Student exist");
        }

        List<UserInfo> uirList = new ArrayList<>();
        for( User user : userList) {

            uirList.add(UserToUserInfo(user));
        }

        return uirList;
    }

    @Override
    public void checkUserCodeOverlap(String userCode) throws Exception {
        if(userRepository.findByUserCode(userCode).isPresent()){
            throw new UserCodeOverlapException("userCode already exist");
        }
    }

    public UserInfo UserToUserInfo(User user) throws Exception {
        UserInfo uir = UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .userCode(user.getUserCode())
                .status(user.getStatus())
                .teamCode(user.getTeamCode())
                .classCode(user.getClassCode())
                .phoneNum(user.getPhoneNum())
                .teamLeader(user.isTeamLeader())
                .build();
        return uir;
    }

}
