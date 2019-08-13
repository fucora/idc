package com.iwellmass.idc.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/13 14:08
 * @description 取消消息
 */
public class CancelMessage extends JobMessage {

	private static final long serialVersionUID = 3329308891751572474L;

	public CancelMessage(//@formatter:off
						 @JsonProperty("id") String id,
						 @JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.CANCEL);
	}
	
	public static final CancelMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new CancelMessage(id, batchNo);
	}

	public void setMessage(String message, Throwable e) {
		setMessage(message);
	}
}
