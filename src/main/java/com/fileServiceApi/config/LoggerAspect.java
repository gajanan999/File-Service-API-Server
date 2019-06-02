package com.fileServiceApi.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class LoggerAspect {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	

	@Before("execution(* com.fileServiceApi.*.*.*(..))")
	public void before(JoinPoint joinPoint){
		//Advice
		logger.info("Entering in method {}", joinPoint);
	}
	
	@After(value = "execution(* com.fileServiceApi.*.*.*(..))")
	public void after(JoinPoint joinPoint) {
		logger.info("After execution of {}", joinPoint);
	}
	
}
