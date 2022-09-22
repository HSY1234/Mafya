package com.a205.mafya.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AttendanceServiceImplTest {
    @Autowired
    private AttendanceServiceImpl attendanceServiceImpl;

    @Test
    void test() {
        System.out.println(">>> " + attendanceServiceImpl.getDate());

        attendanceServiceImpl.record("0743000");
    }
}