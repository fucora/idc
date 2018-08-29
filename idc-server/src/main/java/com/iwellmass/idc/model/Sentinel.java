package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.iwellmass.idc.quartz.IDCPlugin;

@Entity
@IdClass(SentinelPK.class)
@Table(name = "t_idc_sentinel")
public class Sentinel {

	private String taskId;

	private String groupId;

	private Long loadDate;

	private SentinelStatus status;

	@Id
	@Column(name = IDCPlugin.COL_SENTINEL_TASK_ID, length = 50)
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@Id
	@Column(name = IDCPlugin.COL_SENTINEL_GROUP_ID, length = 50)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String gorupId) {
		this.groupId = gorupId;
	}

	@Id
	@Column(name = IDCPlugin.COL_SENTINEL_LOAD_DATE)
	public Long getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(Long loadDate) {
		this.loadDate = loadDate;
	}

	@Column(name = IDCPlugin.COL_SENTINEL_STATUS)
	public SentinelStatus getStatus() {
		return status;
	}

	public void setStatus(SentinelStatus status) {
		this.status = status;
	}

}
