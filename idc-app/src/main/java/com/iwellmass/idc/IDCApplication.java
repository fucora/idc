package com.iwellmass.idc;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.exception.AppException;

@SpringBootApplication
@Import(IDCServerConfiguration.class)
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
