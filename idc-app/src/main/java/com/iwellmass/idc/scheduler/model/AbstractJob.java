package com.iwellmass.idc.scheduler.model;

import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@ToString(of = {"id", "state"})
public abstract class AbstractJob {

	static final Logger LOGGER = LoggerFactory.getLogger(AbstractJob.class);
	
	@Id
	String id;
	
	@Column(name = "task_type")
	@Enumerated(EnumType.STRING)
	TaskType taskType;
	
	@Column(name = "starttime")
	LocalDateTime starttime;
	
	@Column(name = "updatetime")
	LocalDateTime updatetime;

	@Column(name = "state")
	@Enumerated(EnumType.STRING)
	JobState state;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "container")
	List<NodeJob> subJobs;
	
	public AbstractJob() {
	}

	public AbstractJob(String id, AbstractTask task) {
		Objects.requireNonNull(task);
		this.id = id; // 作业 ID
		this.taskType = task.getTaskType(); // 任务类型
		if(taskType == TaskType.WORKFLOW) { // 创建子任务
			subJobs = Objects.requireNonNull(task.getWorkflow())
				.getTaskNodes().stream()
				.map(node -> new NodeJob(id, node))
				.collect(Collectors.toList());
		}
	}

	public void start() {
		this.state = JobState.NEW;
		if (taskType == TaskType.WORKFLOW) {
			AbstractTask task = Objects.requireNonNull(getTask(), "未找到任务");
			Workflow workflow = Objects.requireNonNull(task.getWorkflow(), "未找到工作流");
			Set<NodeTask> successors = workflow.successors(NodeTask.START);
			@SuppressWarnings("unlikely-arg-type")
			Iterator<NodeJob> iterator = getSubJobs().stream()
				.filter(sub -> successors.contains(sub.getNodeId()))
				.iterator();
			while (iterator.hasNext()) {
				NodeJob subJob = iterator.next();
				subJob.start();
			}
		} else {
			// 业务 CODE
			this.start0();
		}
	}
	
	abstract void start0();

	public abstract void renew();

	public abstract void finish();
	
	public abstract void fail();
	
	@Transient
	public abstract AbstractTask getTask();
}