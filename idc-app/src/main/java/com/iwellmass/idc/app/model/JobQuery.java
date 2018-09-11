package com.iwellmass.idc.app.model;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiModelProperty;

public class JobQuery {

	@ApiModelProperty("任务名")
	private String taskName;

	@ApiModelProperty("任务类型")
	private String contentType;

	@ApiModelProperty("节点类型")
	private List<TaskType> taskTypes;

	@ApiModelProperty("调度类型类型")
	private ScheduleType scheduleType;

	@ApiModelProperty("负责人")
	private String assignee;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public List<TaskType> getTaskTypes() {
		return taskTypes;
	}

	public void setTaskTypes(List<TaskType> taskTypes) {
		this.taskTypes = taskTypes;
	}

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}
	
	public static Specification<Job> taskNameLike(String taskName) {
		return (root, query, cb) -> {
			return cb.like(root.get("taskName"), taskName);
		};
	}

	public static Specification<Job> contentTypeEq(String content) {
		return (root, query, cb) -> {
			return cb.equal(root.get("assignee"), content);
		};
	}

	public static Specification<Job> taskTypeIn(List<TaskType> taskType) {
		return (root, query, cb) -> {
			return root.get("taskType").in(taskType);
		};
	}

	public static Specification<Job> scheduleTypeEq(ScheduleType st) {
		return (root, query, cb) -> {
			return cb.equal(root.get("scheduleType"), st);
		};
	}
	public static Specification<Job> scheduleTypeIn(List<ScheduleType> sts) {
		return (root, query, cb) -> {
			return root.get("scheduleType").in(sts);
		};
	}

	public static Specification<Job> assigneeEq(String assignee) {
		return (root, query, cb) -> {
			return cb.equal(root.get("assignee"), assignee);
		};
	}
}
