package com.a205.mafya.db.repository;

import com.a205.mafya.db.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserCode(String userCode);

    List<User> findAllByClassCodeAndStatus(String classCode, int status);
    List<User> findAllByTeamCode(String teamCode);

    //[Park SeHyeon Add]
    List<User> findAllByClassCode(String classCode);

    Page<User> findAllByClassCode(String classCode, Pageable pageable);

    List<User> findAllByClassCodeAndAbsentGreaterThanEqual(String classCode, int absent);

    //[Park SeHyeon End]
}
