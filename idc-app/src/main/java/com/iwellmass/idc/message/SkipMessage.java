package com.iwellmass.idc.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/13 14:08
 * @description 跳过消息
 */
public class SkipMessage extends JobMessage {

	private static final long serialVersionUID = -8876144121388923451L;

	public SkipMessage(//@formatter:off
					   @JsonProperty("id") String id,
					   @JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.SKIP);
	}
	
	public static final SkipMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new SkipMessage(id, batchNo);
	}

	public void setMessage(String message, Throwable e) {
		setMessage(message);
	}
}
