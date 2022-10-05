package com.a205.mafya.api.service;

import com.a205.mafya.api.request.SearchReq;
import com.a205.mafya.api.response.SearchRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.a205.mafya.util.filter.helper.ExcelHelper;
//import com.a205.mafya.api.model.Tutorial;
import com.a205.mafya.db.entity.User;
import com.a205.mafya.db.repository.TutorialRepository;

@Service
public class ExcelService {
    @Autowired
    TutorialRepository repository;

    @Autowired
    SearchService searchService;

    public void save(MultipartFile file){
        try{
            List<User> tutorials = ExcelHelper.excelToTutorials(file.getInputStream());
            repository.saveAll(tutorials);
        } catch (IOException e) {
            throw new RuntimeException("fail to store excel data: " + e.getMessage());
        }
    }

    public ByteArrayInputStream load(){
        List<User> tutorials = repository.findAll();
        ByteArrayInputStream in = ExcelHelper.tutorialsToExcel(tutorials);
        return in;
    }

    public List<User> getAllTutorials() {
        return repository.findAll();
    }

    public ByteArrayInputStream getSearchResult(SearchReq searchReq) {
        List<SearchRes> searchResList = searchService.doIntegratedSearch(searchReq);

        ByteArrayInputStream bais = ExcelHelper.SearchToExcel(searchResList);

        return (bais);
    }
}
