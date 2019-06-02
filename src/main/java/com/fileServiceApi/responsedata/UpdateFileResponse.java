package com.fileServiceApi.responsedata;

/**
 * Update File Operation Response
 * @author gajagaik
 *
 */
public class UpdateFileResponse {

	private String fileName;
	private String operationStatus;
	private String message;
	private String fileDownloadUri;

	public UpdateFileResponse(String fileName, String operationStatus, String message, String fileDownloadUri) {
		super();
		this.fileName = fileName;
		this.operationStatus = operationStatus;
		this.message = message;
		this.fileDownloadUri = fileDownloadUri;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOperationStatus() {
		return operationStatus;
	}

	public void setOperationStatus(String operationStatus) {
		this.operationStatus = operationStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFileDownloadUri() {
		return fileDownloadUri;
	}

	public void setFileDownloadUri(String fileDownloadUri) {
		this.fileDownloadUri = fileDownloadUri;
	}

}
