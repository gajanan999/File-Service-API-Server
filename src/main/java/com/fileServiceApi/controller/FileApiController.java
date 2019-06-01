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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fileServiceApi.responsedata.UploadFileResponse;
import com.fileServiceApi.service.FileStorageService;

@RestController
public class FileApiController {

	private static final Logger logger = LoggerFactory.getLogger(FileApiController.class);
	
	@Autowired
	FileStorageService fileStorageService;
	
	 @PostMapping("/api/upload")
	 public UploadFileResponse  uploadFile(
	            @RequestParam("file") MultipartFile file) {

	        logger.debug("Single file upload!");

	        String fileName = fileStorageService.storeFile(file);

	        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloadFile/")
	                .path(fileName)
	                .toUriString();

	        return new UploadFileResponse(fileName, fileDownloadUri,
	                file.getContentType(), file.getSize());

	  }
}
