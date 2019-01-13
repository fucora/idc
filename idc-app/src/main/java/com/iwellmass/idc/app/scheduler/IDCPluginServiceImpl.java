package com.iwellmass.idc.app.scheduler;

import java.beans.PropertyDescriptor;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.app.repo.IDCConfigRepository;
import com.iwellmass.idc.app.repo.JobDependencyRepository;
import com.iwellmass.idc.app.repo.JobRepository;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.app.repo.WorkflowEdgeRepository;
import com.iwellmass.idc.model.IDCProp;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.model.WorkflowEdge;
import com.iwellmass.idc.quartz.IDCPluginConfig;
import com.iwellmass.idc.quartz.IDCPluginService;

public class IDCPluginServiceImpl implements IDCPluginService {

	private static final Logger LOGGER = LoggerFactory.getLogger(IDCPluginServiceImpl.class);

	@Inject
	private TaskRepository taskRepository;

	@Inject
	private JobRepository jobRepository;

	@Inject
	private WorkflowEdgeRepository workflowRepo;

	@Inject
	private JobDependencyRepository jobDependencyRepo;

	@Inject
	private IDCConfigRepository configRepository;

	private final IDCPluginConfig config = new IDCPluginConfig();

	@Override
	public IDCPluginConfig getConfig() {
		if (config.getConfigVersion() == null || configRepository.checkDirty(config.getConfigVersion())) {
			configRepository.findAll().forEach(prop -> {
				if (prop.getName() == "version") {
					config.setConfigVersion(prop.getUpdatetime());
				}
				setIDCProp(config, prop);
			});
		}
		return config;
	}

	private static void setIDCProp(IDCPluginConfig config, IDCProp prop) {

		String name = camelCaseName(prop.getName());

		PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(IDCPluginConfig.class, name);

		if (pd == null && prop.getValue() != null) {
			LOGGER.warn("'{}' setter not found.", prop.getName());
		} else {
			try {
				Class<?> type = pd.getPropertyType();

				Object v = prop.getValue();
				if (type != String.class) {
					MethodHandle mh = MethodHandles.lookup().findStatic(type, "valueOf",
							MethodType.methodType(type, String.class));
					v = mh.invoke(prop.getValue());
				}
				pd.getWriteMethod().invoke(config, v);
			} catch (Throwable e) {
				LOGGER.error("cannot set '{}' for {}", prop.getValue(), name);
			}
		}

	}

	private static String camelCaseName(String name) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '.') {
				i++;
				builder.append(Character.toUpperCase(name.charAt(i)));
			} else {
				builder.append(name.charAt(i));
			}
		}
		return builder.toString();
	}

	@Transactional
	public void saveTask(Task task) {
		taskRepository.save(task);
	}

	@Transactional
	public Task getTask(TaskKey taskKey) {
		return taskRepository.findOne(taskKey);
	}

	@Transactional
	public void saveJob(Job job) {
		jobRepository.save(job);
	}

	@Transactional
	public Job getJob(JobKey jobKey) {
		return jobRepository.findOne(jobKey);
	}

	@Override
	public List<TaskKey> getSuccessors(TaskKey parentTaskKey, TaskKey taskKey) {
		return workflowRepo.findSuccessors(parentTaskKey.getTaskId(), parentTaskKey.getTaskGroup(), taskKey.getTaskId(),
				taskKey.getTaskGroup()).stream().map(WorkflowEdge::getTaskKey).collect(Collectors.toList());
	}

	@Override
	public List<TaskKey> getPredecessors(TaskKey parentTaskKey, TaskKey taskKey) {
		return workflowRepo.findPredecessors(parentTaskKey.getTaskId(), parentTaskKey.getTaskGroup(),
				taskKey.getTaskId(), taskKey.getTaskGroup()).stream().map(WorkflowEdge::getSrcTaskKey)
				.collect(Collectors.toList());
	}

	@Override
	public List<JobDependency> getJobDependencies(JobKey jobKey) {
		return jobDependencyRepo.findDependencies(jobKey.getJobId(), jobKey.getJobGroup());
	}

	@Override
	public List<WorkflowEdge> getTaskDependencies(TaskKey taskKey) {
		return workflowRepo.findByParentTaskIdAndParentTaskGroup(taskKey.getTaskId(), taskKey.getTaskGroup());
	}
}
