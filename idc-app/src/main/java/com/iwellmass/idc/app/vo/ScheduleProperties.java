package com.iwellmass.idc.app.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.Utils;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.TaskKey;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * 调度配置
 */
@Getter
@Setter
public class ScheduleProperties extends CronVO {
	
	@ApiModelProperty("业务ID")
	private String taskId;
	
	@ApiModelProperty("业务域")
	private String taskGroup;

	@ApiModelProperty("任务名称")
	private String jobName;
	
	@ApiModelProperty("描述")
	private String description;
	
	@ApiModelProperty("负责人")
	private String assignee;
	
	@ApiModelProperty("周期类型")
	private ScheduleType scheduleType;
	
	@ApiModelProperty("失败重试")
	private Boolean isRetry = true;
	
	@ApiModelProperty("出错时阻塞")
	private Boolean blockOnError = true;
	
	@ApiModelProperty("生效日期 yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private LocalDate startTime;

	@ApiModelProperty("失效日期, yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	private LocalDate endTime;
	
	@ApiModelProperty("执行方式")
	private DispatchType dispatchType;
	
	@ApiModelProperty("运行参数")
	private List<ExecParam> parameter;

	@ApiModelProperty("job依赖关系")
    private List<JobDependency> jobDependencies;
	
	
	public ScheduleProperties() {
	}
	
	public ScheduleProperties(Job job) {
		this.setAssignee(job.getAssignee());
		this.setBlockOnError(job.getBlockOnError());
		this.setDispatchType(job.getDispatchType());
		this.setIsRetry(job.getIsRetry());
		this.setJobName(job.getJobName());
		this.setParameter(job.getParameter());
		this.setScheduleType(job.getScheduleType());
		this.setTaskGroup(job.getTaskGroup());
		this.setTaskId(job.getTaskId());
		// 转换
		Optional.ofNullable(job.getStartTime()).map(LocalDateTime::toLocalDate).ifPresent(this::setStartTime);
		Optional.ofNullable(job.getEndTime()).map(LocalDateTime::toLocalDate).ifPresent(this::setEndTime);
		CronVO vo = JSON.parseObject(job.getScheduleConfig(), CronVO.class);
		
		// CRON-EX
		this.setDays(vo.getDays());
		this.setDuetime(vo.getDuetime());
	}


	public String toCronExpression() {
		List<Integer> days = getDays();
		LocalTime duetime = getDuetime();
		
		switch (scheduleType) {
		case MONTHLY: {
			Assert.isFalse(Utils.isNullOrEmpty(days), "月调度配置不能为空");
			boolean isLast = days.stream().filter(i -> i < 0).count() == 1;
			if(isLast && days.size() > 1) {
				throw new AppException("最后 N 天不能使用组合配置模式");
			}
			return String.format("%s %s %s %s * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour(),
				isLast ? days.get(0) == -1 ? "L" : "L" + (days.get(0) + 1)
					: String.join(",", days.stream().map(String::valueOf).collect(Collectors.toList())));
		}
		case WEEKLY: {
			throw new UnsupportedOperationException("not supported yet");
		}
		case DAILY:
			return String.format("%s %s %s * * ? *", duetime.getSecond(), duetime.getMinute(), duetime.getHour());
		default:
			throw new AppException("接收的调度类型" + Arrays.asList(ScheduleType.values()));
		}
	}
	
	@JsonIgnore
	public TaskKey getTaskKey() {
		return new TaskKey(taskId, taskGroup);
	}
	
	public Job toJob() {
		Job job = new Job();
		job.setAssignee(this.getAssignee());
		job.setBlockOnError(this.getBlockOnError());
		job.setCronExpr(this.toCronExpression());
		job.setDispatchType(this.getDispatchType());
		job.setIsRetry(this.getIsRetry());
		job.setJobName(this.getJobName());
		job.setParameter(this.getParameter());
		job.setScheduleType(this.getScheduleType());
		job.setTaskGroup(this.getTaskGroup());
		job.setTaskId(this.getTaskId());
		// 转换
		job.setStartTime(Optional.ofNullable(this.getStartTime()).map(t -> t.atTime(LocalTime.MIN)).orElse(null));
		job.setEndTime(Optional.ofNullable(this.getEndTime()).map(t -> t.atTime(LocalTime.MAX)).orElse(null));
		
		CronVO vo = new CronVO();
		vo.setDays(this.getDays());
		vo.setDuetime(this.getDuetime());
		String cvo = JSON.toJSONString(vo);
		job.setScheduleConfig(cvo);
		return job;
	}
}
