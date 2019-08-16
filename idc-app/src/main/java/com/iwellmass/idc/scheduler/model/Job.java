package com.iwellmass.idc.scheduler.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.model.ScheduleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.scheduler.util.ExecParamConverter;

import lombok.Getter;
import lombok.Setter;

/**
 * 主实例
 */
@Getter
@Setter
@Entity
@Table(name = "idc_job")
public class Job extends AbstractJob {

	static final Logger LOGGER = LoggerFactory.getLogger(Job.class);
	
	/**
	 * 任务名（Task.taskName）
	 */
	@Column(name = "task_name")
	private  String taskName;
	
	/**
	 * 任务组（Task.taskGroup）
	 */
	@Column(name = "task_group")
	private String taskGroup;
	
	/**
	 * 责任人
	 */
	@Column(name = "assignee", length = 20)
	private String assignee;
	
	/**
	 * 实例类型（手动、自动、补数、测试）
	 */
	@Column(name = "job_type")
	@Enumerated(EnumType.STRING)
	private JobType jobType;
	
	/**
	 * 业务日期
	 */
	@Column(name = "load_date")
	private String loadDate;
	
	/**
	 * 运行时参数(保存已被解析的参数值,供nodeJob使用)
	 */
	@Column(name = "param", columnDefinition = "TEXT")
	@Convert(converter = ExecParamConverter.class)
	private List<ExecParam> param;

	/**
	 * 执行批次
	 */
	@Column(name = "should_fire_time")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime shouldFireTime;
	
	/**
	 * 主任务（Task）
	 */
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumns({
		@JoinColumn(name = "task_name", referencedColumnName = "task_name", insertable = false, updatable = false),
		@JoinColumn(name = "task_group", referencedColumnName = "task_group", insertable = false, updatable = false)
	})
	private Task task;
	
	public Job() {
	}

	public Job(String id, Task task) {
		super(id, task);
		// 实例类型
		this.taskName = task.getTaskName();
		this.taskGroup = task.getTaskGroup();
		this.assignee = task.getAssignee();
		this.jobType = task.getScheduleType() == ScheduleType.MANUAL ? JobType.MANUAL : JobType.AUTO;
		this.starttime = LocalDateTime.now();
		this.shouldFireTime = task.getPrevFireTime();
	}

	/**
	 * 刷新最新状态
	 */
	public void refresh() {
		if (this.getTaskType() == TaskType.WORKFLOW) {

		}
		// else ignore
	}

	public boolean isComplete() {
		return state.isComplete();
	}
}