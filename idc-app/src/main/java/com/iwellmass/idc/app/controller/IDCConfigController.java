package com.iwellmass.idc.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iwellmass.common.ServiceResult;
import com.iwellmass.idc.model.IDCProp;

@RestController
@RequestMapping("/config")
public class IDCConfigController {

	@GetMapping
	public ServiceResult<List<IDCProp>> config() {
		return ServiceResult.failure("not supported yet.");
	}
	
	@PutMapping("/set")
	public ServiceResult<String> set(IDCProp prop) {
		return ServiceResult.failure("not supported yet.");
	}
	
}
