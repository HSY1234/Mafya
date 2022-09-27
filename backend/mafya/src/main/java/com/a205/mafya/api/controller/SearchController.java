package com.a205.mafya.api.controller;

import com.a205.mafya.api.request.SearchReq;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {
    @PostMapping
    public ResponseEntity<?> doSearch(@RequestBody SearchReq searchReq) {

        return (new ResponseEntity<Void>(HttpStatus.OK));
    }
}
