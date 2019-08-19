package com.iwellmass.idc.app.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.message.TaskEventPlugin;
import com.iwellmass.idc.app.vo.*;
import com.iwellmass.idc.app.vo.graph.GraphVO;
import com.iwellmass.idc.message.RedoMessage;
import com.iwellmass.idc.message.SkipMessage;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.*;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;

/**
 * Job 服务
 */
@Service
public class JobService {

    static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

    @Resource
    TaskRepository taskRepository;

    @Resource
    JobRepository jobRepository;

    @Resource
    AllJobRepository allJobRepository;

    @Resource
    NodeJobRepository nodeJobRepository;

    @Resource
    WorkflowService workflowService;

    @Resource
    Scheduler qs;

    @Resource
    TaskService taskService;


    @Resource
    private ExecutionLogRepository logRepository;

    Job getJob(String id) {
        return jobRepository.findById(id).orElseThrow(() -> new AppException("任务 '" + id + "' 不存在"));
    }

    public Task getTask(String taskName) {
        return taskRepository.findById(new TaskID(taskName)).orElseThrow(() -> new AppException("调度 '" + taskName + "' 不存在"));
    }

    public PageData<JobRuntimeVO> query(JobQueryParam jqm) {
        Specification<Job> spec = SpecificationBuilder.toSpecification(jqm);
        return QueryUtils.doJpaQuery(jqm, pageable -> {
            return jobRepository.findAll(spec, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "starttime"))).map(job -> {
                JobRuntimeVO vo = new JobRuntimeVO();
                BeanUtils.copyProperties(job, vo);
                return vo;
            });
        });
    }

    public List<Assignee> getAllAssignee() {
        return jobRepository.findAllAssignee().stream().map(Assignee::new).sorted(Comparator.comparing(Assignee::getAssignee)).collect(Collectors.toList());
    }

    public JobVO get(String id) {
        JobVO jobVO = new JobVO();
        Job job = getJob(id);
        BeanUtils.copyProperties(job, jobVO);
        return jobVO;
    }

    public void clear(String id) {
        // TODO
    }

    @Transactional
    public Job createJob(String id, String taskName, List<ExecParam> execParams) {
        Task task = getTask(taskName);
        // 有可能前台强制取消了调度
        // 或者调度已过期、已被删除
//		if (task.getState().isTerminated()) {
//			LOGGER.error("调度已关闭：" + task.getState());
//		} else {
        Job job = new Job(id, task, execParams);
        return jobRepository.save(job);
//		}
    }

    public void redo(String jobId) {
        RedoMessage message = RedoMessage.newMessage(jobId);
        message.setMessage("重启任务:" + jobId);
        TaskEventPlugin.eventService(qs).send(message);
    }

    public void skip(String jobId) {
        SkipMessage message = SkipMessage.newMessage(jobId);
        message.setMessage("跳过任务 jobId:" + jobId);
        TaskEventPlugin.eventService(qs).send(message);
    }

    public JobVO getPlanInstanceDetail(String instanceId) {
        Job job = jobRepository.findById(instanceId).orElseThrow(() -> new AppException("未发现指定计划实例"));
        List<NodeJobVO> nodeJobVOS = job.getSubJobs().stream().map(item -> {
            NodeJobVO nodeJobVO = new NodeJobVO();
            BeanUtils.copyProperties(item, nodeJobVO);
            nodeJobVO.setTaskName(item.getNodeTask().getTaskName());
            nodeJobVO.setContentType(item.getNodeTask().getContentType());
            return nodeJobVO;
        }).collect(Collectors.toList());
        GraphVO graphVO = workflowService.getGraph(jobRepository.findById(instanceId).orElseThrow(() -> new AppException("未发现指定调度计划实例")).getTask().getWorkflowId());
        JobVO jobVO = new JobVO(nodeJobVOS, graphVO, taskService.getTask(job.getTaskName()));
        return jobVO;
    }

    @Transactional
    public void test(String id, String action) {
        AbstractJob job = allJobRepository.findById(id).get();
        Method method = ReflectionUtils.findMethod(job.getClass(), action);
        try {
            method.invoke(job);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ApplicationContextException(e.getMessage(), e);
        }
    }


    public PageData<ExecutionLog> getJobInstanceLog(String id, Pager pager) {
        Pageable page = PageRequest.of(pager.getPage(), pager.getLimit(), new Sort(Sort.Direction.ASC, "id"));
        Page<ExecutionLog> data = logRepository.findByInstanceId(id, page);
        return new PageData<>((int) data.getTotalElements(), data.getContent());
    }

}
