package com.iwellmass.idc.quartz;

import static com.iwellmass.idc.quartz.IDCContextKey.JOB_DISPATCH_TYPE;

import java.util.Date;

import javax.sql.DataSource;

import org.junit.Test;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import com.alibaba.fastjson.JSON;
import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobKey;
import com.iwellmass.idc.model.ScheduleType;

public class IDCPluginTest {
	
	private static final String lqd_01 = "lqd_01";
	private static final String group = "test";
	
	private static final Date _1_1 = new Date(1514736000000L);
	private static final Date _9_1 = new Date(1535731200000L);

	@Test
	public void testSchedule() throws Exception {

		
		IDCSchedulerFactory factory = new IDCSchedulerFactory();
		factory.setDataSource(dataSource());
		factory.setIdcDriverDelegateClass(SimpleIDCDriverDelegate.class.getName());
		
		Scheduler scheduler = factory.getScheduler();
		
		scheduler.clear();
		
		scheduler.start();
		
		JobDetail jdt = JobBuilder.newJob(SimpleJob.class)
			.withIdentity(lqd_01, group)
			.requestRecovery()
			.storeDurably()
			.build();
		
		
		Trigger trigger = TriggerBuilder.newTrigger()
			.withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 L * ? *")
				.withMisfireHandlingInstructionIgnoreMisfires())
			.startAt(_1_1)
			.withIdentity(lqd_01, group)
			.build();
		
		Job job = new Job();
		job.setJobKey(new JobKey(lqd_01, group));
		job.setScheduleType(ScheduleType.MONTHLY);
		job.setDispatchType(DispatchType.AUTO);
		
		IDCContextKey.JOB_JSON.applyPut(trigger.getJobDataMap(), JSON.toJSONString(job));
		JOB_DISPATCH_TYPE.applyPut(trigger.getJobDataMap(), DispatchType.AUTO);
		
		scheduler.scheduleJob(jdt, trigger);
		
		Thread.sleep(10 * 60 * 1000);
		
	}
	private DataSource dataSource() {
		SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
		dataSource.setUrl("jdbc:mysql://austin.realhyx.com:3306/test?characterEncoding=utf8&useSSL=false");
		dataSource.setUsername("root");
		dataSource.setPassword("000000oO)");
		return dataSource;
	}
}
