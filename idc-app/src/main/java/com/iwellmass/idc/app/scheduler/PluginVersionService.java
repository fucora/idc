package com.iwellmass.idc.app.scheduler;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.iwellmass.idc.app.repo.PluginVersionRepository;
import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.quartz.IDCPlugin;

@Component
public class PluginVersionService {

	@Inject
	private PluginVersionRepository pluginVersionRepository;

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

}
