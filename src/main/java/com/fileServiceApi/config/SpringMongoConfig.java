package com.fileServiceApi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import com.mongodb.MongoClient;

public class SpringMongoConfig extends AbstractMongoConfiguration{
	
	@Value("mongo-database")
	private String database;

	@Override
	public MongoClient mongoClient() {
		// TODO Auto-generated method stub
		 return new MongoClient("localhost");
	}

	@Override
	protected String getDatabaseName() {
		// TODO Auto-generated method stub
		return "fileStorage";
	}
	
	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
	    return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
	}

	@Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), database);
    }
}
