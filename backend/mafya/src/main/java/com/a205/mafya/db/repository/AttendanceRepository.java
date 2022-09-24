package com.a205.mafya.db.repository;

import com.a205.mafya.db.entity.Attendance;
import com.a205.mafya.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

    public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
        Optional<Attendance> findByUserAndDayAndMonthAndYear(User user, String day, String month, String year);

        List<Attendance> findAllByUser(User user);
    }
