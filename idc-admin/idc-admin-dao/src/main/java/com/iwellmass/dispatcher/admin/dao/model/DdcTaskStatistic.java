package com.iwellmass.dispatcher.admin.dao.model;

/**
 * Created by xkwu on 2016/5/24.
 */
public class DdcTaskStatistic {
	
	private Integer taskId;
    private DdcTask task;

    private Long totalCounts;
    private Long successdCounts;
    private Long failedCounts;
    private Float successRate;
    public Integer getTaskId() {
		return taskId;
	}

	public void setTaskId(Integer taskId) {
		this.taskId = taskId;
	}

	public DdcTask getTask() {
        return task;
    }

    public void setTask(DdcTask task) {
        this.task = task;
    }

    public Long getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Long totalCounts) {
        this.totalCounts = totalCounts;
    }

    public Long getSuccessdCounts() {
        return successdCounts;
    }

    public void setSuccessdCounts(Long successdCounts) {
        this.successdCounts = successdCounts;
    }

    public Long getFailedCounts() {
        return failedCounts;
    }

    public void setFailedCounts(Long failedCounts) {
        this.failedCounts = failedCounts;
    }

    public Float getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Float successRate) {
        this.successRate = successRate;
    }
}
