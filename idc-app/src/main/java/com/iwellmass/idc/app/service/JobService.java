package com.iwellmass.idc.app.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import com.iwellmass.common.criteria.SpecificationBuilder;
import com.iwellmass.common.exception.AppException;
import com.iwellmass.common.util.PageData;
import com.iwellmass.common.util.QueryUtils;
import com.iwellmass.idc.app.vo.Assignee;
import com.iwellmass.idc.app.vo.JobQueryParam;
import com.iwellmass.idc.app.vo.JobRuntimeVO;
import com.iwellmass.idc.app.vo.JobVO;
import com.iwellmass.idc.scheduler.model.AbstractJob;
import com.iwellmass.idc.scheduler.model.Job;
import com.iwellmass.idc.scheduler.repository.AllJobRepository;
import com.iwellmass.idc.scheduler.repository.JobRepository;

@Service
public class JobService {

	@Resource
	JobRepository jobRepository;
	
	@Resource
	AllJobRepository allJobRepository;

	public Job getJob(String id) {
		return jobRepository.findById(id).orElseThrow(()-> new AppException("任务 '" + id + "' 不存在"));
	}
	
	public PageData<JobRuntimeVO> query(JobQueryParam jqm) {
		Specification<Job> spec = SpecificationBuilder.toSpecification(jqm);
		return QueryUtils.doJpaQuery(jqm, pageable -> {
			return jobRepository.findAll(spec, pageable).map(job -> {
				JobRuntimeVO vo = new JobRuntimeVO();
				BeanUtils.copyProperties(job, vo);;
				return vo;
			});
		});
	}
	
	public List<Assignee> getAllAssignee() {
		return jobRepository.findAllAssignee().stream().map(Assignee::new).collect(Collectors.toList());
	}
	
	public JobVO get(String id) {
		JobVO jobVO = new JobVO();
		Job job = getJob(id);
		BeanUtils.copyProperties(job, jobVO);
		return jobVO;
	}

	public void clear(String id) {

	}

	public void createTask(String id, String scheduleId) {

	}
//
//    @Transactional
//    public void add(Job task) {
//    	// 设置默认值
//        if (task.getTaskType() == TaskType.WORKFLOW) {
////            task.setGroup("idc");
//            task.setContentType("workflow");
//            // task.setWorkflowId(task.getTaskGroup() + "-" + task.getTaskId());
//        }
//    	
//        Job check = taskRepository.findById(task.getId()).get();
//        
//      
//        // 排序字段
//        task.setUpdatetime(LocalDateTime.now());
//        
//        taskRepository.save(task);
//        // idcPlugin.refresh(task);
//    }
//
//    @Transactional
//    public void update(Job task) {
//        Job oldTask = taskRepository.findById(task.getId()).get();
//        
//        if (oldTask == null) {
//        	oldTask = task;
//        } else {
//        	oldTask.setContentType(task.getContentType());
//        	oldTask.setName(task.getName());
//        	oldTask.setDescription(task.getDescription());
//            // 子任务重新注册时我们需要刷新他的参数
//            if (task.getTaskType() == TaskType.WORKFLOW) {
//            	oldTask.setParam(task.getParam());
//            }
//        }
//        
//        oldTask.setUpdatetime(LocalDateTime.now());
//        
//        taskRepository.save(oldTask);
//    }
//
//    public Job getTask(String batchNo) {
//        return taskRepository.findById(batchNo).get();
//    }
//
//    public List<Job> getTasksByType(TaskType taskType) {
//        Sort sort = new Sort(Direction.DESC, "updatetime");
//        // return taskRepository.findByTaskType(taskType, sort);
//        return null;
//    }
//
//
//    @Transactional
//    public Job modifyGraph(Job task) {
//        Assert.isTrue(null != task.getId(), "未传入taskId");
//        Assert.isTrue(null != task.getTaskGroup(), "未传入taskGroup");
//        
//        // 检查是否存在该task
//        Job oldTask = taskRepository.findById(task.getId()).get();
//        if (oldTask == null) {
//            throw new AppException("未查找到该taskKey对应的task信息");
//        }
//        
////        List<WorkflowEdge> edges = IDCUtils.parseWorkflowEdge(task.getGraph());
////        for (WorkflowEdge we : edges) {
////            we.setParentTaskKey(task.getTaskKey()); // 刷新 parentTaskKey
////        }
//        
//        // 刷新 version
//        Workflow workflow = new Workflow();
////        workflow.setTaskId(task.getTaskId());
////        workflow.setTaskGroup(task.getTaskGroup());
////        workflow.setGraph(task.getGraph());
//        // 工作流是否已改变
//        workflowService.saveWorkflow(workflow);
//
//    	// 更新刷新时间
//    	oldTask.setUpdatetime(LocalDateTime.now());
//    	// 更新工作流的画图数据
////    	oldTask.setGraph(task.getGraph());
////    	oldTask.setWorkflowId(workflow.getWorkflowId());
//    	return taskRepository.save(oldTask);
//    }
//
//    /**
//     * 查找Node_task 下的parameter
//     *
//     * @param taskKey
//     * @return
//     */
//    public List<ExecParam> getParam(String batchNo) {
//        Job task = taskRepository.findById(batchNo).get();
//        if (task == null) {
//            throw new AppException("未找到指定task任务");
//        }
//        return task.getParam();
//    }
//
//    /**
//     * 查询 Task 声明的所有参数信息
//     */
//    public List<String> getParams(String taskKey) {
//        // 查询满足要求的Task
//        Job task = taskRepository.findById(taskKey).get();
//        if (task == null) {
//            throw new AppException("未查找到指定task:" + taskKey);
//        }
//        if (task.getTaskType().equals(TaskType.WORKFLOW)) {
//            //  工作流任务
//           //  return taskRepository.findAllSubTask(taskKey.getTaskId(), taskKey.getTaskGroup()).stream().map(SimpleTaskVO::new).collect(Collectors.toList());
//        	return null;
//        } else {
//            // NODE_TASK
//            return null;
//        }
//    }
//
//	public void validate(String batchNo) {
//		Job task = taskRepository.findById(batchNo).get();
//		if (task.getTaskType() == TaskType.WORKFLOW) {
//			Workflow workflow = null;//workflowService.findOne(task.getWorkflowId());
//			Assert.isTrue(workflow != null, "未配置工作流");
//		}
//	}

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

}
