package com.a205.mafya.api.service;

import com.a205.mafya.api.response.AttendanceTeamRes;
import com.a205.mafya.db.dto.UserInfo;
import com.a205.mafya.db.entity.Attendance;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.AttendanceRepository;
import com.a205.mafya.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
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

    @Transactional
    int recordEntrance(String userCode) {
        Optional<User> user = userRepository.findByUserCode(userCode);

        if (user.isPresent()) {
            Optional<Attendance> attendance = attendanceRepository.findByUser(user.get());
            LocalDate date = LocalDate.now(ZoneId.of("Asia/Seoul"));
            LocalTime time = LocalTime.now(ZoneId.of("Asia/Seoul"));

            if (attendance.isPresent()) return (4);

            Attendance data = new Attendance();

            data.setType(0);

            data.setEnterTime(getTime());
            data.setExitTime("");

            data.setYear(date.getYear() + "");
            data.setMonth(date.getMonthValue() < 10 ? "0" + date.getMonthValue() : date.getMonthValue() + "");
            data.setDay(date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth() + "");

            data.setUser(user.get());

            attendanceRepository.save(data);

            return (data.getType());
        }
        else
            return (-1);
    }

    @Transactional
    int recordExit(String userCode) {
        Optional<User> user = userRepository.findByUserCode(userCode);

        if (user.isPresent()) {
            Optional<Attendance> attendance = attendanceRepository.findByUser(user.get());

            if (!attendance.isPresent()) {  //지각
                LocalDate date = LocalDate.now(ZoneId.of("Asia/Seoul"));

                Attendance data = new Attendance();

                data.setType(10);

                data.setEnterTime(getTime());
                data.setExitTime("");

                data.setYear(date.getYear() + "");
                data.setMonth(date.getMonthValue() < 10 ? "0" + date.getMonthValue() : date.getMonthValue() + "");
                data.setDay(date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth() + "");

                data.setUser(user.get());

                attendanceRepository.save(data);
                return (data.getType());
            }
            else {
                LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

                if (now.getHour() < exitTime) {
                    if (attendance.get().getType() / 10 == 1)   attendance.get().setType(11);
                    else                                        attendance.get().setType(2);
                }
                else {
                    if (attendance.get().getType() / 10 == 1)   attendance.get().setType(12);
                    else                                        attendance.get().setType(3);
                }
                attendance.get().setExitTime(getTime());

                attendanceRepository.save(attendance.get());

                return (attendance.get().getType());
            }

        }
        else
            return (-1);
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

        if (time < enterTime)    result = recordEntrance(userCode);
        else                    result = recordExit(userCode);

        return (result);
    }

    @Override
    @Transactional
    public List<AttendanceTeamRes> getTeamInfo(String teamCode) {
        List<AttendanceTeamRes> attendanceTeamResList = new LinkedList<>();

        List<User> userList = userRepository.findAllByTeamCode(teamCode);
        for (User user : userList) {
            Optional<Attendance> attendance = attendanceRepository.findByUser(user);
            AttendanceTeamRes attendanceTeamRes = new AttendanceTeamRes();

            attendanceTeamRes.setId(user.getId());
            attendanceTeamRes.setName(user.getName());
            attendanceTeamRes.setStatus(user.getStatus());
            attendanceTeamRes.setTeamCode(user.getTeamCode());
            attendanceTeamRes.setClassCode(user.getClassCode());
            attendanceTeamRes.setPhoneNum(user.getPhoneNum());
            attendanceTeamRes.setTeamLeader(user.isTeamLeader());
            attendanceTeamRes.setAttendanceStatus(attendance.get().getType());
        }
        return (attendanceTeamResList);
    }
}
