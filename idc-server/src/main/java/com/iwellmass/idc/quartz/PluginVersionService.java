package com.iwellmass.idc.quartz;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.iwellmass.common.exception.AppException;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.repo.PluginVersionRepository;

@Component
public class PluginVersionService {

	private static final Throwable NEVER_HAPPEN_EX = new Exception("Should never happen!");
	private static final Integer BASE_INSTANCE_SEQ = 10000;
	
	@Inject
	private PluginVersionRepository pluginVersionRepository;

	@Transactional
	public int generateInstanceId() {
		Throwable ex = NEVER_HAPPEN_EX;
		for (int i = 0; i < 10; i++) {
			try {
				pluginVersionRepository.increaseInstanceSeqAndGet(IDCPlugin.VERSION);
				PluginVersion e = pluginVersionRepository.findOne(IDCPlugin.VERSION);
				return e.getInstanceSeq().intValue();
			} catch (Throwable e) {
				// try again
				ex = e;
			}
		}
		throw new AppException("不能申请实例 ID: " + ex.getMessage());
	}

	public PluginVersion init() {
		if (pluginVersionRepository.exists(IDCPlugin.VERSION)) {
			return pluginVersionRepository.findOne(IDCPlugin.VERSION);
		} else {
			try {
				PluginVersion version = new PluginVersion().asNew();
				version.setVersion(IDCPlugin.VERSION);
				version.setInstanceSeq(BASE_INSTANCE_SEQ.longValue());
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

}
