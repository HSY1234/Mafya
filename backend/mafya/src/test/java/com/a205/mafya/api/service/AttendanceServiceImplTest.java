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
        attendanceServiceImpl.record("0743004");
        attendanceServiceImpl.getTeamInfo("A205").forEach(System.out::println);
    }
}