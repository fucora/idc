package com.iwellmass.idc.scheduler.model;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.scheduler.util.ExecParamConverter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "idc_job")
public class Job extends AbstractJob {

	static final Logger LOGGER = LoggerFactory.getLogger(Job.class);
	
	@Column(name = "name")
	private  String name;
	
	@Column(name = "group")
	private String group;
	
	@Column(name = "assignee", length = 20)
	private String assignee;
	
	@Column(name = "job_type")
	private JobType jobType;
	
	@Column(name = "load_date")
	private String loadDate;
	
	@Column(name = "param", columnDefinition = "TEXT")
	@Convert(converter = ExecParamConverter.class)
	private List<ExecParam> param;

	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	private JobState state;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "name", referencedColumnName = "name", insertable = false, updatable = false),
		@JoinColumn(name = "group", referencedColumnName = "group", insertable = false, updatable = false)
	})
	private Task task;
	
	public Job() {
	}

	public Job(String id, Task task) {
		Objects.requireNonNull(task);
		
		this.id = id;
		this.name = task.getName();

		// 实例类型
		if (task.getScheduleType() == ScheduleType.MANUAL) {
			this.jobType = JobType.AUTO;
		} else {
			this.jobType = JobType.MANUAL;
		}
		// 子任务
		if(task.getTaskType() == TaskType.SIMPLE) {
			this.taskType = TaskType.SIMPLE;
		} else {
			this.taskType = TaskType.WORKFLOW;
			subJobs = Objects.requireNonNull(task.getWorkflow())
				.getTaskNodes().stream()
				.map(node -> new NodeJob(id, node))
				.collect(Collectors.toList());
		}
	}
	
	public Job(String id, NodeTask nodeTask) {
		Objects.requireNonNull(nodeTask);
		// 设置 ID
		this.id = id;
		// 任务类型
		if(nodeTask.getTaskType() == TaskType.SIMPLE) {
			setTaskType(TaskType.SIMPLE);
		} else {
			setTaskType(TaskType.WORKFLOW);
			subJobs = Objects.requireNonNull(nodeTask.getWorkflow())
				.getTaskNodes().stream()
				.map(subNode -> new NodeJob(id, subNode))
				.collect(Collectors.toList());
		}
	}

	public void start() {

		this.state = JobState.NEW;
		System.out.println("拉拉啊啦啦");

		if (taskType == TaskType.WORKFLOW) {
			
			Task task = Objects.requireNonNull(getTask(), "未找到任务");
			
			if (task.getState().isRunning()) {
				Workflow workflow = Objects.requireNonNull(task.getWorkflow(), "未找到工作流");
				Set<NodeTask> successors = workflow.successors(NodeTask.START);
				@SuppressWarnings("unlikely-arg-type")
				Iterator<NodeJob> iterator = getSubJobs().stream().filter(sub -> successors.contains(sub.getNodeId()))
				.iterator();
				while (iterator.hasNext()) {
					NodeJob subTask = iterator.next();
					subTask.start();
				}
			} else {
				LOGGER.error("任务已 {}" + task.getState().desc());
			}
		}
	}

	public void renew() {
		if (state.isFailure()) {
			throw new JobException("Task already completed: " + this.state);
		}
	}

	public void finish() {
		this.state = JobState.FINISHED;
	}

	public void fail() {
		this.state = JobState.FAILED;
	}
}