package com.a205.mafya.db.repository;

import com.a205.mafya.db.entity.RefMonth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefMonthRepository extends JpaRepository<RefMonth, Integer> {
    Optional<RefMonth> findByDay(int day);
}
