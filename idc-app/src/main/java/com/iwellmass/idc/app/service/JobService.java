package com.iwellmass.idc.app.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.iwellmass.idc.IDCLogger;
import com.iwellmass.idc.executor.ProgressEvent;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.Pager;
import com.iwellmass.idc.app.mapper.JobRuntimeMapper;
import com.iwellmass.idc.app.mapper.MapperUtil;
import com.iwellmass.idc.app.model.Assignee;
import com.iwellmass.idc.app.model.JobQuery;
import com.iwellmass.idc.app.model.PauseRequest;
import com.iwellmass.idc.app.repo.JobRepository;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.vo.JobRuntimeListVO;
import com.iwellmass.idc.app.vo.JobRuntime;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleProperties;
import com.iwellmass.idc.model.ScheduleType;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.TaskType;
import com.iwellmass.idc.model.Workflow;
import com.iwellmass.idc.quartz.IDCPlugin;

@Service
public class JobService {

	static final Logger LOGGER = LoggerFactory.getLogger(JobService.class);

	@Inject
	private JobRepository jobRepository;
	
	@Inject
	private TaskRepository taskRepository;

	@Inject
	private JobRuntimeMapper jobRuntimeMapper;
	
	@Inject
	private IDCPlugin idcPlugin;

    @Inject
    private IDCLogger idcLogger;
	
	@Inject
	private WorkflowService workflowService;
	
	public JobRuntime getJobRuntime(JobKey jobKey) {
		return  jobRuntimeMapper.selectJobRuntime(jobKey);
	}

	public PageData<Job> findJob(JobQuery jobQuery, Pager pager) {
		Specification<Job> spec = jobQuery == null ? null : jobQuery.toSpecification();
		Page<Job> job = jobRepository.findAll(spec, new PageRequest(pager.getPage(), pager.getLimit()));
		return new PageData<Job>((int)job.getTotalElements(), job.getContent());
	}
	
	// available 
	public List<Job> findAvailableDependency(ScheduleType scheduleType) {
		Specification<Job> spec = (root, cq, cb) -> {
			return cb.and(
					cb.equal(root.get("scheduleType"), scheduleType),
					root.get("taskType").in(TaskType.WORKFLOW, TaskType.NODE_TASK)
			);
		};
		return jobRepository.findAll(spec);
	}

	public List<Job> getWorkflowJob() {
		return jobRepository.findByTaskType(TaskType.WORKFLOW);
	}

	public List<Job> getWorkflowJob(JobKey jobKey) {
		return jobRepository.findSubJobs(jobKey.getJobId(), jobKey.getJobGroup());
	}

	public List<Assignee> getAllAssignee() {
		return jobRepository.findAllAssignee().stream().map(id -> {
			Assignee asg = new Assignee();
			asg.setAssignee(id);
			return asg;
		}).collect(Collectors.toList());
	}

	public static <T> Specifications<T> empty() {
		return Specifications.where((root, query, cb) -> {
			return cb.equal(cb.literal(1), 1);
		});
	}

	public Job findJob(JobKey jobKey) {
		return jobRepository.findOne(jobKey);
	}

	public PageData<JobRuntimeListVO> getJobRuntime(JobQuery jobQuery, Pager pager) {
		return MapperUtil.doQuery(pager, ()->jobRuntimeMapper.selectJobRuntimeList(jobQuery));
	}

	public void schedule(ScheduleProperties sp) {
		try {
			Task task = taskRepository.findOne(new TaskKey(sp.getTaskId(), sp.getTaskGroup()));
			
			if (task.getTaskType() == TaskType.WORKFLOW) {
				Workflow workflow = new Workflow();
				workflow.setWorkflowId(task.getWorkflowId());
				workflow.setGraph(task.getGraph());
				workflow.setTaskId(task.getTaskId());
				workflow.setTaskGroup(task.getTaskGroup());
				workflowService.saveWorkflow(workflow);
			}
			
			// TODO 检查 task 完整性
			idcPlugin.schedule(sp);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void reschedule(JobKey jobKey, ScheduleProperties sp) {
		try {
			Job job = jobRepository.findOne(jobKey);
			
			Task task = taskRepository.findOne(job.getTaskKey());
			
			if (sp != null) {
				sp.setTaskId(job.getTaskId());
				sp.setTaskGroup(job.getTaskGroup());
				sp.setDispatchType(job.getDispatchType());
			}
			
			
			if (task.getTaskType() == TaskType.WORKFLOW) {
				Workflow workflow = new Workflow();
				workflow.setWorkflowId(task.getWorkflowId());
				workflow.setGraph(task.getGraph());
				workflow.setTaskId(task.getTaskId());
				workflow.setTaskGroup(task.getTaskGroup());
				workflowService.saveWorkflow(workflow);
			}
			
			idcPlugin.reschedule(jobKey, sp);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void unschedule(JobKey jobKey) {
		try {
			idcPlugin.unschedule(jobKey);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
		
	}

	public void pause(PauseRequest request) {
		try {
			idcPlugin.pause(request);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}

	public void resume(JobKey jobKey) {
		try {
			idcPlugin.resume(jobKey);
		} catch (SchedulerException e) {
			throw new AppException(e.getMessage(), e);
		}
	}


    public void saveRuntimeLog(ProgressEvent progressEvent) {
        idcLogger.log(progressEvent.getInstanceId(),progressEvent.getMessage());
    }
}
