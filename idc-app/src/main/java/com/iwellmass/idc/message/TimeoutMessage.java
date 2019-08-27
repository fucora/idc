package com.iwellmass.idc.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/27 14:49
 * @description 回调超时消息
 */
public class TimeoutMessage extends JobMessage {

	private static final long serialVersionUID = -3906769148931158455L;

	public TimeoutMessage(//@formatter:off
						  @JsonProperty("id") String id,
						  @JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.TIMEOUT);
	}
	
	public static final TimeoutMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new TimeoutMessage(id, batchNo);
	}

	public void setMessage(String message, Throwable e) {
		setMessage(message);
	}
}
