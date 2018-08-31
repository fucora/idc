package com.iwellmass.idc.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;

import com.iwellmass.idc.executor.IDCJob;

public class SimpleIdcApp {

	public static void main(String[] args) {
		SpringApplication.run(SimpleIdcApp.class, args);
	}
	
	@Bean
	private IDCJob idcJob() {
		return new SimpleJob();
	}
}
