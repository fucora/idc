package com.iwellmass.idc.scheduler.model;

import java.util.List;
import java.util.Set;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * 工作流
 */
@Getter
@Setter
@Entity
@Table(name = "idc_workflow")
public class Workflow {

	/**
	 * id
	 */
	@Id
	@Column(name = "id")
	private String id;
	
	/**
	 * 名称
	 */
	@Column(name = "task_name")
	private String taskName;
	
	/**
	 * 描述
	 */
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
	
	/**
	 * 节点
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
	@JoinColumn(name = "pid",updatable = false)
	private List<NodeTask> taskNodes;
	
	/**
	 * 边关系
	 */
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,orphanRemoval = true)
	@JoinColumn(name = "pid",updatable = false)
	private List<WorkflowEdge> edges;

	public Set<String> successors(String node) {
		return null;
	}
}