package com.iwellmass.idc.model;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

@Data
@Entity
@Table(name = "t_idc_workflow")
public class Workflow {

    @Id
    @Column(name = "task_id")
    @ApiModelProperty("任务id")
    private String taskId;

    @Id
    @Column(name = "task_group")
    @ApiModelProperty("任务组")
    private String taskGroup;

    @Column(name = "workflow_id")
    @ApiModelProperty("所属工作流")
	private String workflowId;
	
	// 前端用，流程都画图
    @Column(name = "graph")
    @ApiModelProperty("画图的json字符串表达")
	private String graph;
}