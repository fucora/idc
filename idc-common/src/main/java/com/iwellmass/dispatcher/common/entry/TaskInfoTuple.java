package com.iwellmass.dispatcher.common.entry;

public class TaskInfoTuple {

	private Long workflowExecuteId;

	private Integer taskId;

	private String executeBatchId;

	private Integer workflowId;

	public TaskInfoTuple(Long workflowExecuteId, Integer taskId, String executeBatchId, Integer workflowId) {
		this.workflowExecuteId = workflowExecuteId;
		this.taskId = taskId;
		this.executeBatchId = executeBatchId;
		this.workflowId = workflowId;
	}

	public Long getWorkflowExecuteId() {
		return workflowExecuteId;
	}

	public Integer getTaskId() {
		return taskId;
	}

	public String getExecuteBatchId() {
		return executeBatchId;
	}

	public Integer getWorkflowId() {
		return workflowId;
	}

}
