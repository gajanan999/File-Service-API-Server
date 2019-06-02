package com.fileServiceApi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * This Class has been created for to fetch and set the upload directory for File Storage
 * @author gajagaik
 *
 */
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
