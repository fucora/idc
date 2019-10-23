package com.iwellmass.idc.executor;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.model.JobInstanceStatus;

import java.time.LocalDateTime;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;

public class CompleteEvent implements IDCJobEvent {

    private static final long serialVersionUID = -2050270529918044581L;

    private LocalDateTime endTime;

    private JobInstanceStatus finalStatus;

    private String nodeJobId;

    private String message;

    private Throwable throwable;

    private String nodeTaskName;

    // protected
    private CompleteEvent() {
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public CompleteEvent setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public CompleteEvent setFinalStatus(JobInstanceStatus finalStatus) {
        this.finalStatus = finalStatus;
        return this;
    }

    public JobInstanceStatus getFinalStatus() {
        return finalStatus;
    }

    public String getNodeJobId() {
        return nodeJobId;
    }

    public String getMessage() {
        return message;
    }

    public CompleteEvent setMessage(String message) {
        this.message = message;
        return this;
    }

    public CompleteEvent setMessage(String message, Object... args) {
        return setMessage(arrayFormat(message, args).getMessage());
    }

    public static CompleteEvent successEvent(String nodeJobId, String nodeTaskName) {
        CompleteEvent event = new CompleteEvent();
        event.nodeJobId = nodeJobId;
        event.nodeTaskName = nodeTaskName;
        event.finalStatus = JobInstanceStatus.FINISHED;
        event.endTime = LocalDateTime.now();
        event.setMessage("执行成功");
        return event;
    }

    public static CompleteEvent failureEvent(String nodeJobId, String nodeTaskName) {
        CompleteEvent event = new CompleteEvent();
        event.nodeJobId = nodeJobId;
        event.nodeTaskName = nodeTaskName;
        event.finalStatus = JobInstanceStatus.FAILED;
        event.endTime = LocalDateTime.now();
        event.setMessage("执行失败");
        return event;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public CompleteEvent setThrowable(Throwable throwable) {
        this.throwable = throwable;
        return this;
    }

    @Override
    public String toString() {
        return "CompleteEvent [endTime=" + endTime + ", finalStatus=" + finalStatus + ", nodeJobId=" + nodeJobId
                + ", message=" + message + "]";
    }
}
