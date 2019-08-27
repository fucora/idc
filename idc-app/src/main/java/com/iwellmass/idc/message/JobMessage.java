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
        @Type(name = "START", value = StartMessage.class),
        @Type(name = "FINISH", value = FinishMessage.class),
        @Type(name = "FAIL", value = FailMessage.class),
        @Type(name = "REDO", value = RedoMessage.class),
        @Type(name = "CANCEL", value = CancelMessage.class),
        @Type(name = "SKIP", value = SkipMessage.class),
        @Type(name = "READY", value = ReadyMessage.class),
        @Type(name = "RUNNING", value = RunningMessage.class),
        @Type(name = "TIMEOUT", value = TimeoutMessage.class)
})
@Getter
@Setter
@ToString(of = {"jobId", "event", "message"})
public abstract class JobMessage implements Serializable {

    private static final long serialVersionUID = -7298110033212656554L;

    private final String id;

    private final String jobId;

    private final JobEvent event;

    private String message;

    private StackTraceElement[] stackTraceElements;

    public JobMessage(String id, String jobId, JobEvent event) {
        this(id, jobId, event, null);
    }

    public JobMessage(String id, String jobId, JobEvent event, StackTraceElement[] stackTraceElements) {
        this.id = id;
        this.jobId = jobId;
        this.event = event;
        this.stackTraceElements = stackTraceElements;
    }

}
