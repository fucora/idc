package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;

import com.iwellmass.common.param.ExecParam;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.vo.*;
import com.iwellmass.idc.app.vo.graph.GraphVO;
import com.iwellmass.idc.app.vo.task.MergeTaskParamVO;
import com.iwellmass.idc.app.vo.task.TaskVO;
import com.iwellmass.idc.scheduler.model.*;
import com.iwellmass.idc.scheduler.repository.*;
import com.iwellmass.idc.scheduler.service.IDCLogger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Job 服务
 */
@Service
public class JobService {

    @Resource
    TaskRepository taskRepository;
    @Resource
    JobRepository jobRepository;
    @Resource
    AllJobRepository allJobRepository;
    @Resource
    WorkflowService workflowService;
    @Resource
    TaskService taskService;
    @Inject
    IDCLogger logger;


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
                vo.setScheduleType(job.getTask().getScheduleType());
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
    public Job createJob(String id, String taskName, List<ExecParam> execParams, LocalDateTime shouldFireTime) {
        Task task = getTask(taskName);
        Job job = new Job(id, task, execParams,shouldFireTime == null ? task.getPrevFireTime() : shouldFireTime);
        return jobRepository.save(job);
    }

    public JobVO getJobDetail(String jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new AppException("未发现指定计划实例"));
        List<NodeJobVO> nodeJobVOS = job.getSubJobs().stream().map(item -> {
            NodeJobVO nodeJobVO = new NodeJobVO();
            BeanUtils.copyProperties(item, nodeJobVO);
            nodeJobVO.setTaskName(item.getNodeTask().getTaskName());
            nodeJobVO.setContentType(item.getNodeTask().getContentType());
            return nodeJobVO;
        }).collect(Collectors.toList());
        GraphVO graphVO = workflowService.getGraph(jobRepository.findById(jobId).orElseThrow(() -> new AppException("未发现指定调度计划实例")).getTask().getWorkflowId());
        TaskVO taskVO = taskService.getTask(job.getTaskName());
        List<MergeTaskParamVO> mergeTaskParamVOS = taskVO.getMergeTaskParamVOS();
        for (MergeTaskParamVO m : mergeTaskParamVOS) {
            for (ExecParam p : job.getParams()) {
                if (m.getExecParam().getName().equals(p.getName())) {
                    m.getExecParam().setValue(p.getValue());
                    break;
                }
            }
        }
        JobVO jobVO = new JobVO(nodeJobVOS, graphVO, taskVO, mergeTaskParamVOS);
        return jobVO;
    }

    @Transactional
    public String test(String jobId, String template,String content) {
//        AbstractJob job = allJobRepository.findById(id).get();
//        Method method = ReflectionUtils.findMethod(job.getClass(), action);
//        try {
//            method.invoke(job);
//        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//            throw new ApplicationContextException(e.getMessage(), e);
//        }
//        logger.log(jobId,template,content)
        logger.log(jobId,template,content);
        return "success";

    }


    public PageData<ExecutionLog> getLogs(String jobId, Pager pager) {
        Pageable page = PageRequest.of(pager.getPage() , pager.getLimit(), new Sort(Sort.Direction.ASC, "id"));
        Page<ExecutionLog> data = logRepository.findByJobId(jobId, page);
        return new PageData<>((int) data.getTotalElements(), data.getContent());
    }

}
