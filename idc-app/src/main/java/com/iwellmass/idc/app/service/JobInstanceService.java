package com.iwellmass.idc.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.iwellmass.idc.model.*;
import org.quartz.SchedulerException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.Assert;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.repo.ExecutionLogRepository;
import com.iwellmass.idc.app.repo.JobInstanceRepository;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.CancleRequest;
import com.iwellmass.idc.app.vo.JobInstanceQuery;
import com.iwellmass.idc.app.vo.RedoRequest;
import com.iwellmass.idc.executor.CompleteEvent;
import com.iwellmass.idc.quartz.IDCPlugin;

@Service
public class JobInstanceService {

    @Inject
    private JobInstanceRepository jobInstanceRepository;

    @Inject
    private ExecutionLogRepository logRepository;

    @Inject
    private IDCPlugin idcPlugin;

    public void redo(RedoRequest request) {

        int instanceId = request.getInstanceId();

        JobInstance instance = jobInstanceRepository.findOne(instanceId);

        Assert.isTrue(instance != null, "找不到此实例");

        try {
            idcPlugin.redo(request.getInstanceId());
        } catch (SchedulerException e) {
            throw new AppException("执行失败: " + e.getMessage());
        }
    }


    public void forceComplete(int instanceId) {

        JobInstance instance = jobInstanceRepository.findOne(instanceId);

        Assert.isTrue(instance != null, "找不到此实例");

        CompleteEvent event = CompleteEvent
                .successEvent(instanceId)
                .setMessage("强制结束")
                .setFinalStatus(JobInstanceStatus.SKIPPED)
                .setEndTime(LocalDateTime.now());
        idcPlugin.getStatusService().fireCompleteEvent(event);
    }

    public void cancle(CancleRequest req) {
        JobInstance instance = jobInstanceRepository.findOne(req.getInstanceId());
        Assert.isTrue(instance != null, "找不到此实例");
        CompleteEvent event = CompleteEvent
                .successEvent(req.getInstanceId())
                .setMessage("取消任务")
                .setFinalStatus(JobInstanceStatus.CANCLED)
                .setEndTime(LocalDateTime.now());
        idcPlugin.getStatusService().fireCompleteEvent(event);
    }

    public PageData<ExecutionLog> getJobInstanceLog(Integer id, Pager pager) {
        Pageable page = new PageRequest(pager.getPage(), pager.getLimit(), new Sort(Direction.ASC, "id"));
        Page<ExecutionLog> data = logRepository.findByInstanceId(id, page);
        return new PageData<>((int) data.getTotalElements(), data.getContent());
    }

    @Inject
    private JobInstanceRepository repository;

    public PageData<JobInstance> findJobInstance(JobInstanceQuery queryObject, Pager pager) {
        Pageable pgr = new PageRequest(pager.getPage(), pager.getLimit(), new Sort(Direction.DESC, "startTime"));
        Page<JobInstance> result = queryObject == null ? repository.findAll(pgr)
                : repository.findAll(queryObject.toSpecification(), pgr);
        return new PageData<>(result.getNumberOfElements(), result.getContent());
    }


    public JobInstance getJobInstance(JobKey jobKey, long shouldFireTime) {
        return repository.findOne(jobKey.getJobId(), jobKey.getJobGroup(), shouldFireTime);
    }

    public JobInstance getJobInstance(Integer id) {
        JobInstance instance = repository.findOne(id);
        Assert.isTrue(instance != null, "任务实例 %s 不存在", id);
        return instance;
    }

    public List<JobInstance> getWorkflowSubInstance(Integer id) {
        throw new UnsupportedOperationException("not supported yet.");
    }

    public List<Assignee> getAllAssignee() {
        return repository.findAllAssignee().stream().map(id -> {
            Assignee assignee = new Assignee();
            assignee.setAssignee(id);
            return assignee;
        }).collect(Collectors.toList());

    }

}
