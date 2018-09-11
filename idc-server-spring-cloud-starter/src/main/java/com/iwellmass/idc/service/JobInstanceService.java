package com.iwellmass.idc.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.repo.JobInstanceRepository;

@Service
public class JobInstanceService {

	@Inject
	private JobInstanceRepository jobInstanceRepository;

	public void redo(Integer instanceId) {
		JobInstance instance = jobInstanceRepository.findOne(instanceId);
		if (instance == null) {
			throw new AppException("重跑失败, 任务实例 '" + instanceId + "' 不存在");
		}
		// TODO redo
	}

}
