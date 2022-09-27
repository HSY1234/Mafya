package com.a205.mafya.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface ImgService {
    static final String faceURL = "https://mafya.ml/ai/modelsearch";
    static final String maskURL = "https://mafya.ml/ai/maskreco";
    static final String imgURL = "https://mafya.ml/api/images";

    public boolean saveImg(MultipartFile img, String userCode);

    public String getUrl(String userCode);

    public Map<String, String> processFace(MultipartFile img);

    public Map<String, String> processMask(MultipartFile img, String userCode);
}
