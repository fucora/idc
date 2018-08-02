package com.iwellmass.dispatcher.common.context;

import java.util.Map;

public class JVMContext {

	private static volatile String ip;

	private static volatile int port;

	private Map<String, String> alarmKeys;

	public static String getIp() {
		return ip;
	}

	public static void setIp(String ip) {
		JVMContext.ip = ip;
	}

	public static int getPort() {
		return port;
	}

	public static void setPort(int port) {
		JVMContext.port = port;
	}

	public Map<String, String> getAlarmKeys() {
		return alarmKeys;
	}

	public void setAlarmKeys(Map<String, String> alarmKeys) {
		this.alarmKeys = alarmKeys;
	}

}
