package com.iwellmass.idc.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/13 14:08
 * @description 成功消息
 */
public class FinishMessage extends JobMessage {

	private static final long serialVersionUID = -461924759242824796L;

	public FinishMessage(//@formatter:off
						 @JsonProperty("id") String id,
						 @JsonProperty("batchNo") String batchNo) {//@formatter:on
		super(id, batchNo, JobEvent.FINISH);
	}
	
	public static final FinishMessage newMessage(String batchNo) {
		String id = UUID.randomUUID().toString();
		return new FinishMessage(id, batchNo);
	}
}
