package com.iwellmass.idc.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonTypeInfo(use = Id.NAME, property = "event", visible = true, include = As.EXISTING_PROPERTY)
@JsonSubTypes({ 
	@Type(name = "START", value = RenewMessage.class),
	@Type(name = "RENEW", value = RenewMessage.class),
	@Type(name = "FINISH", value = FinishMessage.class),
	@Type(name = "FAIL", value = FailMessage.class)
	})
@Getter
@Setter
@ToString(of = { "jobId", "event", "message" })
public abstract class JobMessage implements Serializable {

	private static final long serialVersionUID = -1153750416491653924L;

	private final String id;

	private final String jobId;

	private final JobEvent event;

	private String message;

	public JobMessage(String id, String jobId, JobEvent event) {
		this.id = id;
		this.jobId = jobId;
		this.event = event;
	}

}
