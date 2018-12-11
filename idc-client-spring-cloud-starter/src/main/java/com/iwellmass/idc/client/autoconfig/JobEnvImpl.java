package com.iwellmass.idc.client.autoconfig;

import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.JobEnv;
import com.iwellmass.idc.model.ScheduleType;

public class JobEnvImpl implements JobEnv {

	private Integer instanceId;

	private String jobId;

	private String jobGroup;

	private String jobName;

	private String loadDate;

	private ScheduleType scheduleType;

	private String parameter;

	private DispatchType dispatchType;

	private Long shouldFireTime;

	private Long prevFireTime;

	private String taskId;

	public Integer getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(Integer instanceId) {
		this.instanceId = instanceId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getJobGroup() {
		return jobGroup;
	}

	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(String loadDate) {
		this.loadDate = loadDate;
	}

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public DispatchType getDispatchType() {
		return dispatchType;
	}

	public void setDispatchType(DispatchType dispatchType) {
		this.dispatchType = dispatchType;
	}

	public Long getShouldFireTime() {
		return shouldFireTime;
	}

	public void setShouldFireTime(Long shouldFireTime) {
		this.shouldFireTime = shouldFireTime;
	}

	public Long getPrevFireTime() {
		return prevFireTime;
	}

	public void setPrevFireTime(Long prevFireTime) {
		this.prevFireTime = prevFireTime;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
