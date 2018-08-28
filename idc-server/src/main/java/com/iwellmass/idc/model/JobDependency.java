package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "t_idc_dependency")
@IdClass(JobDependencyPK.class)
public class JobDependency {

	private String taskId;

	private String groupId;

	private String dependencyTaskId;

	private String dependencyGroupId;
	
	@Id
	@Column(name = "task_id", length = 50)
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String srcTaskId) {
		this.taskId = srcTaskId;
	}

	@Id
	@Column(name = "group_id", length = 50)
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String srcGroup) {
		this.groupId = srcGroup;
	}

	@Id
	@Column(name = "dependency_task_id", length = 50)
	public String getDependencyTaskId() {
		return dependencyTaskId;
	}

	public void setDependencyTaskId(String tgtTaskId) {
		this.dependencyTaskId = tgtTaskId;
	}

	@Id
	@Column(name = "dependency_group_id", length = 50)
	public String getDependencyGroupId() {
		return dependencyGroupId;
	}

	public void setDependencyGroupId(String dependencyGroupId) {
		this.dependencyGroupId = dependencyGroupId;
	}

}
