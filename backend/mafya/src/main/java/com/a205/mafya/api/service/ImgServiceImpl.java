package com.a205.mafya.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
public class ImgServiceImpl implements ImgService {
    @Override
    public boolean saveImg(MultipartFile img, String userCode) {
        String filePath = "/sehyeon";

        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        String fileFullPath = filePath + "/" + userCode + ".jpg" ;

        try {
            img.transferTo(new File(fileFullPath));
            return (true);
        } catch (Exception e) {
            System.out.println("저장 중 에러");
            return (false);
        }
    }

    @Override
    public boolean uploadCamImg(MultipartFile img) {
        String filePath = "/identify";

        File dir = new File(filePath);
        if (!dir.exists()) dir.mkdir();

        String fileFullPath = filePath + "/" + "cam.jpg";

        try {
            img.transferTo(new File(fileFullPath));
            return (true);
        } catch (Exception e) {
            System.out.println("camImg 업로드 실패");
            return (false);
        }
    }
}
