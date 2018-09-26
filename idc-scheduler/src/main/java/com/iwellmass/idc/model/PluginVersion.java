package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import com.iwellmass.idc.quartz.IDCPlugin;

@Entity
@Table(name = "t_idc_plugin")
public class PluginVersion implements Persistable<String>{

	private static final long serialVersionUID = 3774081184231467500L;

	private String version = IDCPlugin.VERSION;
	
	private Long dependencyVersion;

	@Transient
	private boolean isNew = false;

	@Id
	@Column(length = 10)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "dependency_version")
	public Long getDependencyVersion() {
		return dependencyVersion;
	}

	public void setDependencyVersion(Long dependencyVersion) {
		this.dependencyVersion = dependencyVersion;
	}

	@Transient
	@Override
	public String getId() {
		return getVersion();
	}

	@Transient
	@Override
	public boolean isNew() {
		return isNew;
	}
	
	@Transient
	public PluginVersion asNew() {
		this.isNew = true;
		return this;
	}
}
