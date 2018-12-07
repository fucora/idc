package com.iwellmass.idc.app.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iwellmass.common.criteria.Between;
import com.iwellmass.common.criteria.Equal;
import com.iwellmass.common.criteria.Like;
import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.TaskType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobInstanceQuery implements SpecificationBuilder {
	
	@ApiModelProperty("任务ID")
	@Equal
	private String jobId;

	@ApiModelProperty("任务组")
	@Equal
	private String jobGroup;
	
	@ApiModelProperty("任务名")
	@Like
	private String jobName;

	@ApiModelProperty("执行方式")
	@Equal
	private DispatchType dispatchType;

	@ApiModelProperty("节点类型")
	private TaskType taskType;

	@ApiModelProperty("任务类型")
	@Equal
	private String contentType;

	@ApiModelProperty("业务日期始， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	@Between(value = "loadDate", to = "loadDateTo")
	private LocalDate loadDateFrom;

	@ApiModelProperty("业务日期止， yyyyMMdd")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+8")
	private LocalDate loadDateTo;

	@ApiModelProperty("运行时间始， yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	@Between(value = "startTime", to = "executeTimeTo")
	private LocalDateTime executeTimeFrom;

	@ApiModelProperty("运行时间止， yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "GMT+8")
	private LocalDateTime executeTimeTo;
}
