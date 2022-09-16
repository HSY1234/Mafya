package com.a205.mafya.api.controller;

import com.a205.mafya.api.request.AddUserReq;
import com.a205.mafya.api.request.ModifyUserReq;
import com.a205.mafya.api.response.BasicRes;
import com.a205.mafya.api.response.UserListRes;
import com.a205.mafya.api.response.UserOneRes;
import com.a205.mafya.api.service.UserService;
import com.a205.mafya.db.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/student")
public class UserController {

    @Autowired
    UserService userService;


    // 학생 추가, 이미지는 아직
    @PostMapping("")
    public ResponseEntity<?> AddStudent(@RequestBody AddUserReq userReq) throws Exception{

        userService.addUser(userReq);

        UserOneRes UOR = (UserOneRes) UserOneRes.builder()
                .userInfo(UserInfo.builder().userCode(userReq.getUserCode()).build())
                .msg("SUCCESS")
                .resultCode(0)
                .build();
        return new ResponseEntity<>(UOR, HttpStatus.OK);
    }

    // 학생 정보 지우기
    @DeleteMapping ("{id}")
    public ResponseEntity<?> DeleteStudent(@PathVariable int id) throws Exception{

        userService.deleteUser(id);

        BasicRes BR = BasicRes.builder()
                .msg("SUCCESS")
                // 0 : 요청한 사용자 있음
                .resultCode(0)
                .build();
        return new ResponseEntity<>(BR, HttpStatus.OK);
    }

    // 학생 정보 수정
    @PutMapping ("{id}")
    public ResponseEntity<?> UpdateStudent(@PathVariable int id, @RequestBody ModifyUserReq userReq) throws Exception{

        userService.modifyUser(id, userReq);

        BasicRes BR = BasicRes.builder()
                .msg("SUCCESS")
                // 0 : 요청한 사용자 있음
                .resultCode(0)
                .build();
        return new ResponseEntity<>(BR, HttpStatus.OK);
    }

    // 학생 정보 불러오기
    @GetMapping ("{id}")
    public ResponseEntity<?> GetStudentInfo(@PathVariable int id) throws Exception{

        UserOneRes UOR = (UserOneRes) UserOneRes.builder()
                .userInfo(userService.findUser(id))
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(UOR, HttpStatus.OK);
    }

    // 출석, 불출석 학생 리스트 불러오기
    @GetMapping ("attend")
    public ResponseEntity<?> GetAttendList() throws Exception{



        
        /// 미완ㅁㅁㅁㅁㅁㅁㅁㅁ





        return new ResponseEntity<>(, HttpStatus.OK);
    }

    // 학생 리스트 불러오기
    @GetMapping ("")
    public ResponseEntity<?> GetStudentList() throws Exception{

        UserListRes ULR = (UserListRes) UserListRes.builder()
                .userList(userService.findUserAll())
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(ULR, HttpStatus.OK);
    }

    // 학번 중복 검사
    @GetMapping ("checkId/{userCode}")
    public ResponseEntity<?> UserCodeOverLapCheck(@PathVariable String userCode) throws Exception{
        userService.checkUserCodeOverlap(userCode);

        BasicRes BR = BasicRes.builder()
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(BR, HttpStatus.OK);
    }

}
