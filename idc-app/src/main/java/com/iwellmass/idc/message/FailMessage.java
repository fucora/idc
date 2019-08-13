package com.iwellmass.idc.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/13 14:08
 * @description 失败消息
 */
public class FailMessage extends JobMessage {

	private static final long serialVersionUID = -2251630353833441320L;

	public FailMessage(//@formatter:off
					   @JsonProperty("id") String id,
					   @JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.FAIL);
	}
	
	public static final FailMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new FailMessage(id, batchNo);
	}

	public void setMessage(String message, Throwable e) {
		setMessage(message);
	}
}
