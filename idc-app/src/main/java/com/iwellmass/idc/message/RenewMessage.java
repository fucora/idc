package com.iwellmass.idc.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RenewMessage extends TaskMessage {

	private static final long serialVersionUID = -8973355861973877439L;
	
	public RenewMessage(//@formatter:off
		@JsonProperty("id") String id,
		@JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, TaskEvent.RENEW);
	}
	
	
	
	public static final RenewMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new RenewMessage(id, batchNo);
	}
	
	
}
