package com.iwellmass.datafactory.job;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.dispatcher.sdk.util.IBasicExecuteContext;

public class DataProcessJob implements ITaskService {

	public static String LOAD_DATE = "loadDate";

	@SuppressWarnings("unchecked")
	public void execute(String params, IBasicExecuteContext iBasicExecuteContext) {
		Map<String, String> ps = JSON.parseObject(params, Map.class);
		System.out.println(ps);
		iBasicExecuteContext.executeSucceed("success");
	}

}
