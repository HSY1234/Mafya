package com.a205.mafya.db.repository;

import com.a205.mafya.db.repository.entity.User;
import com.a205.mafya.db.repository.entity.UserImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserImgRepository extends JpaRepository<UserImg, Integer> {
    Optional<UserImg> findByUser(User user);

}
