package com.iwellmass.idc.scheduler.model;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.idc.app.scheduler.JobEnvAdapter;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.idc.app.scheduler.ExecuteRequest;
import com.iwellmass.idc.scheduler.IDCJobExecutors;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 抽象实例，描述一个实例（由 Task 生成）最基本的要素
 */
@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@ToString(of = {"id", "state"})
public abstract class AbstractJob {

	static final Logger LOGGER = LoggerFactory.getLogger(AbstractJob.class);
	
	/**
	 * 全局唯一 ID
	 */
	@Id
	String id;
	
	/**
	 * 任务类型（Task）
	 */
	@Column(name = "task_type")
	@Enumerated(EnumType.STRING)
	TaskType taskType;
	
	/**
	 * 开始时间
	 */
	@Column(name = "starttime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	LocalDateTime starttime;
	
	/**
	 * 最近更新时间
	 */
	@Column(name = "updatetime")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	LocalDateTime updatetime;

	/**
	 * 实例状态（Job）
	 */
	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	JobState state;
	
	/**
	 * 子实例（SubJob）
	 */
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinColumn(name = "container")
	List<NodeJob> subJobs;
	
	public AbstractJob() {
	}

	public AbstractJob(String id, AbstractTask task) {
		Objects.requireNonNull(task);
		this.id = id; // 作业 ID
		this.taskType = task.getTaskType(); // 任务类型
		this.state = JobState.NONE;
		if(taskType == TaskType.WORKFLOW) { // 创建子任务
			subJobs = Objects.requireNonNull(task.getWorkflow())
				.getTaskNodes().stream()
				.map(node -> new
						NodeJob(id, node))
				.collect(Collectors.toList());
		}
	}

//	public void start(JobExecutionContext context) {
//		if (state.isComplete()) {
//			throw new JobException("任务已执行");
//		}
//		if (getTask() == null) {
//			throw new JobException("任务不存在");
//		}
//		doStart(context);
//	}
//
//	abstract public  void doStart(JobExecutionContext context);
//
//
//
//	public void renew() {
//		checkRunning();
//		this.setUpdatetime(LocalDateTime.now());
//	}
//
//	public void success() {
//		checkRunning();
//		setState(JobState.FINISHED);
//	}
//
//	public void failed() {
//		checkRunning();
//		setState(JobState.FAILED);
//	}
//
//
//	public void redo() {
//		// TODO 编写重做逻辑
//	}
//
//	public void cancle() {
//		// TODO 编写取消逻辑
//	}
//
//	private void checkRunning() {
//		if (this.getState().isComplete()) {
//			throw new JobException("任务已结束: " + this.state)  ;
//		}
//	}
	
	@Transient
	public abstract AbstractTask getTask();
}