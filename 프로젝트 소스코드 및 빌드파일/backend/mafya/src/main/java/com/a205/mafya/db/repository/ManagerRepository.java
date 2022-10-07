package com.a205.mafya.db.repository;

import com.a205.mafya.db.repository.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {

    Optional<Manager> findByManagerCode(String managerCode);
}
