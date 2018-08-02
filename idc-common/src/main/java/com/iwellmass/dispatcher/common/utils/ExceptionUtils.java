package com.iwellmass.dispatcher.common.utils;

import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.task.DmallTask;

/**
 * 异常信息工具类
 * @author duheng
 *
 */
public class ExceptionUtils {

	public static void dealErrorInfo(String format, Object... args) throws DDCException {
		
		String message = String.format(format, args);
		DmallTask.getLogger().error(message);
		throw new DDCException(message);
	}
}
