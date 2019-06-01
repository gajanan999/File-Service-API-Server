# File Service API

**File Upload, Delete, Download and Update operation using Spring Boot REST API**

File Service API Server 


In this article, I have developed the RESTful spring boot web service. I have created the the spring boot application with REST API which will upload, delete, download & update
the file in the File storage. currently I have used local storage to store a file 

Spring client uses Multipart requests to store and update file on the server. 

**src/main/resources/application.properties**

    # All files uploaded through the REST API will be stored in this directory 
    file.upload-dir=/Users/gajagaik/FileStorage/




