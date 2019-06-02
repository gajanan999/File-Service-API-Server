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
	
	public Resource loadFileAsResource(String fileName,Path fileStorageLocation) {
		Resource resource = null;
		try {
			Path filePath = fileStorageLocation.resolve(fileName).normalize();
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
	
}
