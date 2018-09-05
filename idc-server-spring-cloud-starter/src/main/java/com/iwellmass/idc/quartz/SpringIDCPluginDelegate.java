package com.iwellmass.idc.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerPlugin;

import com.iwellmass.idc.IDCServerConfiguration;

public class SpringIDCPluginDelegate implements SchedulerPlugin{

	// from properties
	private String delegateClassName;
	
	// 私有变量
	private SchedulerPlugin delegate;
	
	@Override
	public void initialize(String name, Scheduler scheduler, ClassLoadHelper loadHelper) throws SchedulerException {
		this.delegate = IDCServerConfiguration.idcPlugin();
		this.delegate.initialize(name, scheduler, loadHelper);
	}

	@Override
	public void start() {
		delegate.start();
	}

	@Override
	public void shutdown() {
		delegate.shutdown();
	}

	public String getDelegateClassName() {
		return delegateClassName;
	}

	public void setDelegateClassName(String delegateClassName) {
		this.delegateClassName = delegateClassName;
	}

}
