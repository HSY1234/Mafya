package com.a205.mafya.api.service;

import com.a205.mafya.api.request.SearchReq;
import com.a205.mafya.api.response.SearchRes;
import com.a205.mafya.db.dto.UserInfo;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SearchService {
    static final int ERROR = -1;
    static final int ALL = 0;
    static final int ABSENT = 1;
    static final int TRADY = 2;
    static final int CLASS_AND_TEAM_CODE = 3;
    static final int NAME = 4;


    static final int _ENTRANCE = 0;
    static final int _TRADY = 10;
    static final int _TRADY_AND_EARLYLEAVE = 11;
    static final int _TRADY_AND_NORMALEXIT = 12;
    static final int _ENTRANCE_AND_EARLYLEAVE = 2;
    static final int _ENTRANCE_AND_NORMALEXIT = 3;
    static final int _ABSENT = 99;

    List<SearchRes> doIntegratedSearch(SearchReq searchReq);
}
