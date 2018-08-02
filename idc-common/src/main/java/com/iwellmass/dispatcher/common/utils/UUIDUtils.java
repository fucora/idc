package com.iwellmass.dispatcher.common.utils;

import java.util.UUID;

/**
 * UUID工具类
 * @author Ming.Li
 *
 */
public class UUIDUtils {
	
	public static String newUuid() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}
	
	public static void main(String[] args) {
		System.out.println(newUuid());
	}
}
