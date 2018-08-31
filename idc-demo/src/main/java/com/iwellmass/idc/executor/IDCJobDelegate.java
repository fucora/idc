package com.iwellmass.idc.executor;

import org.springframework.web.bind.annotation.ResponseBody;

public class IDCJobDelegate {

	private IDCJob job;

	public IDCJobDelegate(IDCJob job) {
		this.job = job;
	}

	@ResponseBody
	public String execution() {

		DefaultIDCExecutionContext context = new DefaultIDCExecutionContext();

		try {
			job.execute(context);

		} catch (Throwable e) {

		}

		return "hello: " + hashCode() + " >>" + hashCode();
	}

}
