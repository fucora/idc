package com.iwellmass.idc.app.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iwellmass.common.param.ExecParam;
import com.iwellmass.datafactory.common.vo.TaskDetailVO;
import com.iwellmass.idc.app.rpc.DFTaskService;
import com.iwellmass.idc.app.vo.task.*;
import com.iwellmass.idc.model.CronType;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.JobRepository;
import com.iwellmass.idc.scheduler.repository.WorkflowRepository;
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

    // last day of last month compared to shouldFireTime
    public static final String LAST_DAY_OF_LAST_MONTH_COMPARED_SHOULDFIRETIME = "调度批次上月的最后一天";
    public static final String LAST_DAY_OF_LAST_MONTH_OGNL_COMPARED_SHOULDFIRETIME = "#idc.shouldFireTime.plusMonths(-1).with(@TemporalAdjusters@lastDayOfMonth()).format('yyyyMMdd')";
    public static final String LAST_DAY_OF_THIS_MONTH_COMPARED_SHOULDFIRETIME = "调度批次当月的最后一天";
    public static final String LAST_DAY_OF_THIS_MONTH_OGNL_COMPARED_SHOULDFIRETIME = "#idc.shouldFireTime.with(@TemporalAdjusters@lastDayOfMonth()).format('yyyyMMdd')";
    public static final String LAST_DAY_OF_NEXT_MONTH_COMPARED_SHOULDFIRETIME = "调度批次下月的最后一天";
    public static final String LAST_DAY_OF_NEXT_MONTH_OGNL_COMPARED_SHOULDFIRETIME = "#idc.shouldFireTime.plusMonths(1).with(@TemporalAdjusters@lastDayOfMonth()).format('yyyyMMdd')";
    // last day of last month compared to realRunTime
    public static final String LAST_DAY_OF_LAST_MONTH_COMPARED_REALRUNTIME = "实际运行时间上月的最后一天";
    public static final String LAST_DAY_OF_LAST_MONTH_OGNL_COMPARED_REALRUNTIME = "#idc.realRunTime.plusMonths(-1).with(@TemporalAdjusters@lastDayOfMonth()).format('yyyyMMdd')";
    public static final String LAST_DAY_OF_THIS_MONTH_COMPARED_REALRUNTIME = "实际运行时间当月的最后一天";
    public static final String LAST_DAY_OF_THIS_MONTH_OGNL_COMPARED_REALRUNTIME = "#idc.realRunTime.plusMonths(-1).with(@TemporalAdjusters@lastDayOfMonth()).format('yyyyMMdd')";
    public static final String LAST_DAY_OF_NEXT_MONTH_COMPARED_REALRUNTIME = "实际运行时间下月的最后一天";
    public static final String LAST_DAY_OF_NEXT_MONTH_OGNL_COMPARED_REALRUNTIME = "#idc.realRunTime.plusMonths(-1).with(@TemporalAdjusters@lastDayOfMonth()).format('yyyyMMdd')";
    // now
    public static final String NOW = "调度计划提交时间";
    public static final String NOW_OGNL = "#idc.taskUpdateTime.format('yyyyMMdd')";

    public static final Map<String,String> loadDateParams = new LinkedHashMap<>();

    static {
        loadDateParams.put(LAST_DAY_OF_LAST_MONTH_COMPARED_SHOULDFIRETIME,LAST_DAY_OF_LAST_MONTH_OGNL_COMPARED_SHOULDFIRETIME);
        loadDateParams.put(LAST_DAY_OF_THIS_MONTH_COMPARED_SHOULDFIRETIME,LAST_DAY_OF_THIS_MONTH_OGNL_COMPARED_SHOULDFIRETIME);
        loadDateParams.put(LAST_DAY_OF_NEXT_MONTH_COMPARED_SHOULDFIRETIME,LAST_DAY_OF_NEXT_MONTH_OGNL_COMPARED_SHOULDFIRETIME);
        loadDateParams.put(LAST_DAY_OF_LAST_MONTH_COMPARED_REALRUNTIME,LAST_DAY_OF_LAST_MONTH_OGNL_COMPARED_REALRUNTIME);
        loadDateParams.put(LAST_DAY_OF_NEXT_MONTH_COMPARED_REALRUNTIME,LAST_DAY_OF_THIS_MONTH_OGNL_COMPARED_REALRUNTIME);
        loadDateParams.put(LAST_DAY_OF_THIS_MONTH_COMPARED_REALRUNTIME,LAST_DAY_OF_NEXT_MONTH_OGNL_COMPARED_REALRUNTIME);
        loadDateParams.put(NOW,NOW_OGNL);
    }

    @Resource
    TaskRepository taskRepository;
    @Resource
    JobRepository jobRepository;
    @Resource
    WorkflowRepository workflowRepository;

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
                ((CronTaskVO) vo).setExpression(String.valueOf(task.getProps().getOrDefault("expression","未配置")));
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
        List<MergeTaskParamVO> mergeTaskParamVOS = getParams(task.getWorkflowId());
        for (MergeTaskParamVO m : mergeTaskParamVOS) {
            for (ExecParam p : task.getParams()) {
                if (m.getExecParam().getName().equals(p.getName())) {
                    m.getExecParam().setDefaultExpr(p.getDefaultExpr());
                    break;
                }
            }
        }
        vo.setMergeTaskParamVOS(mergeTaskParamVOS);
        return vo;
    }

    public PageData<TaskRuntimeVO> query(TaskQueryParam jqm) {
        return QueryUtils.doJpaQuery(jqm, (p) -> {
            Specification<Task> spec = SpecificationBuilder.toSpecification(jqm);
            return taskRepository.findAll(spec, PageRequest.of(p.getPageNumber(), p.getPageSize(), Sort.by(Sort.Direction.DESC, "createtime"))).map(t -> {
                TaskRuntimeVO vo = new TaskRuntimeVO();
                BeanUtils.copyProperties(t, vo);
                vo.setWorkflowName(t.getWorkflow().getWorkflowName());
                twiceValidateState(t, vo);
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
                taskRuntimeVO.setState(TaskState.NONE);
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

    public List<MergeTaskParamVO> getParams(String workflowId) {
        List<NodeTask> nodeTasks = workflowRepository.findById(workflowId).orElseThrow(() -> new AppException("未发现指定工作流:" + workflowId)).getNodeTasks();
        List<TaskDetailVO> taskDetailVOS = dfTaskService.batchQueryTaskInfo(nodeTasks.stream()
                .filter(nt -> !nt.getTaskId().equalsIgnoreCase("start") &&
                                !nt.getTaskId().equalsIgnoreCase("end") &&
                                !nt.getTaskId().equalsIgnoreCase("control")
                                 )
                .map(nt -> Long.valueOf(nt.getTaskId()))
                .collect(Collectors.toList()))
                .getResult();
        return taskDetailVOS == null ? Lists.newArrayList() : buildMergeTaskParamVOS(taskDetailVOS.stream().map(TaskParamVO::new).collect(Collectors.toList()));
    }

    public List<MergeTaskParamVO> buildMergeTaskParamVOS(List<TaskParamVO> taskParamVOS) {
        Map<String, MergeTaskParamVO> mergeTaskParamVOS = Maps.newHashMap();
        taskParamVOS.forEach(t -> t.getParams().forEach(p -> {
            if (mergeTaskParamVOS.keySet().stream().noneMatch(m -> m.equals(p.getName()))) {
                mergeTaskParamVOS.put(p.getName(), new MergeTaskParamVO(t.getNodeTaskName(), p));
            } else {
                mergeTaskParamVOS.get(p.getName()).setMergedNodeTaskName(String.join("、", mergeTaskParamVOS.get(p.getName()).getMergedNodeTaskName(), t.getNodeTaskName()));
            }
        }));
        return mergeTaskParamVOS.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public List<String> getLoadDateParams() {
        return Lists.newArrayList(loadDateParams.keySet());
    }

}