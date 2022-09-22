package com.a205.mafya.db.repository;

import com.a205.mafya.db.entity.Attendance;
import com.a205.mafya.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

    import java.util.Optional;

    public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
        Optional<Attendance> findByUser(User user);
    }
