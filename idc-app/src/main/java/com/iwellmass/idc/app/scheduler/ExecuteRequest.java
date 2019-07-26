package com.iwellmass.idc.app.scheduler;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteRequest {

	private String domain;
	private String contentType;
	private JobEnvAdapter jobEnvAdapter;
}
