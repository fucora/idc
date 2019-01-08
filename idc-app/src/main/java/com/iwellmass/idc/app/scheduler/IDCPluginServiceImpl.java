package com.iwellmass.idc.app.scheduler;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.iwellmass.idc.DependencyService;
import com.iwellmass.idc.app.repo.*;
import com.iwellmass.idc.model.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.IDCPluginService;
import com.iwellmass.idc.quartz.IDCPlugin;

@Component
public class IDCPluginServiceImpl implements IDCPluginService {

	@Inject
	private PluginVersionRepository pluginVersionRepository;
	
	@Inject
	private TaskRepository taskRepository;
	
	@Inject
	private JobRepository jobRepository;

	@Inject
	private JobInstanceRepository jobInstanceRepository;

	@Inject
	private JobDependencyRepository jobDependencyRepository;

	public PluginVersion initPlugin() {
		if (pluginVersionRepository.exists(IDCPlugin.VERSION)) {
			return pluginVersionRepository.findOne(IDCPlugin.VERSION);
		} else {
			try {
				PluginVersion version = new PluginVersion().asNew();
				version.setVersion(IDCPlugin.VERSION);
				pluginVersionRepository.save(version);
				return version;
			} catch (RuntimeException e) {
				try {
					PluginVersion version = pluginVersionRepository.findOne(IDCPlugin.VERSION);
					return version;
				} catch (RuntimeException e2) {
					throw e2;
				}
			}
		}
	}

	@Transactional
	public void saveTask(Task task) {
		taskRepository.save(task);
	}

	@Transactional
	public Task findTask(TaskKey taskKey) {
		return taskRepository.findOne(taskKey);
	}

	@Transactional
	public List<Task> findTasks(List<TaskKey> taskKey) {
		return taskKey.stream().map(this::findTask).collect(Collectors.toList());
	}

	@Transactional
	public void saveJob(Job job) {
		jobRepository.save(job);
	}

	@Transactional
	public Job findJob(JobKey jobKey) {
		return jobRepository.findOne(jobKey);
	}

	@Override
	public JobInstance findByInstanceId(Integer instanceId) {
		return jobInstanceRepository.findOne(instanceId);
	}

	@Override
	public void saveJobDependencies(List<JobDependency> jobDependencies) {
		jobDependencyRepository.save((Iterable<JobDependency>) () -> jobDependencies.iterator());
	}

	@Override
	public void clearJobDependencies(JobKey jobKey) {
		jobDependencyRepository.deleteByJob(jobKey);
	}
}
