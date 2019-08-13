package com.iwellmass.idc.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/13 14:08
 * @description 开始消息
 */
public class StartMessage extends JobMessage {

	private static final long serialVersionUID = -7277635061263478146L;

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
