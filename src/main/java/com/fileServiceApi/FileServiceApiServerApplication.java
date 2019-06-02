package com.fileServiceApi;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.fileServiceApi.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    StorageProperties.class
})
public class FileServiceApiServerApplication{

	public static void main(String[] args) {
		SpringApplication.run(FileServiceApiServerApplication.class, args);
	}

	
	 
}
