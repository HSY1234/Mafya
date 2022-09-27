package com.a205.mafya.api.service;

import com.a205.mafya.api.request.AddManagerReq;
import com.a205.mafya.db.entity.Manager;
import com.a205.mafya.db.repository.ManagerRepository;
import com.a205.mafya.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ManagerServiceimpl implements ManagerService{

    @Autowired
    ManagerRepository managerRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;


    @Override
    public void addManager(AddManagerReq managerReq) throws Exception {
        Manager manager = Manager.builder()
                .name(managerReq.getName())
                .managerCode(managerReq.getManagerCode())
                .classCode(managerReq.getClassCode())
                .password(managerReq.getPassword())
                .build();

        managerRepository.save(manager);

    }
}
