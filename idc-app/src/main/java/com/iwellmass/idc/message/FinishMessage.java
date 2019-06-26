package com.iwellmass.idc.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FinishMessage extends TaskMessage {

	private static final long serialVersionUID = -8973355861973877439L;
	
	public FinishMessage(//@formatter:off
		@JsonProperty("id") String id,
		@JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, TaskEvent.FINISH);
	}
	
	public static final FinishMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new FinishMessage(id, batchNo);
	}
}
