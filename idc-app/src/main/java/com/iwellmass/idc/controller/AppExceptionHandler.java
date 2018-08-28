package com.iwellmass.idc.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.common.exception.AppException;

@ControllerAdvice
public class AppExceptionHandler {

	@ExceptionHandler(AppException.class)
	@ResponseBody
	public ServiceResult<String> exception(AppException e) {
		return ServiceResult.failure(e.getMessage());
		
	}
}
