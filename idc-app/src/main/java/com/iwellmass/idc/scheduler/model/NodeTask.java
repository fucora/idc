package com.iwellmass.idc.scheduler.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

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

	/**
	 * 工作流 ID
	 */
	@Id
	@Column(name = "pid")
	private String pid;
	
	/**
	 * 节点ID，本工作流内全局唯一
	 */
	@Id
	@Column(name = "id")
	private String id;
	
}
