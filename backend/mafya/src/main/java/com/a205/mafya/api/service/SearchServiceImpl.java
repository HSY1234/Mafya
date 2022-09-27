package com.a205.mafya.api.service;

import com.a205.mafya.api.request.SearchReq;
import com.a205.mafya.db.dto.Date;
import com.a205.mafya.db.dto.UserInfo;
import com.a205.mafya.db.entity.Attendance;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.AttendanceRepository;
import com.a205.mafya.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

    public UserInfo convertUserInfo(User user) {
        UserInfo userInfo = UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .userCode(user.getUserCode())
                .status(user.getStatus())
                .teamCode(user.getTeamCode())
                .classCode(user.getClassCode())
                .phoneNum(user.getPhoneNum())
                .teamLeader(user.isTeamLeader())
                .absent(user.getAbsent())
                .trady(user.getTardy())
                .build();
        return (userInfo);
    }

    public Date getDate() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Date date = new Date();

        date.setYear(now.getYear() + "");
        date.setMonth(now.getMonthValue() < 10 ? "0" + now.getMonthValue() : now.getMonthValue() + "");
        date.setDay(now.getDayOfMonth() < 10 ? "0" + now.getDayOfMonth() : now.getDayOfMonth() + "");

        return (date);
    }

    /**
     *
     * @param content
     * @return
     * -1: 처리 불가
     * 1: 결석
     * 2: 지각
     * 3: 반코드 및 팀코드
     * 4(기타): 이름
     */
    private int analyzeContent(String content) {
        if ("".equals(content) || content == null)  return (ERROR);
        else if (content.contains("결석"))    return (ABSENT);
        else if (content.contains("지각"))    return (TRADY);
        else if (content.matches(".*[a-zA-Z].*"))   return (CLASS_AND_TEAM_CODE);
        else if (content.matches(".*[0-9].*"))      return (CLASS_AND_TEAM_CODE);
        else    return (NAME);
    }

    List<User> addUserListByClassCode(List<User> userList, String classCode) {
        List<User> tmp = userRepository.findAllByClassCode(classCode);

        for (int i = 0; i < tmp.size(); i++)    userList.add(tmp.get(i));
        return (userList);
    }

    List<User> addUserListByTeamCode(List<User> userList, String teamCode) {
        List<User> tmp = userRepository.findAllByTeamCode(teamCode);

        for (int i = 0; i < tmp.size(); i++)    userList.add(tmp.get(i));
        return (userList);
    }

    List<UserInfo> addAbsentUserInfoListByUserAndDate(List<UserInfo> userInfoList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {      //안왔으면 결석(당일 하루에만 해당)
                userInfoList.add(convertUserInfo(userList.get(i)));
                System.out.println(">>> " + userList.get(i));
                continue;
            }

            System.out.println("[A]>>> " + attendance.get());

            if (attendance.get().getType() == 99) { //과거
                userInfoList.add(convertUserInfo(userList.get(i)));
            }
        }

        return (userInfoList);
    }

    List<UserInfo> addTradyUserInfoListByUserAndDate(List<UserInfo> userInfoList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {  //기록이 없다? 이건 에러임
                System.out.println(">>> " + userList.get(i) + "   [ERROR]");
                continue;
            }

            if (attendance.get().getType() == 10 || attendance.get().getType() == 11 || attendance.get().getType() == 12 || attendance.get().getType() == 2) {
                userInfoList.add(convertUserInfo(userList.get(i)));
            }
        }

        return (userInfoList);
    }

    List<UserInfo> addTeamAndClassUserInfoListByUserAndDate(List<UserInfo> userInfoList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {  //기록이 없다? 이건 에러임
                System.out.println(">>> " + userList.get(i) + "   [ERROR2]");
                continue;
            }

            userInfoList.add(convertUserInfo(userList.get(i)));
        }

        return (userInfoList);
    }

        //결석을 하더라도 팀코드랑 반이 있는지 확인해봐야함
    @Transactional
    List<UserInfo> getAbsentUserInfo(SearchReq searchReq) {
        List<UserInfo> userInfoList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = searchReq.getContent().split(" ");

        for (String word : words) {     //단어 분석
            if ("결석".equals(word))   continue;
            else if (word.contains("/")) {
                Date date = new Date();
                StringTokenizer st = new StringTokenizer(word,"/");

                date.setYear("2022");
                date.setMonth(st.nextToken());
                date.setDay(st.nextToken());
                dates.add(date);
                dateConfig++;
            }
            else if (word.matches("^[0-9].*")) {
                classCode.add(word.substring(0, 1));
                    refClass[Integer.parseInt(word.substring(0, 1))] = true;
            }
            else if (word.matches("^[a-zA-Z].*"))  teamCode.add(word);
        }

        System.out.println(">>> teamCode :" + teamCode + "   classCode : " + classCode + "    Dates : " + dates + "    dateConfig : " + dateConfig);


        //2페이즈 (유저 리스트 가져오기)
        for (int i = 0; i < classCode.size(); i++) {
            System.out.println(">> classcode : " + classCode.get(i));
            userList = addUserListByClassCode(userList, classCode.get(i));
        }
        for (int i = 0; i < teamCode.size(); i++) {
            int code = teamCode.get(i).charAt(1) - '0';

//            System.out.println(">> teamcode :" + teamCode.get(i));

            if (refClass[code]) continue;       //팀이 앞서 찾아온 반에 속해 있을 시 건너 뜀

            userList = addUserListByTeamCode(userList, teamCode.get(i));
        }

        //3페이즈 (검색된 유저 리스트로 결석 인원 체크)
        if (dateConfig > 0) {   //지정한 날짜가 있으면
            for (int i = 0; i < dateConfig; i++) {
                System.out.println(dates.get(i));
                userInfoList = addAbsentUserInfoListByUserAndDate(userInfoList, userList, dates.get(i));
            }
        }
        else  //default 오늘 하루
            userInfoList = addAbsentUserInfoListByUserAndDate(userInfoList, userList, getDate());

        //4페이즈 (정렬)
        if (searchReq.getAbsentOrder()) {
            Collections.sort(userInfoList, comparatorAbsent);
            return (userInfoList);
        }
        else if (searchReq.getTradyOrder()) {
            Collections.sort(userInfoList, comparatorTrady);
            return (userInfoList);
        }
        else{
            Collections.sort(userInfoList, comparatorDefault);
            return (userInfoList);
        }
    }

    @Transactional
    List<UserInfo> getTradyUserInfo(SearchReq searchReq) {
        List<UserInfo> userInfoList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = searchReq.getContent().split(" ");

        for (String word : words) {     //단어 분석
            if ("지각".equals(word))   continue;
            else if (word.contains("/")) {
                Date date = new Date();
                StringTokenizer st = new StringTokenizer(word,"/");

                date.setYear("2022");
                date.setMonth(st.nextToken());
                date.setDay(st.nextToken());
                dates.add(date);
                dateConfig++;
            }
            else if (word.matches("^[0-9].*")) {
                classCode.add(word.substring(0, 1));
                refClass[Integer.parseInt(word.substring(0, 1))] = true;
            }
            else if (word.matches("^[a-zA-Z].*"))  teamCode.add(word);
        }

        System.out.println(">>> teamCode :" + teamCode + "   classCode : " + classCode + "    Dates : " + dates + "    dateConfig : " + dateConfig);

        //2페이즈 (유저 리스트 가져오기)
        for (int i = 0; i < classCode.size(); i++) {
            userList = addUserListByClassCode(userList, classCode.get(i));
        }
        for (int i = 0; i < teamCode.size(); i++) {
            int code = teamCode.get(i).charAt(1) - '0';

            if (refClass[code]) continue;       //팀이 앞서 찾아온 반에 속해 있을 시 건너 뜀

            userList = addUserListByTeamCode(userList, teamCode.get(i));
        }

        //3페이즈 (검색된 유저 리스트로 결석 인원 체크)
        if (dateConfig > 0) {   //지정한 날짜가 있으면
            for (int i = 0; i < dateConfig; i++) {
                System.out.println(dates.get(i));
                userInfoList = addTradyUserInfoListByUserAndDate(userInfoList, userList, dates.get(i));
            }
        }
        else  //default 오늘 하루
            userInfoList = addTradyUserInfoListByUserAndDate(userInfoList, userList, getDate());

        //4페이즈 (정렬)
        if (searchReq.getAbsentOrder()) {
            Collections.sort(userInfoList, comparatorAbsent);
            return (userInfoList);
        }
        else if (searchReq.getTradyOrder()) {
            Collections.sort(userInfoList, comparatorTrady);
            return (userInfoList);
        }
        else{
            Collections.sort(userInfoList, comparatorDefault);
            return (userInfoList);
        }
    }


    @Transactional
    List<UserInfo> getTeamAndClassCodeUserInfo(SearchReq searchReq) {
        List<UserInfo> userInfoList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = searchReq.getContent().split(" ");

        for (String word : words) {     //단어 분석
            if (word.contains("/")) {
                Date date = new Date();
                StringTokenizer st = new StringTokenizer(word,"/");

                date.setYear("2022");
                date.setMonth(st.nextToken());
                date.setDay(st.nextToken());
                dates.add(date);
                dateConfig++;
            }
            else if (word.matches("^[0-9].*")) {
                classCode.add(word.substring(0, 1));
                refClass[Integer.parseInt(word.substring(0, 1))] = true;
            }
            else if (word.matches("^[a-zA-Z].*"))  teamCode.add(word);
        }

        System.out.println(">>> teamCode :" + teamCode + "   classCode : " + classCode + "    Dates : " + dates + "    dateConfig : " + dateConfig);

        //2페이즈 (유저 리스트 가져오기)
        for (int i = 0; i < classCode.size(); i++) {
            userList = addUserListByClassCode(userList, classCode.get(i));
        }
        for (int i = 0; i < teamCode.size(); i++) {
            int code = teamCode.get(i).charAt(1) - '0';

            if (refClass[code]) continue;       //팀이 앞서 찾아온 반에 속해 있을 시 건너 뜀

            userList = addUserListByTeamCode(userList, teamCode.get(i));
        }

        //3페이즈 (검색된 유저 리스트로 결석 인원 체크)
        if (dateConfig > 0) {   //지정한 날짜가 있으면
            for (int i = 0; i < dateConfig; i++) {
                System.out.println(dates.get(i));
                userInfoList = addTeamAndClassUserInfoListByUserAndDate(userInfoList, userList, dates.get(i));
            }
        }
        else  //default 오늘 하루
            userInfoList = addTeamAndClassUserInfoListByUserAndDate(userInfoList, userList, getDate());

        //4페이즈 (정렬)
        if (searchReq.getAbsentOrder()) {
            Collections.sort(userInfoList, comparatorAbsent);
            return (userInfoList);
        }
        else if (searchReq.getTradyOrder()) {
            Collections.sort(userInfoList, comparatorTrady);
            return (userInfoList);
        }
        else{
            Collections.sort(userInfoList, comparatorDefault);
            return (userInfoList);
        }
    }

    @Override
    public List<UserInfo> doIntegratedSearch(SearchReq searchReq) {
        int flag = analyzeContent(searchReq.getContent());

        if (flag == ERROR)  return (new LinkedList<>());
        else if (flag == ABSENT) return (getAbsentUserInfo(searchReq));
        else if (flag == TRADY) return (getTradyUserInfo(searchReq));
        else if (flag == CLASS_AND_TEAM_CODE) return (getTeamAndClassCodeUserInfo(searchReq));
        else return (null);
    }

    Comparator<UserInfo> comparatorAbsent = new Comparator<UserInfo>() {
        @Override
        public int compare(UserInfo a, UserInfo b) {
            return (b.getAbsent() - a.getAbsent());
        }
    };

    Comparator<UserInfo> comparatorTrady = new Comparator<UserInfo>() {
        @Override
        public int compare(UserInfo a, UserInfo b) {
            return (b.getTrady() - a.getTrady());
        }
    };

    Comparator<UserInfo> comparatorDefault = new Comparator<UserInfo>() {
        @Override
        public int compare(UserInfo a, UserInfo b) {
            return (a.getId() - b.getId());
        }
    };
}
