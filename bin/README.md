# File Service API

**File Upload, Delete, Download and Update operation using Spring Boot REST API**

File Service API Server 


In this article, I have developed the RESTful spring boot web service. I have created the the spring boot application with REST API which will upload, delete, download & update
the file in the File storage. currently I have used local storage to store a file 

Spring client uses HTTP Multipart requests to store and update file on the server. for that we have do some configuration in server application.properties as follows

## Before bulding project as maven-build 
     **file.upload-dir=/Users/gajagaik/FileStorage/**
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



### File Download and upload using Mongodb

Create Java Mongodb configuration file

    package com.fileServiceApi.config;
    
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.context.annotation.Bean;
    import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
    import org.springframework.data.mongodb.core.MongoTemplate;
    import org.springframework.data.mongodb.gridfs.GridFsTemplate;
    
    import com.mongodb.MongoClient;
    
    public class SpringMongoConfig extends AbstractMongoConfiguration{
    	
    	@Value("mongo-database")
    	private String database;
    
    	@Override
    	public MongoClient mongoClient() {
    		// TODO Auto-generated method stub
    		 return new MongoClient("localhost");
    	}
    
    	@Override
    	protected String getDatabaseName() {
    		// TODO Auto-generated method stub
    		return "fileStorage";
    	}
    	
    	@Bean
    	public GridFsTemplate gridFsTemplate() throws Exception {
    	    return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    	}
    
    	@Bean
        public MongoTemplate mongoTemplate() throws Exception {
            return new MongoTemplate(mongoClient(), database);
        }
    }


Create a controller which will handle the REST HTTP request **FileApiMongoController**
    
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
        


### Create a service class FileStorageMongoService
FileStorageMongoService class is created to handle the business logic, in our case it is used to convert the file in binary and while retriving convert the file from binary to 
normal file and send back to rest response

also it is used for calling Respository FileDocumentRepository

    package com.fileServiceApi.service;

    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.net.MalformedURLException;
    import java.nio.file.Path;
    import java.nio.file.Paths;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.apache.tika.mime.MimeType;
    import org.apache.tika.mime.MimeTypeException;
    import org.apache.tika.mime.MimeTypes;
    import org.bson.BsonBinarySubType;
    import org.bson.types.Binary;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.core.io.Resource;
    import org.springframework.core.io.UrlResource;
    import org.springframework.data.mongodb.core.MongoTemplate;
    import org.springframework.stereotype.Service;
    import org.springframework.util.StringUtils;
    import org.springframework.web.multipart.MultipartFile;
    
    import com.fileServiceApi.dao.FileDocumentRepository;
    import com.fileServiceApi.entities.FileDocument;
    import com.fileServiceApi.vo.ResourceVo;
    
    @Service
    public class FileStorageMongoService {
    
    	
    	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    	
    	public static String RETRIEVE_FOLDER="/Users/gajagaik/FileStorage/";
    	
    	@Autowired
    	MongoTemplate mongoTemplate;
    	
    	@Autowired 
    	FileDocumentRepository fileDocumentRepository;
    	
    	public String storeFile(MultipartFile file,String email) {
    		// Normalize file name
    		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    		try {
    			FileDocument demoDocument = new FileDocument();
    	        demoDocument.setEmailId(email);
    	        demoDocument.setDocType("pictures");
    	        demoDocument.setFile(new Binary(BsonBinarySubType.BINARY, file.getBytes()));
    	        demoDocument.setContentType(file.getContentType());
    	        demoDocument.setFileName(file.getOriginalFilename());
    	        fileDocumentRepository.save(demoDocument);
    	       // mongoTemplate.insert(demoDocument);
    
    		} catch (Exception ex) {
    			logger.error("Could not store file " + fileName + ". Please try again!", ex);
    
    		}
    		return fileName;
    
    	}
    	
    	public ResourceVo loadFileAsResource(String email,String fileName) {
    		ResourceVo resourceVo = new ResourceVo();
    		resourceVo.setContentType("application/octet-stream");
    		resourceVo.setFileName(fileName);
    		List<FileDocument> demoDocuments=new ArrayList<FileDocument>();
    		FileOutputStream fileOuputStream = null;
    		try {
    			demoDocuments =fileDocumentRepository.findByEmailAndName(email, fileName);
    			if(demoDocuments.size()>0) {
    				Binary document = demoDocuments.get(0).getFile();
    			    String contentType=demoDocuments.get(0).getContentType();
    			    MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
    	    	    MimeType mime = allTypes.forName(contentType);
    				
    	    	    String ext = mime.getExtension();
    	    	    fileName="temp_file"+ext;
    	    	    fileOuputStream = new FileOutputStream(RETRIEVE_FOLDER +fileName );
    	            fileOuputStream.write(document.getData()); 
    	            Path p=Paths.get(RETRIEVE_FOLDER).toAbsolutePath().normalize();
    	            resourceVo.setResource(loadFileAsResource(fileName,p));
    	            resourceVo.setContentType(contentType);
    	            resourceVo.setFileName(fileName);
    				
    			}else {
    				
    				
    				return resourceVo;
    			}
    
    		} catch (MimeTypeException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return resourceVo;
    	}
    	
    	public Resource loadFileAsResource(String fileName,Path fileStorageLocation) throws FileNotFoundException {
    		Resource resource = null;
    		try {
    			Path filePath = fileStorageLocation.resolve(fileName).normalize();
    			resource = new UrlResource(filePath.toUri());
    			if (resource.exists()) {
    				return resource;
    			} else {
    				logger.error("File not found " + fileName);
    				throw new FileNotFoundException("File not found " + fileName);
    			}
    		} catch (MalformedURLException ex) {
    			logger.error("File not found " + fileName, ex);
    
    		}
    		return resource;
    	}
    	
    }



### create a interface FileDocumentRepository
it will extends the MongoRepository<K,T> class 

    package com.fileServiceApi.dao;
    
    import java.util.List;
    import java.util.Optional;
    
    import org.springframework.data.mongodb.repository.MongoRepository;
    import org.springframework.data.mongodb.repository.Query;
    
    import com.fileServiceApi.entities.FileDocument;
    
    public interface FileDocumentRepository extends MongoRepository<FileDocument, String> {
    
    	@Query("{ 'id' : ?0 }")
    	public Optional<FileDocument> findById(String id);
    	
    	@Query("{ 'emailId' : ?0 , 'fileName': ?1 }")
        public List<FileDocument> findByEmailAndName(String emailId, String fileName);
    	
    	
    }


### Create a custom exception FileNotFoundException

    package com.fileServiceApi.exception;

    import org.springframework.http.HttpStatus;
    import org.springframework.web.bind.annotation.ResponseStatus;
    
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class FileNotFoundException extends RuntimeException{
    
    	 /**
    	 * 
    	 */
    	private static final long serialVersionUID = 1L;
    
    	public FileNotFoundException(String message) {
    	        super(message);
    	    }
    
    	    public FileNotFoundException(String message, Throwable cause) {
    	        super(message, cause);
    	    }
    }


### For Adding AOP for logging purpose 

Add following dependency to your application

    <!-- https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-aop -->
	<dependency>
	    <groupId>org.springframework.boot</groupId>
	    <artifactId>spring-boot-starter-aop</artifactId>
	</dependency>

and add AOP configuration file in your java src package

    package com.fileServiceApi.config;

    import org.aspectj.lang.JoinPoint;
    import org.aspectj.lang.annotation.After;
    import org.aspectj.lang.annotation.Aspect;
    import org.aspectj.lang.annotation.Before;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.context.annotation.Configuration;
    
    @Aspect
    @Configuration
    public class LoggerAspect {
    	
    	private Logger logger = LoggerFactory.getLogger(this.getClass());
    	
    
    	@Before("execution(* com.fileServiceApi.*.*.*(..))")
    	public void before(JoinPoint joinPoint){
    		//Advice
    		logger.info("Entering in method {}", joinPoint);
    	}
    	
    	@After(value = "execution(* com.fileServiceApi.*.*.*(..))")
    	public void after(JoinPoint joinPoint) {
    		logger.info("After execution of {}", joinPoint);
    	}
    	
    }



