package com.iwellmass.idc.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * 子任务（可由 Job 触发，也可由 NodeJob 触发），需要将 {@link NodeJob#getId()} 设置到子任务的 {@link NodeJob#container} 字段
 */
@Getter
@Setter
@Entity
@Table(name = "idc_node_job")
public class NodeJob extends AbstractJob {

    /**
     * 任务ID（Task.taskId）
     */
    @Column(name = "workflow_id")
    private String workflowId;

    /**
     * 工作流结点ID（NodeTask.id）
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 主 Job ID（Job.id）
     */
    @Column(name = "main_id")
    private String mainId;

    /**
     * 父 Job ID（Job.id OR NodeJob.id）
     */
    @Column(name = "container")
    private String container;

    /**
     * 关联的子任务
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumns({@JoinColumn(name = "workflow_id", referencedColumnName = "workflow_id", insertable = false, updatable = false),
            @JoinColumn(name = "node_id", referencedColumnName = "id", insertable = false, updatable = false)})
    private NodeTask nodeTask;

    public NodeJob() {
    }

//	@Override
//	public void doStart(JobExecutionContext context) {
//		NodeTask task = (NodeTask)Objects.requireNonNull(getTask(), "未找到任务");
//		if(nodeId.equals(NodeTask.END))
//		{
//			setState(JobState.FINISHED);
//			FinishMessage message = FinishMessage.newMessage(getContainer());
//			message.setMessage("启动结束");
//			TaskEventPlugin.eventService(context.getScheduler()).send(message);
//			return;
//		}
//		LOGGER.info("start rpc:"+nodeId+","+nodeTask.taskId);
//		ExecuteRequest request = new ExecuteRequest();
//		request.setDomain(task.getDomain());
//		request.setContentType(task.getContentType());
//		JobEnvAdapter jobEnvAdapter = new JobEnvAdapter();
//		jobEnvAdapter.setTaskId(task.getTaskId());
//		jobEnvAdapter.setNodeJobId(id);
//		request.setJobEnvAdapter(jobEnvAdapter);
//		IDCJobExecutors.getExecutor().execute(request);
//	}

    public NodeJob(String container, NodeTask nodeTask) {
        super(id(container, nodeTask.getId()), nodeTask);
        // 设置 ID
        this.container = container;
        this.nodeId = nodeTask.getId();
        this.nodeTask = nodeTask;
        this.workflowId = nodeTask.getWorkflowId();
        this.state = JobState.NONE;
        this.taskType = TaskType.SIMPLE;
    }

    private static final String id(String container, String nodeId) {
        // TODO hash 对齐
        return container + "_" + nodeId + "_" + System.currentTimeMillis();
    }

    @Override
    public NodeTask getTask() {
        return getNodeTask();
    }

    // judge the nodeJob is systemNode
    public boolean isSystemNode() {
        return this.nodeId.equals(NodeTask.START) || this.nodeId.equals(NodeTask.END) || this.nodeId.equals(NodeTask.CONTROL);
    }
}
