package com.iwellmass.idc.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FailMessage extends JobMessage {

	private static final long serialVersionUID = -8973355861973877439L;
	
	public FailMessage(//@formatter:off
		@JsonProperty("id") String id,
		@JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, TaskEvent.FAIL);
	}
	
	public static final FailMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new FailMessage(id, batchNo);
	}

	public void setMessage(String message, Throwable e) {
		setMessage(message);
	}
}
