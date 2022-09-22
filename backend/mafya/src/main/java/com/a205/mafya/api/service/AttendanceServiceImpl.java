package com.a205.mafya.api.service;

import com.a205.mafya.api.response.AttendanceTeamRes;
import com.a205.mafya.db.dto.Date;
import com.a205.mafya.db.entity.Attendance;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.AttendanceRepository;
import com.a205.mafya.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    private String getTime() {
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String time = now.format(formatter);

        return (time);
    }

    public Date getDate() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        Date date = new Date();

        date.setYear(now.getYear() + "");
        date.setMonth(now.getMonthValue() < 10 ? "0" + now.getMonthValue() : now.getMonthValue() + "");
        date.setDay(now.getDayOfMonth() < 10 ? "0" + now.getDayOfMonth() : now.getDayOfMonth() + "");

        return (date);
    }

    @Transactional
    int recordEntrance(String userCode) {
        Optional<User> user = userRepository.findByUserCode(userCode);

        if (user.isPresent()) {
            Date date = getDate();
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(user.get(), date.getDay(), date.getMonth(), date.getYear());
            if (attendance.isPresent()) return (RE_REQUEST);

            Attendance data = new Attendance();

            data.setType(ENTRANCE);

            data.setEnterTime(getTime());
            data.setExitTime("");

            data.setYear(date.getYear());
            data.setMonth(date.getMonth());
            data.setDay(date.getDay());

            data.setUser(user.get());

            attendanceRepository.save(data);

            return (data.getType());
        }
        else
            return (NO_USER);
    }

    @Transactional
    int recordExit(String userCode) {
        Optional<User> user = userRepository.findByUserCode(userCode);

        if (user.isPresent()) {
            Date date = getDate();
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(user.get(), date.getDay(), date.getMonth(), date.getYear());

            if (!attendance.isPresent()) {  //지각
                Attendance data = new Attendance();

                data.setType(TRADY);

                data.setEnterTime(getTime());
                data.setExitTime("");

                data.setYear(date.getYear());
                data.setMonth(date.getMonth());
                data.setDay(date.getDay());

                data.setUser(user.get());

                attendanceRepository.save(data);
                return (data.getType());
            }
            else {
                LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

                if (now.getHour() < EXITTIME) {
                    if (attendance.get().getType() / 10 == 1)   attendance.get().setType(TRADY_AND_EARLYLEAVE);
                    else                                        attendance.get().setType(ENTRANCE_AND_EARLYLEAVE);
                }
                else {
                    if (attendance.get().getType() / 10 == 1)   attendance.get().setType(TRADY_AND_NORMALEXIT);
                    else                                        attendance.get().setType(ENTRANCE_AND_NORMALEXIT);
                }
                attendance.get().setExitTime(getTime());

                attendanceRepository.save(attendance.get());

                return (attendance.get().getType());
            }

        }
        else
            return (NO_USER);
    }


    /**
     *
     * @param userCode
     * @return
     * -1: 존재 하지 않는 유저
     * 0: 입실
     * 10: 지각
     * 11: 지각 한 후 조퇴
     * 12: 지각 한 후 정상 퇴실
     * 2: 정상 입실 후 조퇴
     * 3: 정상 퇴실
     * 4: 입실 후 다시 입실 요청(요청 안 받고, 지각 시간 이후부터 다시 체크 가능)
     */
    @Override
    public int record(String userCode) {
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));
        int time = now.getHour();
        int result;

        if (time < ENTERTIME)    result = recordEntrance(userCode);
        else                    result = recordExit(userCode);

        return (result);
    }

    @Override
    @Transactional
    public List<AttendanceTeamRes> getTeamInfo(String teamCode) {
        List<AttendanceTeamRes> attendanceTeamResList = new LinkedList<>();

        List<User> userList = userRepository.findAllByTeamCode(teamCode);
        for (User user : userList) {
            Date date = getDate();
            Optional<Attendance> attendance = attendanceRepository.findByUserAndDayAndMonthAndYear(user, date.getDay(), date.getMonth(), date.getYear()); //이것 좀 손봐야함 오늘 날짜 기준으로 정보 있는지 없는지 분기처리해야함

            AttendanceTeamRes attendanceTeamRes = new AttendanceTeamRes();

            attendanceTeamRes.setId(user.getId());
            attendanceTeamRes.setName(user.getName());
            attendanceTeamRes.setStatus(user.getStatus());
            attendanceTeamRes.setTeamCode(user.getTeamCode());
            attendanceTeamRes.setClassCode(user.getClassCode());
            attendanceTeamRes.setPhoneNum(user.getPhoneNum());
            attendanceTeamRes.setTeamLeader(user.isTeamLeader());

            if (attendance.isPresent()) attendanceTeamRes.setAttendanceStatus(attendance.get().getType());
            else                        attendanceTeamRes.setAttendanceStatus(ABSENT);
        }
        return (attendanceTeamResList);
    }
}
