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
			// Load file as Resource
			resource = fileStorageService.loadFileAsResource(fileName);

			// Try to determine file's content type

			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (IOException ex) {
				logger.info("Could not determine file type.");
			}

		}
		// Fallback to the default content type if type could not be determined
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