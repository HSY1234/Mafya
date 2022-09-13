package com.a205.mafya.api.repository;

import com.a205.mafya.db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
