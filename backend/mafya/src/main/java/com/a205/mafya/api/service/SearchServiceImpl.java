package com.a205.mafya.api.service;

import com.a205.mafya.api.request.SearchReq;
import com.a205.mafya.api.response.SearchRes;
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

    public SearchRes convertSearchRes(User user, Date date) {
        SearchRes searchRes = SearchRes.builder()
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
                .date(date.getMonth() + "/" + date.getDay())
                .build();
        return (searchRes);
    }

    public Date getDate() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Date date = new Date();

        date.setYear(now.getYear() + "");
        date.setMonth(now.getMonthValue() < 10 ? "0" + now.getMonthValue() : now.getMonthValue() + "");
        date.setDay(now.getDayOfMonth() < 10 ? "0" + now.getDayOfMonth() : now.getDayOfMonth() + "");

        return (date);
    }

    public Date makeDate(int day, int month) {
        Date date = new Date();

        date.setYear("2022");
        date.setMonth(month < 10 ? "0" + month : month + "");
        date.setDay(day < 10 ? "0" + day : day + "");

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

    List<SearchRes> addAbsentUserInfoListByUserAndDate(List<SearchRes> searchResList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {      //안왔으면 결석(당일 하루에만 해당)
                searchResList.add(convertSearchRes(userList.get(i), date));
                System.out.println(">>> " + userList.get(i));
                continue;
            }

            System.out.println("[A]>>> " + attendance.get());

            if (attendance.get().getType() == 99) { //과거
                searchResList.add(convertSearchRes(userList.get(i), date));
            }
        }

        return (searchResList);
    }

    List<SearchRes> addTradyUserInfoListByUserAndDate(List<SearchRes> searchResList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {  //기록이 없다? 이건 에러임
                System.out.println(">>> " + userList.get(i) + "   [ERROR]");
                continue;
            }

            if (attendance.get().getType() == 10 || attendance.get().getType() == 11 || attendance.get().getType() == 12 || attendance.get().getType() == 2) {
                searchResList.add(convertSearchRes(userList.get(i), date));
            }
        }

        return (searchResList);
    }

    List<SearchRes> addTeamAndClassUserInfoListByUserAndDate(List<SearchRes> searchResList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {  //기록이 없다? 이건 에러임
                System.out.println(">>> " + userList.get(i) + "   [ERROR2]");
                continue;
            }

            searchResList.add(convertSearchRes(userList.get(i), date));
        }

        return (searchResList);
    }

    List<SearchRes> addNameUserInfoListByUserAndDate(List<SearchRes> userInfoList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {  //기록이 없다? 이건 에러임
                System.out.println(">>> " + userList.get(i) + "   [ERROR3]");
                continue;
            }

            userInfoList.add(convertSearchRes(userList.get(i), date));
        }

        return (userInfoList);
    }

    @Transactional
    List<SearchRes> getAbsentUserInfo(SearchReq searchReq) {
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = searchReq.getContent().split(" ");
        Arrays.fill(refClass, false);

        for (String word : words) {     //단어 분석
            if ("결석".equals(word))   continue;
            else if (word.contains("/")) {
                StringTokenizer st = new StringTokenizer(word,"/");
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                Date date = makeDate(day, month);
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

            String team = teamCode.get(i).toUpperCase();

            userList = addUserListByTeamCode(userList, team);
        }

        //3페이즈 (검색된 유저 리스트로 결석 인원 체크)
        if (dateConfig > 0) {   //지정한 날짜가 있으면
            for (int i = 0; i < dateConfig; i++) {
                System.out.println(dates.get(i));
                searchResList = addAbsentUserInfoListByUserAndDate(searchResList, userList, dates.get(i));
            }
        }
        else  //default 오늘 하루
            searchResList = addAbsentUserInfoListByUserAndDate(searchResList, userList, getDate());

        //4페이즈 (정렬)
        if (searchReq.getAbsentOrder())
            Collections.sort(searchResList, comparatorAbsent);
        else if (searchReq.getTradyOrder())
            Collections.sort(searchResList, comparatorTrady);
        else
            Collections.sort(searchResList, comparatorDefault);


        return (searchResList);
    }

    @Transactional
    List<SearchRes> getTradyUserInfo(SearchReq searchReq) {
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = searchReq.getContent().split(" ");
        Arrays.fill(refClass, false);

        for (String word : words) {     //단어 분석
            if ("지각".equals(word))   continue;
            else if (word.contains("/")) {
                StringTokenizer st = new StringTokenizer(word,"/");
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                Date date = makeDate(day, month);
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

            String team = teamCode.get(i).toUpperCase();

            userList = addUserListByTeamCode(userList, team);
        }

        //3페이즈 (검색된 유저 리스트로 결석 인원 체크)
        if (dateConfig > 0) {   //지정한 날짜가 있으면
            for (int i = 0; i < dateConfig; i++) {
                System.out.println(dates.get(i));
                searchResList = addTradyUserInfoListByUserAndDate(searchResList, userList, dates.get(i));
            }
        }
        else  //default 오늘 하루
            searchResList = addTradyUserInfoListByUserAndDate(searchResList, userList, getDate());

        //4페이즈 (정렬)
        if (searchReq.getAbsentOrder())
            Collections.sort(searchResList, comparatorAbsent);
        else if (searchReq.getTradyOrder())
            Collections.sort(searchResList, comparatorTrady);
        else
            Collections.sort(searchResList, comparatorDefault);


        return (searchResList);
    }


    @Transactional
    List<SearchRes> getTeamAndClassCodeUserInfo(SearchReq searchReq) {
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = searchReq.getContent().split(" ");
        Arrays.fill(refClass, false);

        for (String word : words) {     //단어 분석
            if (word.contains("/")) {
                StringTokenizer st = new StringTokenizer(word,"/");
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                Date date = makeDate(day, month);
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
        System.out.println(getDate());

        //2페이즈 (유저 리스트 가져오기)
        for (int i = 0; i < classCode.size(); i++) {
            userList = addUserListByClassCode(userList, classCode.get(i));
        }
        for (int i = 0; i < teamCode.size(); i++) {
            int code = teamCode.get(i).charAt(1) - '0';

            if (refClass[code]) continue;       //팀이 앞서 찾아온 반에 속해 있을 시 건너 뜀

            String team = teamCode.get(i).toUpperCase();

            userList = addUserListByTeamCode(userList, team);
        }

        //3페이즈 (검색된 유저 리스트로 결석 인원 체크)
        if (dateConfig > 0) {   //지정한 날짜가 있으면
            for (int i = 0; i < dateConfig; i++) {
                System.out.println(dates.get(i));
                searchResList = addTeamAndClassUserInfoListByUserAndDate(searchResList, userList, dates.get(i));
            }
        }
        else  //default 오늘 하루
            searchResList = addTeamAndClassUserInfoListByUserAndDate(searchResList, userList, getDate());

        //4페이즈 (정렬)
        if (searchReq.getAbsentOrder())
            Collections.sort(searchResList, comparatorAbsent);
        else if (searchReq.getTradyOrder())
            Collections.sort(searchResList, comparatorTrady);
        else
            Collections.sort(searchResList, comparatorDefault);


        return (searchResList);
    }

    @Transactional
    List<SearchRes> getNameCodeUserInfo(SearchReq searchReq) {
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> names = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = searchReq.getContent().split(" ");
        Arrays.fill(refClass, false);

        for (String word : words) {     //단어 분석
            if (word.contains("/")) {
                StringTokenizer st = new StringTokenizer(word,"/");
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                Date date = makeDate(day, month);
                dates.add(date);
                dateConfig++;
            }
            else
                names.add(word);
        }

        //2페이즈 (유저 리스트 가져오기)
        for (int i = 0; i < names.size(); i++) {
            List<User> tmp = userRepository.findAllByName(names.get(i));

            for (int j = 0; j < tmp.size(); j++)
                userList.add(tmp.get(j));
        }

        //3페이즈 (검색된 유저 리스트로 결석 인원 체크)
        if (dateConfig > 0) {   //지정한 날짜가 있으면
            for (int i = 0; i < dateConfig; i++) {
                System.out.println(dates.get(i));
                searchResList = addNameUserInfoListByUserAndDate(searchResList, userList, dates.get(i));
            }
        }
        else  //default 오늘 하루
            searchResList = addNameUserInfoListByUserAndDate(searchResList, userList, getDate());

        //4페이즈 (정렬)
        if (searchReq.getAbsentOrder())
            Collections.sort(searchResList, comparatorAbsent);
        else if (searchReq.getTradyOrder())
            Collections.sort(searchResList, comparatorTrady);
        else
            Collections.sort(searchResList, comparatorDefault);


        return (searchResList);
    }

    @Override
    public List<SearchRes> doIntegratedSearch(SearchReq searchReq) {
        int flag = analyzeContent(searchReq.getContent());

        if (flag == ERROR)  return (new LinkedList<>());
        else if (flag == ABSENT) return (getAbsentUserInfo(searchReq));
        else if (flag == TRADY) return (getTradyUserInfo(searchReq));
        else if (flag == CLASS_AND_TEAM_CODE) return (getTeamAndClassCodeUserInfo(searchReq));
        else return (getNameCodeUserInfo(searchReq));
//        else return (null);
    }

    Comparator<SearchRes> comparatorAbsent = new Comparator<SearchRes>() {
        @Override
        public int compare(SearchRes a, SearchRes b) {
            return (b.getAbsent() - a.getAbsent());
        }
    };

    Comparator<SearchRes> comparatorTrady = new Comparator<SearchRes>() {
        @Override
        public int compare(SearchRes a, SearchRes b) {
            return (b.getTrady() - a.getTrady());
        }
    };

    Comparator<SearchRes> comparatorDefault = new Comparator<SearchRes>() {
        @Override
        public int compare(SearchRes a, SearchRes b) { return (a.getId() - b.getId()); }
    };
}
