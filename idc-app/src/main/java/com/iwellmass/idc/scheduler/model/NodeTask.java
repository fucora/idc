package com.iwellmass.idc.scheduler.model;

import javax.persistence.*;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.scheduler.util.ExecParamConverter;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 工作流节点，包装一个可执行的任务
 */
@Getter
@Setter
@Entity
@IdClass(WfID.class)
@Table(name = "idc_node_task")
public class NodeTask extends AbstractTask {
	
	public static final String START = "START";
	public static final String END = "END";
	public static final String CONTROL = "CONTROL";  // todo used for concurrent control

	/**
	 * 节点ID，本工作流内全局唯一
	 */
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "task_id")
	private String taskId;

	/**
	 * 业务类型
	 */
	@Column(name = "content_type")
	private String contentType;

}
