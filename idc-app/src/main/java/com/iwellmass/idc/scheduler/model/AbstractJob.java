package com.iwellmass.idc.scheduler.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractJob {

	static final Logger LOGGER = LoggerFactory.getLogger(AbstractJob.class);
	
	@Id
	String id;
	
	@Column(name = "task_type")
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
		this.id = id;
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

	public abstract void start();

	public abstract void renew();

	public abstract void finish();
	
	public abstract void fail();
}