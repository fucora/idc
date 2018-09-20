package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import io.swagger.annotations.ApiOperation;

@Entity
@Table(name = "t_idc_dependency")
@IdClass(JobDependencyPK.class)
public class JobDependency {
	
	private String srcTaskId;
	
	private String srcGroupId;

	private String taskId;

	private String groupId;

	@Id
	@Column(name = "src_task_id", length = 50)
	public String getSrcTaskId() {
		return srcTaskId;
	}

	public void setSrcTaskId(String srcTaskId) {
		this.srcTaskId = srcTaskId;
	}

	@Id
	@Column(name = "src_group_id", length = 50)
	public String getSrcGroupId() {
		return srcGroupId;
	}

	public void setSrcGroupId(String srcGroupId) {
		this.srcGroupId = srcGroupId;
	}

	@Id
	@Column(name = "task_id", length = 50)
	@ApiOperation("依赖的 taskId")
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String srcTaskId) {
		this.taskId = srcTaskId;
	}

	@Id
	@Column(name = "group_id", length = 50)
	@ApiOperation("依赖的 groupId")
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String srcGroup) {
		this.groupId = srcGroup;
	}

}
