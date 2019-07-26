package com.iwellmass.idc.scheduler.quartz;

import com.iwellmass.idc.app.message.TaskEventPlugin;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.DirectSchedulerFactory;
import org.quartz.simpl.PropertySettingJobFactory;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.JobStore;
import org.quartz.spi.SchedulerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 调度器工厂
 *
 * @author hawkins
 * @date 2019-07-23 17:50
 */
public class IDCSchedulerFactory {

    private static Logger logger = LoggerFactory.getLogger(IDCSchedulerFactory.class);

    private static String schedulerName = "idc-schd";
    private static String schedulerInstanceId = "idc-schd-01";
    private static String rmiRegistryHost = null;
    private static int rmiRegistryPort = 0;
    private static int idleWaitTime = -1;
    private static int dbFailureRetryInterval = -1;
    private static boolean jmxExport = false;
    private static String jmxObjectName = null;


    public static Scheduler getScheduler(TaskEventPlugin taskEventPlugin, JobStore jobStore) {
        SimpleThreadPool threadPool = new SimpleThreadPool();
        threadPool.setThreadCount(1);
        threadPool.setThreadNamePrefix("idc-schd-thread");

        Map<String, SchedulerPlugin> schedulerPluginMap = new HashMap<>();
        schedulerPluginMap.put(TaskEventPlugin.NAME, taskEventPlugin);

        try {
            DirectSchedulerFactory.getInstance().createScheduler(schedulerName, schedulerInstanceId, threadPool, jobStore, schedulerPluginMap,
                    rmiRegistryHost, rmiRegistryPort, idleWaitTime, dbFailureRetryInterval, jmxExport, jmxObjectName);

            Scheduler scheduler = DirectSchedulerFactory.getInstance().getScheduler(schedulerName);
            scheduler.setJobFactory(new PropertySettingJobFactory());
            return scheduler;
        } catch (SchedulerException e) {
            logger.info("create scheduler error!", e);
            throw new RuntimeException(e);
        }

    }
}
