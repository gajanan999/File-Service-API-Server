package com.fileServiceApi.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypes;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fileServiceApi.dao.FileDocumentRepository;
import com.fileServiceApi.responsedata.UploadFileResponse;
import com.fileServiceApi.service.FileStorageMongoService;
import com.fileServiceApi.vo.ResourceVo;

@RestController
public class FileApiMongoController {
	
	private static final Logger logger = LoggerFactory.getLogger(FileApiMongoController.class);

	@Autowired
	private FileStorageMongoService fileStorageMongoService;
	
	@Autowired 
	FileDocumentRepository fileDocumentRepository;

	
	@PostMapping("/mongotemp/upload")
	public UploadFileResponse singleFileUpload(@RequestParam("file") MultipartFile file, @RequestParam("email") String email) {
			String fileName = fileStorageMongoService.storeFile(file, email);
			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
					.path(fileName).toUriString();
	    return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}
	
	@PostMapping(value="/mongotemp/retrieve", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource> retrieveFile(@RequestParam("email") String email,@RequestParam("fileName") String fileName,HttpServletRequest request){	

	    ResourceVo resourceVo=fileStorageMongoService.loadFileAsResource(email, fileName);   
	    return ResponseEntity.ok().contentType(MediaType.parseMediaType(resourceVo.getContentType()))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resourceVo.getFileName() + "\"").body(resourceVo.getResource());
	}
	
	
}
