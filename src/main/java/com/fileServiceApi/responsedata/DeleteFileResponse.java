package com.fileServiceApi.responsedata;

public class DeleteFileResponse {

	private String fileName;
	private String operationStatus;
	private String message;
	
	public DeleteFileResponse(String fileName, String operationStatus, String message) {
		super();
		this.fileName = fileName;
		this.operationStatus = operationStatus;
		this.message = message;
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
	
	
	
	
}
