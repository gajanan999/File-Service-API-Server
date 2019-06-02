package com.fileServiceApi.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;

public interface FileStorageDao {


	 public String store(InputStream inputStream, String fileName,
	   String contentType, DBObject metaData);
	 
	 public ResponseEntity<Resource> retrive(String fileName) throws IOException;
	 
	 public GridFSDBFile getById(String id);
	 
	 public GridFSDBFile getByFilename(String filename);
	 
	 public List findAll();

}
