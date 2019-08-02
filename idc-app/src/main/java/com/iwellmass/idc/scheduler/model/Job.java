package com.iwellmass.idc.scheduler.model;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

import org.quartz.JobExecutionContext;
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
	 * 运行时参数
	 */
	@Column(name = "param", columnDefinition = "TEXT")
	@Convert(converter = ExecParamConverter.class)
	private List<ExecParam> param;
	
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
		this.updatetime = LocalDateTime.now();
	}

	/**
	 * 刷新最新状态
	 */
	public void refresh() {
		if (this.getTaskType() == TaskType.WORKFLOW) {
			
		}
		// else ignore
	}

	@Override
	public void doStart(JobExecutionContext context) {
		//TODO 如果是workflow需要在此代码块中修改job的状态
		 runNextJob(NodeTask.START,context);

	}

	public boolean isComplete() {
		return state.isComplete();
	}

	public synchronized void runNextJob(String startNode, JobExecutionContext context) {
		AbstractTask task = Objects.requireNonNull(getTask(), "未找到任务");
		Workflow workflow = Objects.requireNonNull(task.getWorkflow(), "未找到工作流");
		// 找到立即节点
		Set<String> successors = workflow.successors(startNode);
		Iterator<NodeJob> iterator = getSubJobs().stream()
				.filter(sub -> successors.contains(sub.getNodeId()))
				.iterator();


		// any success
		boolean anySuccess = false;
		while (iterator.hasNext()) {
			NodeJob next = iterator.next();
			try {
				Set<String> previous = 	workflow.getPrevious(next.getNodeId());

				//如果存在未完成的任务 则不继续执行
				boolean unfinishJob  =  getSubJobs().stream()
						.filter(sub -> previous.contains(sub.getNodeId()))
						.anyMatch(sub->!sub.getState().isSuccess());
				if(unfinishJob){
//					anySuccess = true;
					continue;
				}
				next.start(context);
				anySuccess = true;
			} catch (Exception e) {
				e.printStackTrace();
				anySuccess |= false;
				next.setState(JobState.FAILED);
			}
		}
		// 贪婪模式
		if (!anySuccess) {
			setState(JobState.FAILED);
		}
	}
}