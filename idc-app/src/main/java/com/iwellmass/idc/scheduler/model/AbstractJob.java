package com.iwellmass.idc.scheduler.model;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.*;

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
	LocalDateTime starttime;
	
	/**
	 * 最近更新时间
	 */
	@Column(name = "updatetime")
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

	public void start() {
		if (state.isComplete()) {
			throw new JobException("任务已执行");
		}
		if (getTask() == null) {
			throw new JobException("任务不存在");
		}
		
		if (taskType == TaskType.WORKFLOW) {
			AbstractTask task = Objects.requireNonNull(getTask(), "未找到任务");
			Workflow workflow = Objects.requireNonNull(task.getWorkflow(), "未找到工作流");
			// 找到立即节点
			Set<String> successors = workflow.successors(NodeTask.START);
			Iterator<NodeJob> iterator = getSubJobs().stream()
				.filter(sub -> successors.contains(sub.getNodeId()))
				.iterator();
			// any success
			boolean anySuccess = false;
			while (iterator.hasNext()) {
				NodeJob next = iterator.next();
				try {
					next.start();
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
		} else {
			
			AbstractTask task = Objects.requireNonNull(getTask(), "未找到任务");
			
			ExecuteRequest request = new ExecuteRequest();
			request.setDomain(task.getDomain());
			
			IDCJobExecutors.getExecutor().execute(request);
		}
	}
	
	public void renew() {
		checkRunning();
		this.setUpdatetime(LocalDateTime.now());
	}

	public void complete(JobState state) {
		checkRunning();
		if (state.isComplete()) {
			throw new IllegalArgumentException("非法的完成状态: " + state);
		}
		setState(state);
	}
	
	public void redo() {
		// TODO 编写重做逻辑
	}
	
	public void cancle() {
		// TODO 编写取消逻辑
	}
	
	private void checkRunning() {
		if (this.getState().isComplete()) {
			throw new JobException("任务已结束: " + this.state)  ;
		}
	}
	
	@Transient
	public abstract AbstractTask getTask();
}