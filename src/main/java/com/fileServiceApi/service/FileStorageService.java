package com.fileServiceApi.service;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;


import com.fileServiceApi.FileServiceApiServerApplication;
import com.fileServiceApi.config.StorageProperties;

@Service
public class FileStorageService {

	private static final Logger logger = LoggerFactory.getLogger(FileServiceApiServerApplication.class);
	
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
    
    
    
}
