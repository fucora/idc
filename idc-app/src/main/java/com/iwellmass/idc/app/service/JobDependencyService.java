package com.iwellmass.idc.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.iwellmass.idc.app.repo.JobDependencyRepository;
import com.iwellmass.idc.model.JobDependency;
import com.iwellmass.idc.model.JobKey;

@Service
public class JobDependencyService {
	
	private JobDependencyRepository jobDepRepo;
	
	public void saveDependencies(JobKey jobKey, List<JobKey> dependencies) {
		// 先 validate
		
	}

	private void validate(JobKey jobPk, List<JobDependency> deps) {

		/*if (Utils.isNullOrEmpty(deps)) {
			return;
		}

		TriggerKey triggerKey = new TriggerKey(jobPk.getJobId(), jobPk.getJobGroup());
		
		
		workflowService.validate(deps);
		
		
		// 初始化
		DirectedAcyclicGraph<TriggerKey, Dependency> depGraph = new DirectedAcyclicGraph<>(Dependency.class);
		List<JobDependency> existingDeps = dependencyRepo.findAll();
		if (existingDeps != null) {
			for (JobDependency dep : existingDeps) {
				TriggerKey srcPk = new TriggerKey(dep.getSrcJobId(), dep.getSrcJobGroup());
				TriggerKey targetPk = new TriggerKey(dep.getJobId(), dep.getJobGroup());
				depGraph.addVertex(srcPk);
				depGraph.addVertex(targetPk);
				depGraph.addEdge(srcPk, targetPk);
			}
		}

		// 检查依赖
		depGraph.addVertex(triggerKey);
		for (JobDependency dep : deps) {
			TriggerKey target = new TriggerKey(dep.getJobId(), dep.getJobGroup());
			try {
				depGraph.addVertex(target);
				depGraph.addEdge(triggerKey, target);
			} catch (IllegalArgumentException e) {
				throw new AppException("无法添加 " + triggerKey + " -> " + target + " 依赖: " + e.getMessage());
			}
			dep.setSrcJobId(triggerKey.getName());
			dep.setSrcJobGroup(triggerKey.getGroup());
		}*/
	}
}
