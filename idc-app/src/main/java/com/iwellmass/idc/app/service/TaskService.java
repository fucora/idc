package com.iwellmass.idc.app.service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.TaskQueryParam;
import com.iwellmass.idc.app.vo.TaskRuntimeVO;
import com.iwellmass.idc.app.vo.task.CronTaskVO;
import com.iwellmass.idc.app.vo.task.ManualTaskVO;
import com.iwellmass.idc.app.vo.task.TaskVO;
import com.iwellmass.idc.scheduler.model.ScheduleType;
import com.iwellmass.idc.scheduler.model.Task;
import com.iwellmass.idc.scheduler.model.TaskID;
import com.iwellmass.idc.scheduler.repository.TaskRepository;

@Service
public class TaskService {

	@Resource
	TaskRepository taskRepository;

	public TaskVO getTask(String name) {

		Task task = taskRepository.findById(new TaskID(name)).orElseThrow(()-> new AppException("任务不存在"));
		TaskVO vo;
		if (task.getScheduleType() == ScheduleType.AUTO) {
			vo = new CronTaskVO();
			BeanUtils.copyProperties(task, vo, "workflow");
			vo.setContentType(task.getProps().get("cronType").toString());
            ((CronTaskVO) vo).setDays((List<Integer>) task.getProps().get("days"));
            ((CronTaskVO) vo).setDuetime((LocalTime) task.getProps().get("duetime"));
		} else {
			vo = new ManualTaskVO();
			BeanUtils.copyProperties(task, vo, "workflow");
		}
		if (task.getStarttime() != null) {
			vo.setStartDate(task.getStarttime().toLocalDate());
		}
		if (task.getEndtime() != null) {
			vo.setEndDate(task.getEndtime().toLocalDate());
		}
		return vo;
	}

	public PageData<TaskRuntimeVO> query(TaskQueryParam jqm) {
		return QueryUtils.doJpaQuery(jqm, (p) -> {
			Specification<Task> spec = SpecificationBuilder.toSpecification(jqm);
			return taskRepository.findAll(spec, p).map(t -> {
				TaskRuntimeVO vo = new TaskRuntimeVO();
				BeanUtils.copyProperties(t, vo);
				return vo;
			});
		});
	}

	public List<Assignee> getAllAssignee() {
		return taskRepository.findAllAssignee().stream().map(Assignee::new).collect(Collectors.toList());
	}
}