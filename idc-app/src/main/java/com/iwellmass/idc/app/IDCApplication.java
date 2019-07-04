package com.iwellmass.idc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.scheduler.SchedulerConfig;

/**
 * 启动吧
 */
@SpringCloudApplication
@EnableFeignClients
@Configuration
@EnableResourceServer
@Import({ SchedulerConfig.class })
public class IDCApplication {

	static final Logger LOGGER = LoggerFactory.getLogger(IDCApplication.class);
	
	public static void main(String[] args) {
		new SpringApplicationBuilder(IDCApplication.class).web(WebApplicationType.SERVLET).run(args);
	}

	@ControllerAdvice
	public class ExceptionAdvice {

		@ExceptionHandler(AppException.class)
		@ResponseBody
		public ServiceResult<String> exception(AppException e) {
			LOGGER.error(e.getMessage());
			return ServiceResult.failure(e.getMessage());
		}
		
		@ExceptionHandler(Exception.class)
		@ResponseBody
		public ServiceResult<String> otherException(Exception e) {
			LOGGER.error(e.getMessage(), e);
			return ServiceResult.failure(e.getMessage());
		}
	}
}
