package com.iwellmass.idc.scheduler.model;

import java.util.HashSet;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
     * 修改日期
     */
    @Column(name = "updatetime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updatetime;

	/**
	 * 节点
	 */

	@Fetch(FetchMode.SELECT)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
	@JoinColumn(name = "pid",updatable = false)
	private List<NodeTask> taskNodes;

	/**
	 * 边关系
	 */

	@Fetch(FetchMode.SELECT)
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER,orphanRemoval = true)
	@JoinColumn(name = "pid",updatable = false)
	private List<WorkflowEdge> edges;

	public Set<String> successors(String node) {
		Set<String> result = new HashSet<>();
		for(WorkflowEdge workflowEdge:edges)
		{
			if(workflowEdge.getSource().equals(node))
			{
				result.add(workflowEdge.getTarget());
			}
		}
		return result;
	}

	public Set<String> getPrevious(String node) {
		Set<String> result = new HashSet<>();
		for(WorkflowEdge workflowEdge:edges) {
			if(workflowEdge.getTarget().equals(node))
			{
				if(!workflowEdge.getSource().equals(NodeTask.START)){
					result.add(workflowEdge.getSource());
				}
			}
		}
		return result;
	}

    public Workflow() {
        updatetime = LocalDateTime.now();
    }
}