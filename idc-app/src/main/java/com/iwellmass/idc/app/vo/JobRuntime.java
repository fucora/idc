package com.iwellmass.idc.app.vo;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRuntime extends Job {

	// ~~ 调度属性 ~~
	private String jobId;

	private String jobGroup;
	
	@ApiModelProperty("计划名称")
	private String jobName;
	
	@ApiModelProperty("责任人")
	private String assignee;
	
	@ApiModelProperty("周期类型")
	@Enumerated(EnumType.STRING)
	private ScheduleType scheduleType;
	
	@ApiModelProperty("出错重试")
	private Boolean isRetry;
	
	@ApiModelProperty("阻塞下游任务")
	private Boolean blockOnError;
	
	@ApiModelProperty("生效日期")
	@JsonFormat(pattern="yyyyMMdd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime startTime;
	
	@ApiModelProperty("失效日期")
	@JsonFormat(pattern="yyyyMMdd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime endTime;
	
	@ApiModelProperty("参数")
	private List<ExecParam> parameter;
	
	@ApiModelProperty("创建时间")
	private LocalDateTime createTime;

	@ApiModelProperty("更新时间")
	private LocalDateTime updateTime;
	
	@ApiModelProperty("cron 表达式")
	@Column(name = "cron_expr")
	private String cronExpr;
	
	// ~~ Task 相关 ~~
	@ApiModelProperty("业务ID")
	private String taskId;

	@ApiModelProperty("业务域")
	private String taskGroup;
	
	@ApiModelProperty("任务类型")
	@Enumerated(EnumType.STRING)
	private TaskType taskType;

	@ApiModelProperty("业务类型")
	private String contentType;
	
	@ApiModelProperty("执行方式")
	@Enumerated(EnumType.STRING)
	private DispatchType dispatchType;
	
	@ApiModelProperty("workflow_id")
	private String workflowId;
	
	@ApiModelProperty("依赖等待")
	private List<JobBarrierVO> barriers;
}
