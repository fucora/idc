package com.iwellmass.dispatcher.thrift.bvo;

import java.util.ArrayList;
import java.util.List;

/**
 * 桥接 IDC className 与 Job taskType，
 */
public class TaskTypeHelper {

	private static final List<String> LIST = new ArrayList<>();

	static {
		LIST.add("SPQRK_SQL=com.iwellmass.idc.XXX");
		LIST.add("同步任务=com.iwellmass.idc.YYY");
	}

	public static final String classTypeOf(String taskType) {

		StringBuilder sb = new StringBuilder();

		LIST.stream().filter(s -> s.startsWith(taskType + "=")).findFirst().ifPresent(s -> {
			sb.append(s.trim().substring(s.indexOf("=") + 1));
		});

		return sb.toString();
	}

	public static final String taskTypeOf(String className) {
		StringBuilder sb = new StringBuilder();

		LIST.stream().filter(s -> s.endsWith("=" + className)).findFirst().ifPresent(s -> {
			sb.append(s.substring(0, s.indexOf("=")));
		});

		return sb.toString();
	}

	public static void main(String[] args) {
		System.out.println(classTypeOf("SPQRK_SQL"));
		System.out.println(taskTypeOf("com.iwellmass.idc.YYY"));
	}

}
