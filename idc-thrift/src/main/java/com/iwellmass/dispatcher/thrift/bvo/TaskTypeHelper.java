package com.iwellmass.dispatcher.thrift.bvo;

import java.util.ArrayList;
import java.util.List;

/**
 * 桥接 IDC className 与 Job taskType，
 */
public class TaskTypeHelper {

	private static final List<String> LIST = new ArrayList<>();

	public static final String SPARK_SQL = "SPARK_SQL";
	public static final String SCALA = "SCALA";
	public static final String SPARK_APP = "SPARK_APP";
	public static final String DATA_SYNC = "DATA_SYNC";
	
	static {
		LIST.add("SPARK_SQL=com.iwellmass.datafactory.job.DataProcessJob");
		LIST.add("SCALA=com.iwellmass.datafactory.job.DataProcessJob");
		LIST.add("SPARK_APP=com.iwellmass.datafactory.job.DataProcessJob");
		LIST.add("DATA_SYNC=com.iwellmass.datafactory.job.DataSyncJob");
	}

	public static final String classNameOf(String contentType) {

		StringBuilder sb = new StringBuilder();

		LIST.stream().filter(s -> s.startsWith(contentType + "=")).findFirst().ifPresent(s -> {
			sb.append(s.trim().substring(s.indexOf("=") + 1));
		});

		return sb.toString();
	}

	public static final String contentTypeOf(String className) {
		StringBuilder sb = new StringBuilder();

		LIST.stream().filter(s -> s.endsWith("=" + className)).findFirst().ifPresent(s -> {
			sb.append(s.substring(0, s.indexOf("=")));
		});

		return sb.toString();
	}

	public static boolean isDataSyncJob(String taskType) {
		return DATA_SYNC.equals(taskType);
	}
}
