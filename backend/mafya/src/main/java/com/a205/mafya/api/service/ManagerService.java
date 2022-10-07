package com.a205.mafya.api.service;

import com.a205.mafya.api.request.AddManagerReq;
import com.a205.mafya.api.request.AddUserReq;

public interface ManagerService {
    public void addManager(AddManagerReq managerReq) throws Exception;
}
