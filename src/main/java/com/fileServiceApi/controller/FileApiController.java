package com.fileServiceApi.controller;

import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fileServiceApi.service.FileStorageService;

@RestController
public class FileApiController {

	private static final Logger logger = LoggerFactory.getLogger(FileApiController.class);
	
	@Autowired
	FileStorageService fileStorageService;
	
	 @PostMapping("/api/upload")
	    // If not @RestController, uncomment this
	    //@ResponseBody
	    public ResponseEntity<?> uploadFile(
	            @RequestParam("file") MultipartFile uploadfile) {

	        logger.debug("Single file upload!");

	        if (uploadfile.isEmpty()) {
	            return new ResponseEntity("please select a file!", HttpStatus.OK);
	        }

	        fileStorageService.storeFile(uploadfile);

	        return new ResponseEntity("Successfully uploaded - " +
	                uploadfile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);

	    }
}
