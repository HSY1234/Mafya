package com.a205.mafya.api.controller;

import com.a205.mafya.api.filter.exception.TokenException;
import com.a205.mafya.api.filter.exception.WrongPasswordException;
import com.a205.mafya.api.request.AddUserReq;
import com.a205.mafya.api.request.LoginReq;
import com.a205.mafya.api.request.ModifyUserReq;
import com.a205.mafya.api.response.*;
import com.a205.mafya.api.service.AuthService;
import com.a205.mafya.api.service.TokenService;
import com.a205.mafya.api.service.UserService;
import com.a205.mafya.db.dto.UserInfo;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.entity.Manager;
import com.a205.mafya.db.repository.ManagerRepository;
import com.a205.mafya.db.repository.UserRepository;
import com.a205.mafya.util.CookieProvider;
import com.a205.mafya.util.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.NoSuchElementException;
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
    TokenProvider tokenProvider;
    @Autowired
    CookieProvider cookieProvider;
    @Autowired
    TokenService tokenService;

    // 로그인
    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody LoginReq loginReq, HttpServletResponse resp){

        log.info("security 통과 완료");

        // jwt 생성
        String accessToken = authService.login(loginReq, 'a');
        String refreshToken = authService.login(loginReq, 'r');

        // 매니저인지 확인
        Optional<User> user = userRepository.findByUserCode(loginReq.getUserCode());
        Optional<Manager> manager = managerRepository.findByManagerCode(loginReq.getUserCode());

        String userCode = "";
        String password = "";
        String isManager = "";
        String teamCode = "";
        String classCode = "";
        if(manager.isPresent()){
            userCode = manager.get().getManagerCode();
            password = manager.get().getPassword();
            isManager = "Y";
            classCode = manager.get().getClassCode();
        }else if(user.isPresent()){
            userCode = user.get().getUserCode();
            password = user.get().getPassword();
            isManager = "N";
            teamCode = user.get().getTeamCode();
        }else{
            throw new NoSuchElementException("no user exist");
        }

//        if(!password.equals(loginReq.getPassword())){
//            throw new WrongPasswordException("Wrong password");
//        }

        // accessToken은 responseEntity로 보내기
        LoginRes LR = LoginRes.builder()
                .accessToken(accessToken)
                .msg("SUCCESS")
                .resultCode(0)
                .isManager(isManager)
                .classCode(classCode)
                .teamCode(teamCode)
                .build();
        // refreshToken은 HttpOnly cookie로 보내기
        cookieProvider.addTokenToCookie(resp,"refreshToken",refreshToken);

        return new ResponseEntity<>(LR, HttpStatus.OK);
    }

    // 로그아웃
    @GetMapping("logout")
    public ResponseEntity<?> logout(@RequestHeader(value="accessToken") String accessToken, HttpServletRequest req, HttpServletResponse resp) throws TokenException {
//        //////jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        ////////////////////////////////////

        // refresh 토큰 제거
        cookieProvider.addTokenToCookie(resp,"refreshToken","logout");

        return new ResponseEntity<>("logout", HttpStatus.OK);
    }


    // 학생 추가
    @PostMapping("")
    public ResponseEntity<?> AddStudent(@RequestHeader(value="accessToken") String accessToken,HttpServletRequest req, @RequestBody AddUserReq userReq) throws Exception{

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
    public ResponseEntity<?> DeleteStudent(@RequestHeader(value="accessToken") String accessToken,HttpServletRequest req, @PathVariable int id) throws Exception{
//        //////jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        //////////////////////////////////


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
    public ResponseEntity<?> UpdateStudent(@RequestHeader(value="accessToken") String accessToken,HttpServletRequest req, @PathVariable int id, @RequestBody ModifyUserReq userReq) throws Exception{

//        //////jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        ///////////////////////////////////


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
    public ResponseEntity<?> GetStudentInfoById(@RequestHeader(value="accessToken") String accessToken, HttpServletRequest req,@PathVariable int id) throws Exception{

//        //////jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        ///////////////////////////////////


        UserOneRes UOR = UserOneRes.builder()
                .userInfo(userService.findUserById(id))
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(UOR, HttpStatus.OK);
    }

    // 학생 정보 불러오기 By userCode
    @GetMapping ("userCode/{userCode}")
    public ResponseEntity<?> GetStudentInfoByUserCode(@RequestHeader(value="accessToken") String accessToken,HttpServletRequest req, @PathVariable String userCode) throws Exception{
//        //////jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        //////////////////////////////////

        UserOneRes UOR = UserOneRes.builder()
                .userInfo(userService.findUserByUserCode(userCode))
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(UOR, HttpStatus.OK);
    }

    // 출석, 불출석 학생 리스트 불러오기
    @GetMapping ("attend")
    public ResponseEntity<?> GetAttendList(@RequestHeader(value="accessToken") String accessToken,HttpServletRequest req) throws Exception{

//        //////jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        ///////////////////////////////////


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
    public ResponseEntity<?> GetStudentList(@RequestHeader(value="accessToken") String accessToken,HttpServletRequest req, @PageableDefault(page = 0, size = 10, sort = "id")Pageable pageable) throws Exception{

        System.out.println(">>> processPaging : " + pageable);

//        //////jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        ///////////////////////////////////


        UserListRes ULR = UserListRes.builder()
                .userList(userService.findUserAll(pageable))
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(ULR, HttpStatus.OK);
    }

    // 학번 중복 검사
    @GetMapping ("checkId/{userCode}")
    public ResponseEntity<?> UserCodeOverLapCheck(@RequestHeader(value="accessToken") String accessToken,HttpServletRequest req, @PathVariable String userCode) throws Exception{
        userService.checkUserCodeOverlap(userCode);

//        ////// jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        ///////////////////////////////////

        BasicRes BR = BasicRes.builder()
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(BR, HttpStatus.OK);
    }

    //[Park SeHyoen Add]
    @GetMapping("classCode/{classCode}")
    public ResponseEntity<?> GetStudentListByClassCode(@RequestHeader(value="accessToken") String accessToken,HttpServletRequest req, @PageableDefault(page = 0, size = 10, sort = "id")Pageable pageable, @PathVariable String classCode) throws Exception{
        System.out.println(">>> GetStudentListByClassCode : " + pageable + "   classCode : " + classCode);

//        ////// jwt 적용 코드 /////////////////
//        tokenService.TokenValidation(accessToken,tokenProvider.resolveRefreshToken(req));
//        ///////////////////////////////////

        UserListRes ULR = UserListRes.builder()
                .userList(userService.findUserAllByClassCode(pageable, classCode))
                .msg("SUCCESS")
                .resultCode(0)
                .build();

        return new ResponseEntity<>(ULR, HttpStatus.OK);
    }
    //[Park SeHyeon End]
}
