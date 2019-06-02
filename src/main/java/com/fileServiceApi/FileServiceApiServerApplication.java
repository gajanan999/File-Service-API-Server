package com.fileServiceApi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.fileServiceApi.config.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties({ StorageProperties.class })
public class FileServiceApiServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileServiceApiServerApplication.class, args);
	}

}
