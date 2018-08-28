package com.iwellmass.idc.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@IdClass(JobExecutionBarrierPK.class)
@Table(name = "t_idc_execution_barrier")
public class JobExecutionBarrier {

	private String instanceId;

	private String taskId;

	private String gorupId;

	private LocalDateTime loadDate;
	
	private BarrierFlag flag;

	@Id
	@Column(name = "instance_id")
	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	@Id
	@Column(name = "task_id", length = 50)
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Id
	@Column(name = "group_id", length = 50)
	public String getGorupId() {
		return gorupId;
	}

	public void setGorupId(String gorupId) {
		this.gorupId = gorupId;
	}

	@Id
	@Column(name = "load_date")
	public LocalDateTime getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(LocalDateTime loadDate) {
		this.loadDate = loadDate;
	}

	@Column(name = "flag")
	public BarrierFlag getFlag() {
		return flag;
	}

	public void setFlag(BarrierFlag flag) {
		this.flag = flag;
	}

}
