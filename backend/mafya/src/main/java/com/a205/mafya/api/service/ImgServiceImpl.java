package com.a205.mafya.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class ImgServiceImpl implements ImgService {
    @Override
    public boolean saveImg(MultipartFile img) throws Exception {
//        System.out.println(System.getProperty("user.dir"));

//        String filePath = "C:/Users/SSAFY/Desktop/tmp"; //local Test Path
        String filePath = "/sehyeon";

        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        String originalFileName = img.getOriginalFilename();
        String fileFullPath = filePath + "/" + originalFileName;

        try {
            img.transferTo(new File(fileFullPath));
            System.out.println("경로: " + fileFullPath);
        } catch (Exception e) {
            System.out.println("경로: " + fileFullPath);
            System.out.println("저장 중 에러");
        }
        return (true);
    }
}
