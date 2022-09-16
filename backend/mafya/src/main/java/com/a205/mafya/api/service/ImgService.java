package com.a205.mafya.api.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImgService {
    public boolean saveImg( MultipartFile img) throws Exception;
}
