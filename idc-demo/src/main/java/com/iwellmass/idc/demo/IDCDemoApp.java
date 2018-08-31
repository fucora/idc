package com.iwellmass.idc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.iwellmass.idc.executor.IDCJob;

@SpringBootApplication
public class IDCDemoApp {

	public static void main(String[] args) {
		SpringApplication.run(IDCDemoApp.class, args);
	}
	
	@Bean("moody-job")
	public IDCJob moodyJob() {
		return new MoodyJob();
	}
}
