package com.fileServiceApi.daoImpl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import com.fileServiceApi.dao.FileStorageDao;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFSDBFile;


@Repository
public class FileStorageDaoImpl implements FileStorageDao {
	
	private static final Logger logger = LoggerFactory.getLogger(FileStorageDaoImpl.class);
	 @Autowired
	 GridFsTemplate gridFsTemplate;
	 
	 @Autowired
	 GridFsOperations gridFsOperations;

	@Override
	public String store(InputStream inputStream, String fileName, String contentType, DBObject metaData) {
		logger.info("Entering in store method");
		gridFsTemplate
	    .store(inputStream, fileName, contentType, metaData);	
		logger.info("Exiting in store method");
		return "Success";
	}

	@Override
	public ResponseEntity<Resource> retrive(String fileName) throws IOException {
		// TODO Auto-generated method stub
		logger.info("Exiting in store method");
		GridFSFile file= gridFsTemplate.findOne(new 
				Query(Criteria.where("filename").is(fileName)));
		
		Resource resource=gridFsTemplate.getResource(file);
		return ResponseEntity.ok()
	            .contentLength(resource.contentLength())
	            .body(gridFsTemplate.getResource(file));
	}

	@Override
	public GridFSDBFile getById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GridFSDBFile getByFilename(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
