package com.iwellmass.idc.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/13 14:08
 * @description 取消消息
 */
public class ReadyMessage extends JobMessage {

	private static final long serialVersionUID = -2740056254576029854L;

	public ReadyMessage(//@formatter:off
						@JsonProperty("id") String id,
						@JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.READY);
	}
	
	public static final ReadyMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new ReadyMessage(id, batchNo);
	}

	public void setMessage(String message, Throwable e) {
		setMessage(message);
	}
}
