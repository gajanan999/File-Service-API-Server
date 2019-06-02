package com.fileServiceApi.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.fileServiceApi.entities.FileDocument;

public interface FileDocumentRepository extends MongoRepository<FileDocument, String> {

	@Query("{ 'id' : ?0 }")
	public Optional<FileDocument> findById(String id);
	
	@Query("{ 'emailId' : ?0 , 'fileName': ?1 }")
    public List<FileDocument> findByEmailAndName(String emailId, String fileName);
	
	
}
