package com.iwellmass.idc.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/13 14:08
 * @description 取消消息
 */
public class RunningMessage extends JobMessage {

	private static final long serialVersionUID = 7346330219811900560L;

	public RunningMessage(//@formatter:off
						  @JsonProperty("id") String id,
						  @JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.RUNNING);
	}
	
	public static final RunningMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new RunningMessage(id, batchNo);
	}

	public void setMessage(String message, Throwable e) {
		setMessage(message);
	}
}
