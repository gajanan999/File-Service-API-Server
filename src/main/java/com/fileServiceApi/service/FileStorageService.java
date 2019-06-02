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
import com.fileServiceApi.exception.FileNotFoundException;
import com.fileServiceApi.exception.FileStorageException;

/**
 * This is service class which is used to handle the business logic in our case it is nothing but File Conversion
 * @author gajagaik
 *
 */
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
			 throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

	public String storeFile(MultipartFile file) {
		// Normalize file name
		String fileName = StringUtils.cleanPath(file.getOriginalFilename());
		logger.info(" FileName: ", fileName);
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
				throw new FileNotFoundException("File not found " + fileName);

			}
		} catch (MalformedURLException ex) {
			logger.error("File not found " + fileName, ex);
			throw new FileNotFoundException("File not found " + fileName, ex);
		}

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

	/*
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
