# File Service API

**File Upload, Delete, Download and Update operation using Spring Boot REST API**

File Service API Server 


In this article, I have developed the RESTful spring boot web service. I have created the the spring boot application with REST API which will upload, delete, download & update
the file in the File storage. currently I have used local storage to store a file 

Spring client uses HTTP Multipart requests to store and update file on the server. for that we have do some configuration in server application.properties as follows

**src/main/resources/application.properties**
    
    ## MULTIPART (MultipartProperties)
    # Enable multipart uploads
    spring.servlet.multipart.enabled=true
    # Threshold after which files are written to disk.
    spring.servlet.multipart.file-size-threshold=2KB
    # Max file size.
    spring.servlet.multipart.max-file-size=200MB
    # Max Request Size
    spring.servlet.multipart.max-request-size=215MB
    

## File Storage Properties
**src/main/resources/application.properties**

    # All files uploaded through the REST API will be stored in this directory 
    file.upload-dir=/Users/gajagaik/FileStorage/


Create a Pojo class StorageProperties.java to get location of Upload Directory in current local system i.e Server 

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



