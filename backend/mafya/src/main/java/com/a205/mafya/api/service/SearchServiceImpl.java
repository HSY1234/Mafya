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

    public SearchRes convertSearchRes(User user, Date date, int type) {
        final String NORMAL_PHRASES = "정상";
        final String TRADY_PHARSES = "지각";
        final String ABSENT_PHARSES = "결석";

        String trace;

        if (type == _ENTRANCE)      trace = NORMAL_PHRASES;
        else if (type == _TRADY)    trace = TRADY_PHARSES;
        else if (type == _TRADY_AND_EARLYLEAVE)     trace = TRADY_PHARSES;
        else if (type == _TRADY_AND_NORMALEXIT)     trace = TRADY_PHARSES;
        else if (type == _ENTRANCE_AND_EARLYLEAVE)  trace = TRADY_PHARSES;
        else if (type == _ENTRANCE_AND_NORMALEXIT)  trace = NORMAL_PHRASES;
        else                        trace = ABSENT_PHARSES;


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
                .date(date.getYear() + "/" + date.getMonth() + "/" + date.getDay())
                .trace(trace)
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

    public static Boolean checkValid(String word) {
        for(int i = 0; i < word.length(); i++) {
            if (!String.valueOf(word.charAt(i)).matches("[a-zA-Z0-9가-힣/]"))
                return (false);
        }
        return (true);
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
        if (content == null)  return (ERROR);
        else if ("".equals(content) || "전체".equals(content))    return (ALL);
        else if (content.contains("결석"))    return (ABSENT);
        else if (content.contains("지각"))    return (TRADY);
        else if (content.matches(".*[a-zA-Z].*"))   return (CLASS_AND_TEAM_CODE);
        else if (content.matches(".*[0-9].*")) {
            if (content.matches(".*[가-밗].*[밙-힣].*")) {
                if (content.matches(".*[0-9][반].*"))
                    return (CLASS_AND_TEAM_CODE);
                return (NAME);
            }
            return (CLASS_AND_TEAM_CODE);
        }
        else if (content.matches(".*[가-힣].*"))    return (NAME);
        else    return (ERROR);
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
                searchResList.add(convertSearchRes(userList.get(i), date, _ABSENT));
                System.out.println(">>> " + userList.get(i));
                continue;
            }

            System.out.println("[A]>>> " + attendance.get());

            if (attendance.get().getType() == _ABSENT) { //과거
                searchResList.add(convertSearchRes(userList.get(i), date, _ABSENT));
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

            if (attendance.get().getType() == _TRADY || attendance.get().getType() == _TRADY_AND_EARLYLEAVE ||
                    attendance.get().getType() == _TRADY_AND_NORMALEXIT || attendance.get().getType() == _ENTRANCE_AND_EARLYLEAVE) {
                searchResList.add(convertSearchRes(userList.get(i), date, attendance.get().getType()));
            }
        }

        return (searchResList);
    }

    List<SearchRes> addTeamAndClassUserInfoListByUserAndDate(List<SearchRes> searchResList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {  //기록이 없다? 이건 에러임 => 결석으로 띄움
                System.out.println(">>> " + userList.get(i) + "   [ERROR2]");
                searchResList.add(convertSearchRes(userList.get(i), date, _ABSENT));
                continue;
            }

            searchResList.add(convertSearchRes(userList.get(i), date, attendance.get().getType()));
        }

        return (searchResList);
    }

    List<SearchRes> addNameUserInfoListByUserAndDate(List<SearchRes> searchResList, List<User> userList, Date date) {
        for (int i = 0; i < userList.size(); i++) {
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {  //기록이 없다? 이건 에러임 => 결석으로 띄움
                System.out.println(">>> " + userList.get(i) + "   [ERROR3]");
                searchResList.add(convertSearchRes(userList.get(i), date, _ABSENT));
                continue;
            }

            searchResList.add(convertSearchRes(userList.get(i), date, attendance.get().getType()));
        }

        return (searchResList);
    }

    List<SearchRes> addAllUserInfoListByUser(List<SearchRes> userInfoList, List<User> userList) {
        for (int i = 0; i < userList.size(); i++) {
            List<Attendance> attendanceList = attendanceRepository.findAllByUser(userList.get(i));

            for (int j = 0; j < attendanceList.size(); j++) {
                Date date = new Date();

                date.setYear(attendanceList.get(j).getYear());
                date.setMonth(attendanceList.get(j).getMonth());
                date.setDay(attendanceList.get(j).getDay());

                userInfoList.add(convertSearchRes(userList.get(i), date, attendanceList.get(j).getType()));
            }
        }

        return (userInfoList);
    }

    @Transactional
    List<SearchRes> getAllUserInfo() {
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = userRepository.findAll();
        Date date = getDate();

        for (int i = 0; i < userList.size(); i++){
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(userList.get(i), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent())
                searchResList.add(convertSearchRes(userList.get(i), date, _ABSENT));
            else
                searchResList.add(convertSearchRes(userList.get(i), date, attendance.get().getType()));
        }

        return (searchResList);
    }

    @Transactional
    List<SearchRes> getAbsentUserInfo(String content, SearchReq searchReq) {
        System.out.println("<<< Absent >>>");
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = content.split(" ");
        Arrays.fill(refClass, false);

        //
        if ("결석".equals(content.trim())) {
            Date date = getDate();
            userList = userRepository.findAll();

            searchResList = addAbsentUserInfoListByUserAndDate(searchResList, userList, date);

            return (searchResList);
        }
        //

        for (String word : words) {     //단어 분석
            if ("".equals(word) || !checkValid(word))    continue;

            System.out.println("contain word : " + word);

            if ("결석".equals(word))   continue;
            else if (word.contains("/")) {
                StringTokenizer st = new StringTokenizer(word,"/");
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                if (month <= 0 || month > 12)   continue;
                if (day <= 0 || day > 31)   continue;

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
        if(teamCode.size() > 0 || classCode.size() > 0) {
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
        }
        else {      //반번호, 팀코드 없음
            userList = userRepository.findAll();
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
    List<SearchRes> getTradyUserInfo(String content, SearchReq searchReq) {
        System.out.println("<<<< Trady >>>>");
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;

        String words[] = content.split(" ");
        Arrays.fill(refClass, false);

        //
        if ("지각".equals(content.trim())) {
            Date date = getDate();
            userList = userRepository.findAll();

            searchResList = addTradyUserInfoListByUserAndDate(searchResList, userList, date);

            return (searchResList);
        }
        //

        for (String word : words) {     //단어 분석
            if ("".equals(word) || !checkValid(word))    continue;

            System.out.println("contain word : " + word);

            if ("지각".equals(word))   continue;
            else if (word.contains("/")) {
                StringTokenizer st = new StringTokenizer(word,"/");
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                if (month <= 0 || month > 12)   continue;
                if (day <= 0 || day > 31)   continue;

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
        if(teamCode.size() > 0 || classCode.size() > 0) {
            for (int i = 0; i < classCode.size(); i++) {
                userList = addUserListByClassCode(userList, classCode.get(i));
            }
            for (int i = 0; i < teamCode.size(); i++) {
                int code = teamCode.get(i).charAt(1) - '0';

                if (refClass[code]) continue;       //팀이 앞서 찾아온 반에 속해 있을 시 건너 뜀

                String team = teamCode.get(i).toUpperCase();

                userList = addUserListByTeamCode(userList, team);
            }
        }
        else {      //반번호, 팀코드 없음
            userList = userRepository.findAll();
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
    List<SearchRes> getTeamAndClassCodeUserInfo(String content, SearchReq searchReq) {
        System.out.println("<<< team and class code >>>>");
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> teamCode = new LinkedList<>();
        List<String> classCode = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;
        Boolean isAll = false;

        String words[] = content.split(" ");
        Arrays.fill(refClass, false);

        for (String word : words) {     //단어 분석
            if ("".equals(word) || !checkValid(word))    continue;

            System.out.println("contain word : " + word);

            if (word.contains("/")) {
                StringTokenizer st = new StringTokenizer(word,"/");
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                if (month <= 0 || month > 12)   continue;
                if (day <= 0 || day > 31)   continue;

                Date date = makeDate(day, month);
                dates.add(date);
                dateConfig++;
            }
            else if ("전부".equals(word)) {
                isAll = true;
            }
            else if (word.matches("^[0-9].*")) {
                classCode.add(word.substring(0, 1));
                refClass[Integer.parseInt(word.substring(0, 1))] = true;
            }
            else if (word.matches("^[a-zA-Z].*"))  teamCode.add(word);
        }

        System.out.println(">>> teamCode :" + teamCode + "   classCode : " + classCode + "    Dates : " + dates + "    dateConfig : " + dateConfig + "     isAll : " + isAll);
        System.out.println(getDate());

        //2페이즈 (유저 리스트 가져오기)
        if(teamCode.size() > 0 || classCode.size() > 0) {
            for (int i = 0; i < classCode.size(); i++) {
                userList = addUserListByClassCode(userList, classCode.get(i));
            }
            for (int i = 0; i < teamCode.size(); i++) {
                int code = teamCode.get(i).charAt(1) - '0';

                if (refClass[code]) continue;       //팀이 앞서 찾아온 반에 속해 있을 시 건너 뜀

                String team = teamCode.get(i).toUpperCase();

                userList = addUserListByTeamCode(userList, team);
            }
        }
        else {
            userList = userRepository.findAll();
        }

        //3페이즈 (검색된 유저 리스트로 결석 인원 체크)
        if (isAll) {
            searchResList = addAllUserInfoListByUser(searchResList, userList);
        }
        else if (dateConfig > 0) {   //지정한 날짜가 있으면
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
    List<SearchRes> getNameCodeUserInfo(String content, SearchReq searchReq) {
        System.out.println("<<<< name >>>>");
        List<SearchRes> searchResList = new LinkedList<>();
        List<User> userList = new LinkedList<>();
        List<String> names = new LinkedList<>();
        Boolean refClass[] = new Boolean[10];
        List<Date> dates = new LinkedList<>();
        int dateConfig = 0;
        Boolean isAll = false;

        String words[] = content.split(" ");
        Arrays.fill(refClass, false);

        for (String word : words) {     //단어 분석
            if ("".equals(word) || !checkValid(word))    continue;

            System.out.println("contain word : " + word);

            if (word.contains("/")) {
                StringTokenizer st = new StringTokenizer(word,"/");
                int month = Integer.parseInt(st.nextToken());
                int day = Integer.parseInt(st.nextToken());

                if (month <= 0 || month > 12)   continue;
                if (day <= 0 || day > 31)   continue;

                Date date = makeDate(day, month);
                dates.add(date);
                dateConfig++;
            }
            else if ("전부".equals(word)) {
                isAll = true;
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
        if (isAll) { //전부
            searchResList = addAllUserInfoListByUser(searchResList, userList);
        }
        else if (dateConfig > 0) {   //지정한 날짜가 있으면
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

    private List<SearchRes> integrateResult(List<SearchRes> result, List<SearchRes> data) {
        result.addAll(data);
        return (result);
    }

    @Override
    public List<SearchRes> doIntegratedSearch(SearchReq searchReq) {
        String[] contents = searchReq.getContent().split(",");
        List<SearchRes> result = new LinkedList<>();

        for (int i = 0; i < contents.length; i++){
            String content = contents[i].trim();

            System.out.println(content);

            int flag = analyzeContent(content);

            if (flag == ERROR)  continue;
            else if (flag == ALL) result = integrateResult(result, getAllUserInfo());
            else if (flag == ABSENT) result = integrateResult(result, getAbsentUserInfo(content, searchReq));
            else if (flag == TRADY) result = integrateResult(result, getTradyUserInfo(content, searchReq));
            else if (flag == CLASS_AND_TEAM_CODE) result = integrateResult(result, getTeamAndClassCodeUserInfo(content, searchReq));
            else result = integrateResult(result, getNameCodeUserInfo(content, searchReq));
        }

        return (result);
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
