package com.fileServiceApi.vo;

import org.springframework.core.io.Resource;

/**
 * This class is nothing but just helper class for handle multiple values from service to controller
 * @author gajagaik
 *
 */
public class ResourceVo {

	private Resource resource;
	private String contentType;
	private String fileName;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
