package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iwellmass.datafactory.common.vo.TaskDetailVO;
import com.iwellmass.idc.app.rpc.DFTaskService;
import com.iwellmass.idc.app.vo.task.*;
import com.iwellmass.idc.model.CronType;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.JobRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.TaskQueryParam;
import com.iwellmass.idc.app.vo.TaskRuntimeVO;
import com.iwellmass.idc.scheduler.repository.TaskRepository;

@Service
public class TaskService {

    @Resource
    TaskRepository taskRepository;
    @Resource
    JobRepository jobRepository;

    @Inject
    DFTaskService dfTaskService;

    public TaskVO getTask(String name) {

        Task task = taskRepository.findById(new TaskID(name)).orElseThrow(() -> new AppException("任务不存在"));
        TaskVO vo;
        if (task.getScheduleType() == ScheduleType.AUTO) {
            vo = new CronTaskVO();
            BeanUtils.copyProperties(task, vo, "workflow");
            ((CronTaskVO) vo).setCronType(CronType.valueOf(task.getProps().get("cronType").toString()));
            if (((CronTaskVO) vo).getCronType().equals(CronType.MONTHLY)) {
                ((CronTaskVO) vo).setDays((List<Integer>) task.getProps().get("days"));
            }
        } else {
            vo = new ManualTaskVO();
            BeanUtils.copyProperties(task, vo, "workflow");
        }
        if (task.getStartDateTime() != null) {
            vo.setStartDate(task.getStartDateTime().toLocalDate());
        }
        if (task.getEndDateTime() != null) {
            vo.setEndDate(task.getEndDateTime().toLocalDate());
        }
        return vo;
    }

    public PageData<TaskRuntimeVO> query(TaskQueryParam jqm) {
        return QueryUtils.doJpaQuery(jqm, (p) -> {
            Specification<Task> spec = SpecificationBuilder.toSpecification(jqm);
            return taskRepository.findAll(spec, PageRequest.of(p.getPageNumber(),p.getPageSize(),Sort.by(Sort.Direction.DESC,"createtime"))).map(t -> {
                TaskRuntimeVO vo = new TaskRuntimeVO();
                BeanUtils.copyProperties(t, vo);
                vo.setWorkflowName(t.getWorkflow().getWorkflowName());
                twiceValidateState(t,vo);
                return vo;
            });
        });
    }

    public List<Assignee> getAllAssignee() {
        return taskRepository.findAllAssignee().stream().map(Assignee::new).collect(Collectors.toList());
    }

    // twice validate task'state when complete ,the task is like to be running or error
    public void twiceValidateState(Task task, TaskRuntimeVO taskRuntimeVO) {
        if (task.getState().equals(TaskState.COMPLETE)) {
            List<Job> jobs = jobRepository.findAllByTaskName(task.getTaskName());
            if (jobs.size() == 0) {
                taskRuntimeVO.setState(TaskState.CANCEL);
                return;
            }
            for (Job job : jobs) {
                if (job.getState().equals(JobState.NONE) || job.getState().equals(JobState.RUNNING)) {
                    taskRuntimeVO.setState(TaskState.EXECUTING);
                    return;
                }
                if (job.getState().equals(JobState.FAILED)) {
                    taskRuntimeVO.setState(TaskState.ERROR);
                    return;
                }
            }
        }
    }

    public List<MergeTaskParamVO> getParams(String taskName) {
        List<NodeTask> nodeTasks = taskRepository.findById(new TaskID(taskName)).orElseThrow(() -> new AppException("未找到指定taskName:" + taskName + "任务")).getWorkflow().getNodeTasks();
        List<TaskDetailVO> taskDetailVOS = dfTaskService.batchQueryTaskInfo(nodeTasks.stream()
                .filter(nt -> !nt.getTaskId().equalsIgnoreCase("start") && !nt.getTaskId().equalsIgnoreCase("end"))
                .map(nt -> Long.valueOf(nt.getTaskId()))
                .collect(Collectors.toList()))
                .getResult();
        return buildMergeTaskParamVOS(taskDetailVOS.stream().map(TaskParamVO::new).collect(Collectors.toList()));
    }

    public List<MergeTaskParamVO> buildMergeTaskParamVOS(List<TaskParamVO> taskParamVOS) {
        Map<String, MergeTaskParamVO> mergeTaskParamVOS = Maps.newHashMap();
        taskParamVOS.forEach(t -> t.getParams().forEach(p -> {
            if (mergeTaskParamVOS.keySet().stream().noneMatch(m -> m.equals(p.getName()))) {
                mergeTaskParamVOS.put(p.getName(), new MergeTaskParamVO(t.getNodeTaskName(), p));
            } else {
                mergeTaskParamVOS.get(p.getName()).setMergedNodeTaskName(String.join(",", mergeTaskParamVOS.get(p.getName()).getMergedNodeTaskName(), t.getNodeTaskName()));
            }
        }));
        return mergeTaskParamVOS.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }


}