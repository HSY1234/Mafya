package com.a205.mafya.api.controller;

import com.a205.mafya.api.service.AwsS3Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api("아마존 S3 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/s3")
public class AmazonS3TestController {
    private final AwsS3Service awsS3Service;

    /**
     * Amazon S3에 파일 업로드
     * return 성공 시 200 Success와 함께 업로드 된 url반환
     */
    @ApiOperation(value = "Amazon S3에 파일 업로드", notes = "Amazon S3에 파일 업로드 ")
    @PostMapping(value = "/file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadFile(@ApiParam(value="(단일) 이미지, 동영상 업로드", required = true) @RequestPart(value="file") MultipartFile multipartFile) {
        System.out.println("[ 파일 업로드 ]");

        if (multipartFile.getContentType().startsWith("image")){
            System.out.println(">>> 이미지 파일 업로드 : " + multipartFile.getOriginalFilename());
            String result = awsS3Service.uploadFileOnlyOne(multipartFile);

            if ("fail".equals(result)) {
                System.out.println(">>> 이미지 업로드 실패");
                return (new ResponseEntity<String>("fail", HttpStatus.INTERNAL_SERVER_ERROR));
            }
            else {
                System.out.println(">>> 이미지 업로드 성공");
                return (new ResponseEntity<String>(result, HttpStatus.OK));
            }
        }
        else {
            System.out.println(">>> 기타 파일 업로드");
            return (new ResponseEntity<String>("fail", HttpStatus.BAD_REQUEST));
        }
    }

    /**
     * Amazon S3에 업로드 된 파일을 삭제
     * return 성공 시 200 Success
     */
    @ApiOperation(value = "Amazon S3에 업로드 된 파일을 삭제", notes = "Amazon S3에 업로드된 파일 삭제")
    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@ApiParam(value="파일 하나 삭제(url 전달)", required = true) @RequestParam String url) {
        System.out.println("[ 파일 삭제 ] : " + url);

        awsS3Service.deleteFile(url);
        return (new ResponseEntity<Void>(HttpStatus.OK));
    }
}
