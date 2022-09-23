package com.a205.mafya.api.controller;

import com.a205.mafya.api.request.AddUserReq;
import com.a205.mafya.api.request.LoginReq;
import com.a205.mafya.api.request.ModifyUserReq;
import com.a205.mafya.api.response.*;
import com.a205.mafya.api.service.AuthService;
import com.a205.mafya.api.service.UserService;
import com.a205.mafya.db.dto.UserInfo;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.entity.Manager;
import com.a205.mafya.db.repository.ManagerRepository;
import com.a205.mafya.db.repository.UserRepository;
import com.a205.mafya.util.CookieProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/student")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ManagerRepository managerRepository;

    @Autowired
    CookieProvider cookieProvider;

    // 로그인
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginReq loginReq, HttpServletResponse resp){

        // jwt 생성
        String[] tokens = authService.login(loginReq);

        // 매니저인지 확인
        Optional<User> user = userRepository.findByUserCode(loginReq.getUserCode());
        Optional<Manager> manager = managerRepository.findByManagerCode(loginReq.getUserCode());

        String isManager = "";
        String teamCode = "";
        String classCode = "";
        if(manager.isPresent()){
            isManager = "Y";
            classCode = manager.get().getClassCode();
        }else{
            isManager = "N";
            teamCode = user.get().getTeamCode();
        }


        // accessToken은 responseEntity로 보내기
        LoginRes LR = LoginRes.builder()
                .accessToken(tokens[0])
                .msg("SUCCESS")
                .resultCode(0)
                .isManager(isManager)
                .classCode(classCode)
                .teamCode(teamCode)
                .build();
        // refreshToken은 HttpOnly cookie로 보내기
        cookieProvider.addTokenToCookie(resp,"refreshToken",tokens[1]);

        return new ResponseEntity<>(LR, HttpStatus.OK);
    }

    // 로그아웃
    @GetMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader(value="accessToken") String accessToken, HttpServletResponse rep){
        // refresh 토큰 제거
        Cookie cookie = new Cookie("refreshToken", "logout");
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 14); // 유효기간 2주
        // httpOnly 옵션을 추가해 서버만 쿠키에 접근할 수 있게 설정
        cookie.setHttpOnly(true);
        rep.addCookie(cookie);

        return new ResponseEntity<>("logout", HttpStatus.OK);
    }


    // 학생 추가
    @PostMapping("")
    public ResponseEntity<?> AddStudent(@RequestHeader(value="accessToken") String accessToken, @RequestBody AddUserReq userReq) throws Exception{

        userService.addUser(userReq);

        UserOneRes UOR = UserOneRes.builder()
                .userInfo(UserInfo.builder().userCode(userReq.getUserCode()).build())
                .msg("SUCCESS")
                .resultCode(0)
                .build();
        return new ResponseEntity<>(UOR, HttpStatus.OK);
    }

    // 학생 정보 지우기
    @DeleteMapping ("{id}")
    public ResponseEntity<?> DeleteStudent(@RequestHeader(value="accessToken") String accessToken, @PathVariable int id) throws Exception{

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
    public ResponseEntity<?> UpdateStudent(@RequestHeader(value="accessToken") String accessToken, @PathVariable int id, @RequestBody ModifyUserReq userReq) throws Exception{

        userService.modifyUser(id, userReq);

        BasicRes BR = BasicRes.builder()
                .msg("SUCCESS")
                // 0 : 요청한 사용자 있음
                .resultCode(0)
                .build();
        return new ResponseEntity<>(BR, HttpStatus.OK);
    }

    // 학생 정보 불러오기 By id
    @GetMapping ("id/{id}")
    public ResponseEntity<?> GetStudentInfoById(@RequestHeader(value="accessToken") String accessToken, @PathVariable int id) throws Exception{

        UserOneRes UOR = UserOneRes.builder()
                .userInfo(userService.findUserById(id))
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(UOR, HttpStatus.OK);
    }

    // 학생 정보 불러오기 By userCode
    @GetMapping ("userCode/{userCode}")
    public ResponseEntity<?> GetStudentInfoByUserCode(@RequestHeader(value="accessToken") String accessToken, @PathVariable String userCode) throws Exception{

        UserOneRes UOR = UserOneRes.builder()
                .userInfo(userService.findUserByUserCode(userCode))
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(UOR, HttpStatus.OK);
    }

    // 출석, 불출석 학생 리스트 불러오기
    @GetMapping ("attend")
    public ResponseEntity<?> GetAttendList(@RequestHeader(value="accessToken") String accessToken) throws Exception{

        List<UserInfo>[] attList =  userService.findAttendList();
        UserAttendRes UAR = UserAttendRes.builder()
                .attList(attList[0])
                .notAttList(attList[1])
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(UAR, HttpStatus.OK);
    }

    // 학생 리스트 불러오기 (페이지네이션)
    @GetMapping ("")

    public ResponseEntity<?> GetStudentList(@RequestHeader(value="accessToken") String accessToken, @PageableDefault(page = 0, size = 10, sort = "id")Pageable pageable) throws Exception{
        System.out.println(">>> processPaging : " + pageable);

        UserListRes ULR = UserListRes.builder()
                .userList(userService.findUserAll(pageable))
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(ULR, HttpStatus.OK);
    }

    // 학번 중복 검사
    @GetMapping ("checkId/{userCode}")
    public ResponseEntity<?> UserCodeOverLapCheck(@RequestHeader(value="accessToken") String accessToken, @PathVariable String userCode) throws Exception{
        userService.checkUserCodeOverlap(userCode);

        BasicRes BR = BasicRes.builder()
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(BR, HttpStatus.OK);
    }

}
