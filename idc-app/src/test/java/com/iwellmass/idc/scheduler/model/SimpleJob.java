package com.iwellmass.idc.scheduler.model;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * ${DESC}
 *
 * @author hawkins
 * @date 2019-07-24 17:29
 */
public class SimpleJob implements Job {

    public static final CyclicBarrier localBarrier = new CyclicBarrier(2);

    public static final String TEST_DATA = "test_data";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        Integer awitTime = (Integer)context.getJobDetail().getJobDataMap().get("await-time");

        if (awitTime == null) awitTime = 10;

        System.out.println("开始执行任务：" + context.getFireInstanceId());

        JobDataMap jobDataMap = context.getMergedJobDataMap();

        System.out.println("收到参数：" + jobDataMap.getString(TEST_DATA));

        try {
            localBarrier.await(awitTime, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
