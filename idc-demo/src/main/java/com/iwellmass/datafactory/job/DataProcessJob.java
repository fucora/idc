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
		Long taskId = Long.parseLong(ps.get("taskId"));
		String loadDate = ps.get("loadDate");
		System.out.println(taskId + loadDate);
		iBasicExecuteContext.executeSucceed("success");
	}

}
