package com.iwellmass.idc.app.scheduler;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.IDCPluginService;
import com.iwellmass.idc.app.repo.JobRepository;
import com.iwellmass.idc.app.repo.PluginVersionRepository;
import com.iwellmass.idc.app.repo.TaskRepository;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.model.Task;
import com.iwellmass.idc.model.TaskKey;
import com.iwellmass.idc.quartz.IDCPlugin;

@Component
public class IDCPluginServiceImpl implements IDCPluginService {

	@Inject
	private PluginVersionRepository pluginVersionRepository;
	
	@Inject
	private TaskRepository taskRepository;
	
	@Inject
	private JobRepository jobRepository;

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
}
