package com.iwellmass.idc.app.vo;

import java.time.LocalDateTime;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobRuntimeListVO {

	// ~~ 调度属性 ~~
	@ApiModelProperty("调度ID")
	private String jobId;

	@ApiModelProperty("调度组")
	private String jobGroup;
	
	@ApiModelProperty("任务名称")
	private String jobName;
	
	@ApiModelProperty("责任人")
	private String assignee;
	
	@ApiModelProperty("当前批次")
	private LocalDateTime shouldFireTime;
	
	@ApiModelProperty("下一批次")
	private LocalDateTime nextFireTime;
	
	@ApiModelProperty("实例ID")
	private Integer instanceId;
	
	@ApiModelProperty("调度状态")
	private Integer scheduleStatus;
	
}
