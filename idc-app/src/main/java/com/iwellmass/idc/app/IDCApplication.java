package com.iwellmass.idc.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.exception.AppException;

@SpringCloudApplication
@EnableFeignClients
@Configuration
@EnableResourceServer
@EnableJpaRepositories("com.iwellmass.idc.repo")
@EntityScan("com.iwellmass.idc.model")
public class IDCApplication {
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(IDCApplication.class).web(true).run(args);
	}

	@ControllerAdvice
	public class ExceptionAdvice {

		@ExceptionHandler(AppException.class)
		@ResponseBody
		public ServiceResult<String> exception(AppException e) {
			return ServiceResult.failure(e.getMessage());
		}
	}
}
