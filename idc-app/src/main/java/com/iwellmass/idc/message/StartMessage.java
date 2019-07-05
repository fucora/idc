package com.iwellmass.idc.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 开始消息
 */
public class StartMessage extends JobMessage {

	private static final long serialVersionUID = -8973355861973877439L;
	
	public StartMessage(//@formatter:off
		@JsonProperty("id") String id,
		@JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.START);
	}
	
	
	
	public static final StartMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new StartMessage(id, batchNo);
	}
	
	
}
