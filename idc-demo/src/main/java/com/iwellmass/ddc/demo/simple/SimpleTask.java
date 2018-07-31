package com.iwellmass.ddc.demo.simple;

import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.dispatcher.sdk.util.IBasicExecuteContext;

public class SimpleTask implements ITaskService{

	public void execute(String params, IBasicExecuteContext ctx) {
		System.out.println("simple task start ......");
		try {
			Thread.sleep(10 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("simple task end: " + params);
		ctx.executeSucceed();
	}
}
