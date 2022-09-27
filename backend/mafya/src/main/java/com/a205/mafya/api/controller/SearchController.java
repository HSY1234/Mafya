package com.a205.mafya.api.controller;

import com.a205.mafya.api.request.SearchReq;
import com.a205.mafya.api.service.SearchService;
import com.a205.mafya.db.dto.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @PostMapping
    public ResponseEntity<?> doSearch(@RequestBody SearchReq searchReq) {
        List<UserInfo> userInfoList = searchService.doIntegratedSearch(searchReq);

        return (new ResponseEntity<List<UserInfo>>(userInfoList, HttpStatus.OK));
    }
}
