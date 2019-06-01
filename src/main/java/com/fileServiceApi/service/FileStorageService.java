package com.fileServiceApi.service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fileServiceApi.FileServiceApiServerApplication;
import com.fileServiceApi.config.StorageProperties;

@Service
public class FileStorageService {

	private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
	
	private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(StorageProperties storageProperties) {
        this.fileStorageLocation = Paths.get(storageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
        	 logger.error(ex.getMessage(), ex);
        }
    }
    
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (file.isEmpty()) {
        	 logger.info("file is empty");
        }else {
        	byte[] bytes;
			try {
				bytes = file.getBytes();
				Path path = Paths.get(this.fileStorageLocation + "/"+file.getOriginalFilename());
	            Files.write(path, bytes);
	            logger.info(String.valueOf(bytes));
			} catch (IOException ex) {
				// TODO Auto-generated catch block
				 logger.error(ex.getMessage(), ex);
			}
             
        }      
        return fileName;
    }
    
   
    
}
