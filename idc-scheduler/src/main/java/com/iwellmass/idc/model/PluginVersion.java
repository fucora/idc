package com.iwellmass.idc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.data.domain.Persistable;

import com.iwellmass.idc.quartz.IDCPlugin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "t_idc_plugin")
public class PluginVersion implements Persistable<String> {

	private static final long serialVersionUID = 3774081184231467500L;

	@Id
	private String version = IDCPlugin.VERSION;

	@Column(name = "task_seq")
	private int taskSeq;

	@Column(name = "instance_seq")
	private int instanceSeq;

	@Transient
	private boolean isNew = false;

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
