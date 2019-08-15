package com.iwellmass.idc;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.model.CronType;
import com.iwellmass.idc.model.ScheduleType;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.List;

/**
 * @author nobita chen
 * @email nobita0522@qq.com
 * @date 2019/8/15 16:14
 * @description
 */
public class ExecuteRequest{

	// ====================   necessary param for running task: taskId,parameters,loadDate,

	/**
	 * 业务域方的实际任务id:nodeTask
	 */
	private String taskId;

	/**
	 * 任务运行所需全部参数,也需要包含loadDate
	 */
	private List<ExecParam> params;

	/**
	 * 业务参数
	 */
	private String loadDate;

	// ========================= use to build url for request

	/**
	 * 业务域
	 */
	private String domain;

	/**
	 * 业务类型
	 */
	private String contentType;

	// ==========================  redundant param for running task

	/**
	 * nodeJob 示例id:nodeJob
	 */
	private String nodeJobId;

	/**
	 * 任务的实例id:job
	 */
	private String jobId;

	/**
	 * 调度计划名称:task.taskName
	 */
	private String taskName;

	/**
	 * 该调度计划的执行方式:手动，自动
	 */
	@Enumerated(value = EnumType.STRING)
	private ScheduleType scheduleType;

	/**
	 * 本次执行的批次时间
	 */
	private Long shouldFireTime;

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public List<ExecParam> getParams() {
		return params;
	}

	public void setParams(List<ExecParam> params) {
		this.params = params;
	}

	public String getLoadDate() {
		return loadDate;
	}

	public void setLoadDate(String loadDate) {
		this.loadDate = loadDate;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getNodeJobId() {
		return nodeJobId;
	}

	public void setNodeJobId(String nodeJobId) {
		this.nodeJobId = nodeJobId;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public ScheduleType getScheduleType() {
		return scheduleType;
	}

	public void setScheduleType(ScheduleType scheduleType) {
		this.scheduleType = scheduleType;
	}

	public Long getShouldFireTime() {
		return shouldFireTime;
	}

	public void setShouldFireTime(Long shouldFireTime) {
		this.shouldFireTime = shouldFireTime;
	}

}
