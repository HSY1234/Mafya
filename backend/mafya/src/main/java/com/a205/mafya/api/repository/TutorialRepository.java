package com.a205.mafya.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

//import com.a205.mafya.api.model.Tutorial;
import com.a205.mafya.db.entity.User;

public interface TutorialRepository extends JpaRepository<User, Long> {
}
