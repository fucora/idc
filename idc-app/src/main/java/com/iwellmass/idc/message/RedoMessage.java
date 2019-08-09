package com.iwellmass.idc.message;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * 重跑消息
 */
public class RedoMessage extends JobMessage {

	private static final long serialVersionUID = 3590156527292607345L;

	public RedoMessage(//@formatter:off
					   @JsonProperty("id") String id,
					   @JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.REDO);
	}
	
	public static final RedoMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new RedoMessage(id, batchNo);
	}

	public void setMessage(String message, Throwable e) {
		setMessage(message);
	}
}
