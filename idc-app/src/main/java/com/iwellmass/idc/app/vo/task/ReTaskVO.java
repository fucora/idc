package com.iwellmass.idc.app.vo.task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwellmass.idc.scheduler.model.Task;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.scheduler.model.TaskType;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = Id.NAME, property = "scheduleType", include = As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
	@Type (name = "MANUAL", value = ReManualTaskVO.class),
	@Type (name = "AUTO", value = ReCronTaskVO.class)
})
public abstract class ReTaskVO {

	@ApiModelProperty("计划名称")
	String taskName;

	@ApiModelProperty("描述")
	String description;

	@ApiModelProperty("负责人")
	String assignee;

	@ApiModelProperty("失败重试")
	Boolean isRetry = true;
	
	@ApiModelProperty("运行参数")
	List<ExecParam> params;
	
	@ApiModelProperty("调度方式")
	ScheduleType scheduleType;
	
	@ApiModelProperty("生效日期")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	LocalDate startDate;

	@ApiModelProperty("失效日期")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	LocalDate endDate;
	
	@ApiModelProperty("出错时阻塞")
	Boolean blockOnError = true;

	@ApiModelProperty("业务域")
	String domain;

	@ApiModelProperty("任务类型")
	TaskType taskType;

	@ApiModelProperty("业务类型")
	String contentType;
	
	public abstract Trigger buildTrigger(TriggerKey key);

	@JsonIgnore
	public abstract Map<String, Object> getProps();

	public Task buildNewTask(Task oldTask) {
		Task newTask = new Task();
		newTask.setTaskName(oldTask.getTaskName());
		newTask.setTaskGroup(oldTask.getTaskGroup());
		newTask.setWorkflowId(oldTask.getWorkflowId());
		newTask.setCreatetime(LocalDateTime.now());
		newTask.setUpdatetime(newTask.getCreatetime());
		return newTask;
	}
}