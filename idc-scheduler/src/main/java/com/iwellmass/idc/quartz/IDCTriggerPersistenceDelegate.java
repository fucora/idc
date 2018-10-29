package com.iwellmass.idc.quartz;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.quartz.JobDetail;
import org.quartz.impl.jdbcjobstore.CronTriggerPersistenceDelegate;
import org.quartz.spi.OperableTrigger;

public class IDCTriggerPersistenceDelegate extends CronTriggerPersistenceDelegate implements IDCConstants{
	
	@Override
	public int insertExtendedTriggerProperties(Connection conn, OperableTrigger trigger, String state,
			JobDetail jobDetail) throws SQLException, IOException {
		// 保存到我们的信息表
		//		Job idcJob = JSON.parseObject(JOB_JSON.applyGet(trigger.getJobDataMap()), Job.class);
		//		getContext().createJob(idcJob);
		return super.insertExtendedTriggerProperties(conn, trigger, state, jobDetail);
	}
}
