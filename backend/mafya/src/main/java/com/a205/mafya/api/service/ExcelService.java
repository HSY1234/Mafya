package com.a205.mafya.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.a205.mafya.api.filter.helper.ExcelHelper;
//import com.a205.mafya.api.model.Tutorial;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.api.repository.TutorialRepository;

@Service
public class ExcelService {
    @Autowired
    TutorialRepository repository;

    public void save(MultipartFile file){
        System.out.println("ExcelService_save");
        System.out.println("00");
        try{
            System.out.println("11");
            System.out.println(file.getOriginalFilename());
            System.out.println(">>>>>>>>>" + file.getBytes());
            List<User> tutorials = ExcelHelper.excelToTutorials(file.getInputStream());
            /*System.out.println(file.getOriginalFilename());
            System.out.println(">>>>>>>>>" + file.getBytes());*/

            //List<Tutorial> test_user = ExcelHelper.excelToTutorials(file.getInputStream());
            System.out.println("22");
            System.out.println(">>>>"+tutorials);
            repository.saveAll(tutorials);
            //repository.saveAll(test_user);
            System.out.println("33");
        } catch (IOException e) {
            System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load(){
        System.out.println("00");
        List<User> tutorials = repository.findAll();
        //List<Tutorial> test_user = repository.findAll();
        System.out.println("11");
        ByteArrayInputStream in = ExcelHelper.tutorialsToExcel(tutorials);
        //ByteArrayInputStream in = ExcelHelper.tutorialsToExcel(test_user);
        System.out.println("22");
        return in;
    }

    public List<User> getAllTutorials() {
        return repository.findAll();
    }
}
