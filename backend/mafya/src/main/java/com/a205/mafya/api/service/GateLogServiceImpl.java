package com.a205.mafya.api.service;

import com.a205.mafya.db.entity.GateLog;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.GateLogRepository;
import com.a205.mafya.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class GateLogServiceImpl implements GateLogService {
    @Autowired
    GateLogRepository gateLogRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AttendanceService attendanceService;

    @Override
    @Transactional
    public int inputLog(String userCode) {
        Optional<User> user = userRepository.findByUserCode(userCode);

        if (user.isPresent()) {
            GateLog gateLog = new GateLog();
            int result;

            gateLog.setUser(user.get());
            gateLogRepository.save(gateLog);

            result = attendanceService.record(userCode);

            return (result);
        }
        else {
            return (-2);
        }
    }
}
