package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_idc_workflow")
@Getter
@Setter
public class Workflow {

	@Id
    @Column(name = "workflow_id")
    @ApiModelProperty("工作流ID")
	private String workflowId;
	
	// 前端用，流程都画图
    @Column(name = "graph")
    @ApiModelProperty("工作流图")
	private String graph;
	
    @Column(name = "task_id")
    @ApiModelProperty("任务id")
    private String taskId;

    @Column(name = "task_group")
    @ApiModelProperty("任务组")
    private String taskGroup;


}