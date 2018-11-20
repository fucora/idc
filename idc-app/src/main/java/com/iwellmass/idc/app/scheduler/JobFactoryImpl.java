package com.iwellmass.idc.app.scheduler;

import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.simpl.PropertySettingJobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class JobFactoryImpl extends PropertySettingJobFactory {
	
	@Inject
	private AutowireCapableBeanFactory autowireCapableBeanFactory;
	
	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
		// 新建任务
        Job job = super.newJob(bundle, scheduler);
        try {
        	autowireCapableBeanFactory.autowireBean(job);
        } catch (Throwable e) {
        	throw new SchedulerException("创建 " + bundle.getJobDetail().getJobClass() + " 时失败", e);
        }
        return job;
	}
}
