package com.a205.mafya.api.service;

import com.a205.mafya.api.request.SearchReq;
import com.a205.mafya.db.dto.UserInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SearchServiceImplTest {
    @Autowired
    private SearchService searchService;

    @Test
    void test2() {
//        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        String content = "2반";
        SearchReq searchReq = new SearchReq(content, false, false);

        List<UserInfo> userInfoList = searchService.doIntegratedSearch(searchReq);
//        List<UserInfo> userInfoList = new LinkedList<>();

        for (int i = 0; i < userInfoList.size(); i++) {
            System.out.println(userInfoList.get(i).getName() + "  " + userInfoList.get(i).getId());
        }
    }

}