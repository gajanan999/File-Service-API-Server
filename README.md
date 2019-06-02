# File Service API

**File Upload, Delete, Download and Update operation using Spring Boot REST API**

File Service API Server 


In this article, I have developed the RESTful spring boot web service. I have created the the spring boot application with REST API which will upload, delete, download & update
the file in the File storage. currently I have used local storage to store a file 

Spring client uses HTTP Multipart requests to store and update file on the server. for that we have do some configuration in server application.properties as follows

## Before bulding project as maven-build 
     **i.e file.upload-dir=/Users/gajagaik/FileStorage/**
    (@Test run issue may occure to avoid that do the following step)
    You have to keep a cute.jpeg file in the fileStoarge location in the local system and file storage location has been mentioned in application.properties

    


**src/main/resources/application.properties**
    
    ## MULTIPART (MultipartProperties)
    # Enable multipart uploads
    spring.servlet.multipart.enabled=true
    # Threshold after which files are written to disk.
    spring.servlet.multipart.file-size-threshold=2KB
    # Max file size.
    spring.servlet.multipart.max-file-size=200MB
    # Max Request Size
    spring.servlet.multipart.max-request-size=215MB
    

## File Storage Properties
**src/main/resources/application.properties**

    # All files uploaded through the REST API will be stored in this directory 
    file.upload-dir=/Users/gajagaik/FileStorage/


Create a Pojo class StorageProperties.java to get location of Upload Directory in current local system i.e Server 

    package com.fileServiceApi.config;
    
    import org.springframework.boot.context.properties.ConfigurationProperties;
    @ConfigurationProperties(prefix = "file")
    public class StorageProperties {
    
    	private String uploadDir;
    
        public String getUploadDir() {
            return uploadDir;
        }
    
        public void setUploadDir(String uploadDir) {
            this.uploadDir = uploadDir;
        }
    }


#Create a FileStorageService.java class to do the CRUD Operation on the File which are locted in File storage



    package com.fileServiceApi.service;
    
    import java.io.File;
    import java.io.IOException;
    import java.net.MalformedURLException;
    import java.nio.file.Files;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.nio.file.StandardCopyOption;
    
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.Resource;
    import org.springframework.core.io.UrlResource;
    import org.springframework.stereotype.Service;
    import org.springframework.util.StringUtils;
    import org.springframework.web.multipart.MultipartFile;
    
    import com.fileServiceApi.config.StorageProperties;
    
    @Service
    public class FileStorageService {
    
    	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    
    	private final Path fileStorageLocation;
    
    	@Autowired
    	public FileStorageService(StorageProperties storageProperties) {
    		this.fileStorageLocation = Paths.get(storageProperties.getUploadDir()).toAbsolutePath().normalize();
    
    		try {
    			Files.createDirectories(this.fileStorageLocation);
    		} catch (Exception ex) {
    			logger.error(ex.getMessage(), ex);
    		}
    	}
    
    	public String storeFile(MultipartFile file) {
    		// Normalize file name
    		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    		try {
    			// Check if the file's name contains invalid characters
    			if (fileName.contains("..")) {
    				logger.info("Sorry! Filename contains invalid path sequence ", fileName);
    
    			}
    			// Copy file to the target location (Replacing existing file with the same name)
    			Path targetLocation = this.fileStorageLocation.resolve(fileName);
    			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
    
    		} catch (IOException ex) {
    			logger.error("Could not store file " + fileName + ". Please try again!", ex);
    
    		}
    		return fileName;
    
    	}
    
    	public Resource loadFileAsResource(String fileName) {
    		Resource resource = null;
    		try {
    			Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
    			resource = new UrlResource(filePath.toUri());
    			if (resource.exists()) {
    				return resource;
    			} else {
    				logger.error("File not found " + fileName);
    
    			}
    		} catch (MalformedURLException ex) {
    			logger.error("File not found " + fileName, ex);
    
    		}
    		return resource;
    	}
    
    	public boolean deleteFile(String fileName) {
    		boolean removedFlag = false;
    		logger.debug("Entering in deleteFile Method");
    		try {
    			Path targetLocation = this.fileStorageLocation.resolve(fileName);
    			logger.debug(targetLocation.toUri().getPath());
    			File file = new File(targetLocation.toUri().getPath());
    
    			if (file.delete()) {
    				removedFlag = true;
    			} else {
    				removedFlag = false;
    			}
    
    		} catch (Exception e) {
    
    			e.printStackTrace();
    
    		}
    		logger.debug("Exiting from deleteFile method");
    		return removedFlag;
    	}
    
    	/**
    	 * To check File exists in the File Storage or not
    	 */
    	public boolean checkFileExists(String fileName) {
    		boolean exists = false;
    		Path targetLocation = this.fileStorageLocation.resolve(fileName);
    		logger.debug(targetLocation.toUri().getPath());
    		File file = new File(targetLocation.toUri().getPath());
    		if (file.exists()) {
    			exists = true;
    		}
    		return exists;
    	}
    
    }



## Create a FileApiController.java to handle HTTP request from client

    
    package com.fileServiceApi.controller;
    
    import java.io.IOException;
    
    import javax.servlet.http.HttpServletRequest;
    
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.Resource;
    import org.springframework.http.HttpHeaders;
    import org.springframework.http.MediaType;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.DeleteMapping;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.PutMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;
    import org.springframework.web.multipart.MultipartFile;
    import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
    
    import com.fileServiceApi.responsedata.DeleteFileResponse;
    import com.fileServiceApi.responsedata.UpdateFileResponse;
    import com.fileServiceApi.responsedata.UploadFileResponse;
    import com.fileServiceApi.service.FileStorageService;
    
    @RestController
    public class FileApiController {
    
    	private static final Logger logger = LoggerFactory.getLogger(FileApiController.class);
    
    	@Autowired
    	FileStorageService fileStorageService;
    	
    	/**
    	 * Upload a new File in the File Storage using @PostMapping
    	 * @param file
    	 * @return
    	 */
    	@PostMapping("/api/upload")
    	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {
    
    		logger.debug("Entering in uploadFile method");
    
    		String fileName = fileStorageService.storeFile(file);
    
    		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
    				.path(fileName).toUriString();
    		logger.debug("Exiting from uploadFile method");
    		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
    	}
    
    	
    	/**
    	 *  Download the File from File Storage using @GetMapping
    	 * @param fileName
    	 * @param request
    	 * @return
    	 */
    	@GetMapping("api/downloadFile/{fileName:.+}")
    	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
    		logger.debug("Entering in downloadFile method");
    		String contentType = null;
    		Resource resource = null;
    		String file = "";
    		if (fileStorageService.checkFileExists(fileName)) {
    			resource = fileStorageService.loadFileAsResource(fileName);
    			try {
    				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
    			} catch (IOException ex) {
    				logger.info("Could not determine file type.");
    			}
    
    		}
    		if (contentType == null) {
    			contentType = "application/octet-stream";
    		}
    		file = null != resource ? "resource.getFilename()" : fileName + " is Not Exists";
    		logger.debug("Exiting  from downloadFile method");
    		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
    				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"").body(resource);
    	}
    	
    	/**
    	 *  Delete the file from FileStorage using @DeleteMapping 
    	 * @param fileName
    	 * @return
    	 */
    	@DeleteMapping("api/delete/{fileName:.+}")
    	public DeleteFileResponse deleteFile(@PathVariable String fileName) {
    		String message = "Something Went WRONG! May be file not found or you don't have access to delete the File";
    		String operationStatus = "File Delete operation Failed";
    		if (fileStorageService.checkFileExists(fileName)) {
    			if (fileStorageService.deleteFile(fileName)) {
    				message = "File Deleted Successfully";
    				operationStatus = "SUCCESS";
    			}
    		} else {
    			message = "File is not exists in the File Storage";
    			operationStatus = "FAILED";
    		}
    		return new DeleteFileResponse(fileName, operationStatus, message);
    	}
    	
    	
    	/**
    	 * Update a file in the file storage with new file which is passed in HTTP : PUT method call 
    	 * @param file
    	 * @param fileName
    	 * @return
    	 */
    	@PutMapping("/api/updateFile")
    	public UpdateFileResponse updateFile(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
    		String message = "";
    		String operationStatus = "";
    		String fileDownloadUri="";
    		String updatedFileName="";
    		
    		if (fileStorageService.checkFileExists(fileName)) {
    			if (fileStorageService.deleteFile(fileName)) {
    				updatedFileName = fileStorageService.storeFile(file);
    				fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
    						.path(fileName).toUriString();
    				message = "File Successfully updated";
    				operationStatus = "SUCCESS";
    			}else {
    				message = "File can not be updated cause may be you don't have access to it";
    				operationStatus = "FAILED";
    			}
    		}else {
    			message = "File is not exists in the File Storage for Update Operation";
    			operationStatus = "FAILED";
    		}
    		return new UpdateFileResponse(updatedFileName, operationStatus, message,fileDownloadUri);
    	}
    
    }


## pom.xml 

    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    	<modelVersion>4.0.0</modelVersion>
    	<parent>
    		<groupId>org.springframework.boot</groupId>
    		<artifactId>spring-boot-starter-parent</artifactId>
    		<version>2.1.5.RELEASE</version>
    		<relativePath/> <!-- lookup parent from repository -->
    	</parent>
    	<groupId>com.fileServiceApi</groupId>
    	<artifactId>FileServiceAPIServer</artifactId>
    	<version>0.0.1-SNAPSHOT</version>
    	<name>FileServiceAPIServer</name>
    	<description>Demo project for File Service API Using REST Service</description>
    
    	<properties>
    		<java.version>1.8</java.version>
    	</properties>
    
    	<dependencies>
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-web</artifactId>
    		</dependency>
    
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-test</artifactId>
    			<scope>test</scope>
    		</dependency>
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-configuration-processor</artifactId>
    			<optional>true</optional>
    		</dependency>
    	</dependencies>
    
    	<build>
    		<plugins>
    			<plugin>
    				<groupId>org.springframework.boot</groupId>
    				<artifactId>spring-boot-maven-plugin</artifactId>
    			</plugin>
    		</plugins>
    	</build>
    
    </project>

