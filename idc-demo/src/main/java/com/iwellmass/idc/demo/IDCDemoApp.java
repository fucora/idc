package com.iwellmass.idc.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.context.annotation.Bean;

import com.iwellmass.idc.executor.IDCJob;

@SpringCloudApplication
public class IDCDemoApp {

	public static void main(String[] args) {
		SpringApplication.run(IDCDemoApp.class, args);
	}
	
	@Bean
	public IDCJob moodyJob() {
		return new MoodyJob("moody");
	}
}
