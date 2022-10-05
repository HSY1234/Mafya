package com.a205.mafya.api.controller;

import java.util.List;

import com.a205.mafya.api.request.SearchReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.a205.mafya.util.filter.helper.ExcelHelper;
import com.a205.mafya.util.message.ResponseMessage;
//import com.a205.mafya.api.model.Tutorial;
import com.a205.mafya.db.repository.entity.User;
import com.a205.mafya.api.service.ExcelService;

@CrossOrigin("http://localhost:8080")
@Controller
@RequestMapping("/excel")
public class ExcelController {
    @Autowired
    ExcelService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam("uploadUserFile") MultipartFile file) {
        String message = "";
        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                fileService.save(file);
                message = "Uploaded the file successfully: " + file.getOriginalFilename();
                return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
            } catch (Exception e) {
                message = "Could not upload the file: " + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
            }
        }
        message = "Please upload an excel file!";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage(message));
    }

    @GetMapping("/tutorials")
    public ResponseEntity<List<User>> getAllTutorials() {
        try {
            List<User> tutorials = fileService.getAllTutorials();

            if (tutorials.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(tutorials, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> getFile() {
        String filename = "user_list.xlsx";
        InputStreamResource file = new InputStreamResource(fileService.load());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    @PostMapping("/search")
    public ResponseEntity<?> getSearch(@RequestBody SearchReq searchReq) {
        String filename = "result.xlsx";
        InputStreamResource file = new InputStreamResource(fileService.getSearchResult(searchReq));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);

    }

}
