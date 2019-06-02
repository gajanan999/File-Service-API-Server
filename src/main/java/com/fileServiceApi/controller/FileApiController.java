package com.fileServiceApi.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fileServiceApi.responsedata.DeleteFileResponse;
import com.fileServiceApi.responsedata.UpdateFileResponse;
import com.fileServiceApi.responsedata.UploadFileResponse;
import com.fileServiceApi.service.FileStorageService;


/**
 * This Class is nothing but the Rest Controller which handles the HTTP request from the client
 * @author gajagaik
 *
 */
@RestController
@RequestMapping("/api/")
public class FileApiController {

	@Autowired
	FileStorageService fileStorageService;

	private static final Logger logger = LoggerFactory.getLogger(FileApiController.class);

	@Value("${FILE_UPLOAD}")
	private String FILE_UPLOAD;

	@Value("${SUCCESS}")
	private String SUCCESS;

	@Value("${NOT_AUTHORIZED}")
	private String NOT_AUTHORIZED;

	@Value("${FAILED}")
	private String FAILED;

	@Value("${UPDATE_FAIL}")
	private String UPDATE_FAIL;

	@Value("${FILE_NOT_EXISTS}")
	private String FILE_NOT_EXISTS;

	/**
	 * Upload a new File in the File Storage using @PostMapping
	 * 
	 * @param file
	 * @return
	 */
	@PostMapping(value = "upload", consumes = { "multipart/form-data" })
	public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {

		String fileName = fileStorageService.storeFile(file);
		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileName).toUriString();
		return new UploadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	/**
	 * Download the File from File Storage using @GetMapping
	 * 
	 * @param fileName
	 * @param request
	 * @return
	 */
	@GetMapping("download/{fileName:.+}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
		
		String contentType = null;
		Resource resource = null;
		String file = "";
		if (fileStorageService.checkFileExists(fileName)) {
			resource = fileStorageService.loadFileAsResource(fileName);
			try {
				contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
			} catch (IOException ex) {
				logger.info("Could not determine file type.", ex);
			}
		}
		if (contentType == null) {
			contentType = "application/octet-stream";
		}
		file = null != resource ? "resource.getFilename()" : fileName + " is Not Exists";
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file + "\"").body(resource);
	}

	/**
	 * Delete the file from FileStorage using @DeleteMapping- HTTP Method
	 * 
	 * @param fileName
	 * @return
	 */
	@DeleteMapping("delete/{fileName:.+}")
	public DeleteFileResponse deleteFile(@PathVariable String fileName) {
		String message = "";
		String operationStatus = "";
		if (fileStorageService.checkFileExists(fileName)) {
			if (fileStorageService.deleteFile(fileName)) {
				message = "File Deleted Successfully";
				operationStatus = "SUCCESS";
			}
		} else {
			message = FILE_NOT_EXISTS;
			operationStatus = FAILED;
		}
		return new DeleteFileResponse(fileName, operationStatus, message);
	}

	/**
	 * Update a file in the file storage with new file which is passed in HTTP : PUT
	 * method call
	 * 
	 * @param file
	 * @param fileName
	 * @return
	 */
	@PutMapping("update")
	public UpdateFileResponse updateFile(@RequestParam("file") MultipartFile file,
			@RequestParam("fileName") String fileName) {
		String message = "";
		String operationStatus = "";
		String fileDownloadUri = "";
		String updatedFileName = "";

		if (fileStorageService.checkFileExists(fileName)) {
			if (fileStorageService.deleteFile(fileName)) {
				updatedFileName = fileStorageService.storeFile(file);
				fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
						.path(fileName).toUriString();
				message = FILE_UPLOAD;
				operationStatus = SUCCESS;
			} else {
				message = NOT_AUTHORIZED;
				operationStatus = FAILED;
			}
		} else {
			message = UPDATE_FAIL;
			operationStatus = FAILED;
		}
		return new UpdateFileResponse(updatedFileName, operationStatus, message, fileDownloadUri);
	}

}
