package com.a205.mafya.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class ImgServiceImpl implements ImgService {
    @Override
    public boolean saveImg(MultipartFile img, String userCode) {
//        String filePath = "C:/Users/SSAFY/Desktop/tmp"; //local Test Path //System.getProperty("user.dir")
        String filePath = "/sehyeon";

        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        String originalFileName = img.getOriginalFilename();
        String fileFullPath = filePath + "/" + userCode + ".jpg" ;

        try {
            img.transferTo(new File(fileFullPath));
            return (true);
        } catch (Exception e) {
            System.out.println("저장 중 에러");
            return (false);
        }
    }
}
