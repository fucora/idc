package com.iwellmass.dispatcher.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iwellmass.common.ServiceResult;
import com.iwellmass.dispatcher.admin.dao.Pager;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcApplicationMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskUpdateHistoryMapper;
import com.iwellmass.dispatcher.admin.dao.mapper.DdcTaskWorkflowMapper;
import com.iwellmass.dispatcher.admin.dao.model.*;
import com.iwellmass.dispatcher.admin.service.ITaskService;
import com.iwellmass.dispatcher.admin.service.aspect.DdcPermission;
import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.task.DmallTask;
import com.iwellmass.dispatcher.common.task.DmallTaskDisallowConcurrent;
import com.iwellmass.dispatcher.common.utils.DateUtils;
import com.iwellmass.dispatcher.thrift.bvo.WorkflowTask;

import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private DdcApplicationMapper appMapper;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private DdcTaskMapper taskMapper;

    @Autowired
    private DdcTaskWorkflowMapper taskWorkflowMapper;

    @Autowired
    private DdcTaskUpdateHistoryMapper taskUpdateMapper;

    private final static Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Override
    @Transactional
    @DdcPermission
    public void deleteTask(int appId, int taskId) throws DDCException {

        DdcTask task = taskMapper.selectByPrimaryKey(taskId);
        taskMapper.deleteByPrimaryKey(taskId);

        DdcTaskUpdateHistoryExample example = new DdcTaskUpdateHistoryExample();
        example.createCriteria().andTaskIdEqualTo(taskId);
        taskUpdateMapper.deleteByExample(example);

        JobKey jobKey = buildJobKey(task);
        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("任务编号：{}，任务删除失败！", taskId);
            throw new DDCException("任务编号：{%d}，任务删除失败！", taskId);
        }
    }

    @Override
    @Transactional
    @DdcPermission
    public void enableTask(int appId, int taskId) throws DDCException {

        DdcTask task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            logger.error("任务不存在，不能启用，任务编号：{}", taskId);
            throw new DDCException("任务不存在，不能启用，任务编号：{%d}", taskId);
        }

        Date now = new Date();
        task.setTaskStatus(Constants.TASK_STATUS_ENABLED);
        //TODO:
//        task.setUpdateUser(LoginContext.getLoginContext().getUserName());
        task.setUpdateUser("admin");
        task.setUpdateTime(now);
        taskMapper.updateByPrimaryKeySelective(task);

        DdcTaskUpdateHistory record = new DdcTaskUpdateHistory();
        record.setTaskId(taskId);
//        record.setUpdateUser(LoginContext.getLoginContext().getUserName());
        record.setUpdateUser("admin");
        record.setUpdateTime(now);
        record.setUpdateDetail("任务被启用！");
        taskUpdateMapper.insertSelective(record);

        JobDataMap map = new JobDataMap();

        int category = task.getTaskCategoty();
        if (category == Constants.TASK_CATEGORY_BASIC || category == Constants.TASK_CATEGORY_WORKFLOW) {
            map.put("taskId", taskId);
        } else {
            logger.error("任务名称{}，不支持的任务大类{}", task.getTaskName(), task.getTaskCategoty());
            throw new DDCException("任务名称{%s}，不支持的任务大类{%d}", task.getTaskName(), task.getTaskCategoty());
        }
        try {
            JobKey jobKey = buildJobKey(task);
            JobDetail job = buildJob(task, jobKey, map);

            TriggerKey triggerKey = buildTriggerKey(task);
            Trigger trigger = buildTrigger(task, triggerKey);

            scheduleJob(job, jobKey, trigger, triggerKey);
        } catch (SchedulerException e) {
            logger.error("启用任务失败，任务名称{}，错误信息{}", task.getTaskName(), e);
            throw new DDCException("启用任务失败，任务名称{%s}，错误信息{%s}", task.getTaskName(), e.getMessage());
        }
    }

    /**
     * Select by task id and app id ddc task.
     * 通过appId和taskId来查询task，为了验证数据的操作权限
     *
     * @param appId  the app id
     * @param taskId the task id
     * @return the ddc task
     * @throws DDCException the ddc exception
     */
    private DdcTask selectByTaskIdAndAppId(int appId, int taskId) throws DDCException {
        DdcTaskExample taskExample = new DdcTaskExample();
        DdcTaskExample.Criteria taskCriteria = taskExample.createCriteria();
        taskCriteria.andAppIdEqualTo(appId);
        taskCriteria.andTaskIdEqualTo(taskId);
        List<DdcTask> ddcTaskList = taskMapper.selectByExample(taskExample);
        if (ddcTaskList == null || ddcTaskList.size() != 1) {
            throw new DDCException("查询task数据异常");
        }
        return ddcTaskList.get(0);
    }

    @Override
    @Transactional
    @DdcPermission
    public void disableTask(int appId, int taskId) throws DDCException {

        Date now = new Date();
        DdcTask task = selectByTaskIdAndAppId(appId, taskId);

        task.setTaskStatus(Constants.TASK_STATUS_DISABLED);
        task.setUpdateUser("admin");
        task.setUpdateUser("admin");
        task.setUpdateTime(now);
        taskMapper.updateByPrimaryKeySelective(task);

        DdcTaskUpdateHistory record = new DdcTaskUpdateHistory();
        record.setTaskId(taskId);
        task.setUpdateUser("admin");
        record.setUpdateUser("admin");
        record.setUpdateTime(now);
        record.setUpdateDetail("任务被停用！");
        taskUpdateMapper.insertSelective(record);

        JobKey jobKey = buildJobKey(task);

        try {
            scheduler.deleteJob(jobKey);
        } catch (SchedulerException e) {
            logger.error("禁用任务失败，任务名称{}，错误信息{}", task.getTaskName(), e);
            throw new DDCException("禁用任务失败，任务名称{%s}，错误信息{%s}", task.getTaskName(), e.getMessage());
        }
    }

    @Override
    @DdcPermission
    public void executeTask(int appId, int taskId) throws DDCException {

        DdcTask task = selectByTaskIdAndAppId(appId, taskId);
        if (task == null || task.getTaskStatus() == Constants.TASK_STATUS_DISABLED) {
            logger.error("任务不存在或未启用，不能手动执行，任务编号：{}", taskId);
            throw new DDCException("任务不存在或未启用，不能手动执行，任务编号：{%d}", taskId);
        }

        JobDataMap map = new JobDataMap();
        JobKey jobKey = buildJobKey(task);

        int category = task.getTaskCategoty();
        if (category == Constants.TASK_CATEGORY_BASIC || category == Constants.TASK_CATEGORY_WORKFLOW) {
            map.put("taskId", taskId);
        } else {
            logger.error("任务名称{}，不支持的任务大类{}", task.getTaskName(), task.getTaskCategoty());
            throw new DDCException("任务名称{%s}，不支持的任务大类{%d}", task.getTaskName(), task.getTaskCategoty());
        }
        map.put("triggerType", Constants.TASK_EXECUTE_TYPE_MANUAL);
        map.put("user", "admin");

        try {
            scheduler.triggerJob(jobKey, map);
        } catch (SchedulerException e) {
            logger.error("手动执行任务失败，任务名称{}，错误信息{}", task.getTaskName(), e);
            throw new DDCException("手动执行任务失败，任务名称{%s}，错误信息{%s}", task.getTaskName(), e.getMessage());
        }
        
        try{
        	DdcTaskUpdateHistory record = new DdcTaskUpdateHistory();
//        	String user = LoginContext.getLoginContext().getUserName();
            String user = "admin";
        	record.setTaskId(taskId);
        	record.setUpdateUser(user);
        	record.setUpdateTime(new Date());
        	record.setUpdateDetail(String.format("{%s}手动执行任务！", user));
        	taskUpdateMapper.insertSelective(record);
        } catch(Throwable e) {
        	logger.error("插入任务执行记录失败，错误信息：{}", e);
        }
    }

    @Override
    @DdcPermission
    public ServiceResult taskTable(int appId, DdcTask task, Pager page) {
        DdcTaskExample taskExample = new DdcTaskExample();
        DdcTaskExample.Criteria taskCriteria = taskExample.createCriteria();

        taskCriteria.andAppIdEqualTo(task.getAppId());
        if (task.getTaskId() != null) {
            taskCriteria.andTaskIdEqualTo(task.getTaskId());
        }
        if (task.getTaskName() != null) {
            taskCriteria.andTaskNameLike("%" + task.getTaskName() + "%");
        }
        if (task.getClassName() != null) {
            taskCriteria.andClassNameLike("%" + task.getClassName() + "%");
        }
        if (task.getTaskType() != null) {
            taskCriteria.andTaskTypeEqualTo(task.getTaskType());
        }
        taskExample.setPage(page);

        return new ServiceResult(page, taskMapper.selectByExample(taskExample), taskMapper.countByExample(taskExample));
    }

    @Override
    @Transactional
    @DdcPermission
    public void createOrUpdateTask(int appId, DdcTask task) throws DDCException {

        if (task.getTaskId() == null) {
            createTask(task);
        } else {
            updateTask(task);
        }
    }

    private void createTask(DdcTask task) throws DDCException {

        //任务大类
        int category = task.getTaskCategoty();
        //任务类型
        int type = task.getTaskType();

        Date now = new Date();
        task.setCreateTime(now);
//        task.setCreateUser(LoginContext.getLoginContext().getUserName());
        task.setCreateUser("admin");
        task.setUpdateTime(now);
        task.setUpdateUser("admin");

        DdcApplication application = appMapper.selectByPrimaryKey(task.getAppId());
        task.setAppKey(application.getAppKey());

        taskMapper.insertSelective(task); //插入新任务到数据库表DDC_TASK
        int taskId = task.getTaskId(); //数据库自增的id

        if (task.getTaskStatus() == Constants.TASK_STATUS_DISABLED || (category == Constants.TASK_CATEGORY_BASIC && type == Constants.TASK_TYPE_SUBTASK)) { //停用的任务、流程子任务
            return;
        }

        JobDataMap map = new JobDataMap();

        if (category == Constants.TASK_CATEGORY_BASIC || category == Constants.TASK_CATEGORY_WORKFLOW) {
            map.put("taskId", taskId);
        } else {
            logger.error("任务名称{}，不支持的任务大类{}", task.getTaskName(), task.getTaskCategoty());
            throw new DDCException("任务名称{%s}，不支持的任务大类{%d}", task.getTaskName(), task.getTaskCategoty());
        }

        JobKey jobKey = buildJobKey(task);
        JobDetail job = buildJob(task, jobKey, map);

        try {
            TriggerKey triggerKey = buildTriggerKey(task);
            Trigger trigger = buildTrigger(task, triggerKey);

            scheduleJob(job, jobKey, trigger, triggerKey);
        } catch (SchedulerException e) {
            logger.error("创建任务，名称{}，错误信息{}", task.getTaskName(), e);
            throw new DDCException("创建任务，名称{%s}，错误信息{%s}", task.getTaskName(), e);
        }
    }

    /**
     * 修改任务，先更新任务，再更新quartz
     */
    private void updateTask(DdcTask task) throws DDCException {

        int category = task.getTaskCategoty();
        int type = task.getTaskType();

        DdcTask currentTask = selectByTaskIdAndAppId(task.getAppId(), task.getTaskId()); //修改前的任务

        Date now = new Date();
        task.setUpdateTime(now);
        task.setUpdateUser("admin");
        taskMapper.updateByPrimaryKeySelective(task); //更新任务

        String updateDetails = generateChangeDetail(currentTask, task);
        if (StringUtils.isNotBlank(updateDetails)) {
            DdcTaskUpdateHistory record = new DdcTaskUpdateHistory();
            record.setTaskId(task.getTaskId());
            record.setUpdateUser("admin");
            record.setUpdateTime(now);
            record.setUpdateDetail(updateDetails);
            taskUpdateMapper.insertSelective(record);
        }

        JobDataMap map = new JobDataMap();
        map.put("taskId", task.getTaskId());

        JobKey jobKey = buildJobKey(task);
        JobDetail job = buildJob(task, jobKey, map);

        try {
            if ((category == Constants.TASK_CATEGORY_BASIC && (type == Constants.TASK_TYPE_CRON || type == Constants.TASK_TYPE_SIMPLE)) || category == Constants.TASK_CATEGORY_WORKFLOW) { //定时任务、简单任务或流程任务

                if (task.getTaskStatus() == Constants.TASK_STATUS_DISABLED) { //新状态为停用，则从quartz移除任务
                    scheduler.deleteJob(jobKey);
                } else if (needRescheduleJob(currentTask, task)) {
                	TriggerKey triggerKey = buildTriggerKey(task);
                	Trigger trigger = buildTrigger(task, triggerKey);

                	scheduleJob(job, jobKey, trigger, triggerKey);
                }
            }
        } catch (SchedulerException e) {
            logger.error("修改任务：{}，错误信息：{}", task.getTaskName(), e);
            throw new DDCException("创建任务，名称{%s}，错误信息{%s}", task.getTaskName(), e);
        }
    }
    
    /**
     * schedule 任务到Quartz
     * 若job存在且trigger存在，则rescheduleJob
     * 若job存在但trigger不存在，则删除job然后scheduleJob
     * 若job不存在，则直接scheduleJob
     * @param job
     * @param jobKey
     * @param trigger
     * @param triggerKey
     * @throws SchedulerException
     */
    private void scheduleJob(JobDetail job, JobKey jobKey, Trigger trigger, TriggerKey triggerKey) throws SchedulerException {
    	
    	boolean jobExist = scheduler.checkExists(jobKey);
    	boolean triggerExist = scheduler.checkExists(triggerKey);
    	if (jobExist) {
    		if(triggerExist) { //trigger存在，直接rescheduleJob
    			scheduler.addJob(job, true);
    			scheduler.rescheduleJob(triggerKey, trigger);
    		} else {
    			scheduler.deleteJob(jobKey);
    			scheduler.scheduleJob(job, trigger);
    		}
    	} else {
    		scheduler.scheduleJob(job, trigger);
    	}
    }
    
    private boolean needRescheduleJob(DdcTask oldTaks, DdcTask newTask) {
    	
    	if(newTask.getTaskStatus() == Constants.TASK_STATUS_DISABLED) {
    		return false;
    	}
    	
    	//1、任务状态从禁用变为启用
    	if(oldTaks.getTaskStatus() == Constants.TASK_STATUS_DISABLED && newTask.getTaskStatus() == Constants.TASK_STATUS_ENABLED) {
    		return true;
    	}

    	//2、并行运行机制发生变化
    	if(oldTaks.getConcurrency() != newTask.getConcurrency()) {
    		return true;
    	}
    	
    	//3、定时任务cron发生变化
    	if((oldTaks.getTaskType() == Constants.TASK_TYPE_CRON  || oldTaks.getTaskType() == Constants.TASK_TYPE_FLOW)&& ((oldTaks.getCron() == null && newTask.getCron() != null) || (oldTaks.getCron() != null && newTask.getCron() == null) || (oldTaks.getCron() != null && newTask.getCron() != null && !oldTaks.getCron().trim().equals(newTask.getCron().trim())))) {
    		return true;
    	}
    	
    	//4、简单任务发生变化
    	if(oldTaks.getTaskType() == Constants.TASK_TYPE_SIMPLE) {
    		if((oldTaks.getSimpleStartTime() == null && newTask.getSimpleStartTime() != null) || (oldTaks.getSimpleStartTime() != null && newTask.getSimpleStartTime() == null) || (oldTaks.getSimpleStartTime() != null && newTask.getSimpleStartTime() != null && !oldTaks.getSimpleStartTime().equals(newTask.getSimpleStartTime()))) {
    			return true;
    		}
    		
    		if((oldTaks.getSimpleEndTime() == null && newTask.getSimpleEndTime() != null) || (oldTaks.getSimpleEndTime() != null && newTask.getSimpleEndTime() == null) || (oldTaks.getSimpleEndTime() != null && newTask.getSimpleEndTime() != null && !oldTaks.getSimpleEndTime().equals(newTask.getSimpleEndTime()))) {
    			return true;
    		}
    		
    		if((oldTaks.getSimpleRepeatCount() == null && newTask.getSimpleRepeatCount() != null) || (oldTaks.getSimpleRepeatCount() != null && newTask.getSimpleRepeatCount() == null) || (oldTaks.getSimpleRepeatCount() != null && newTask.getSimpleRepeatCount() != null && oldTaks.getSimpleRepeatCount().intValue() != newTask.getSimpleRepeatCount().intValue())) {
    			return true;
    		}
    		
    		if((oldTaks.getSimpleRepeatInterval() == null && newTask.getSimpleRepeatInterval() != null) || (oldTaks.getSimpleRepeatInterval() != null && newTask.getSimpleRepeatInterval() == null) || (oldTaks.getSimpleRepeatInterval() != null && newTask.getSimpleRepeatInterval() != null && oldTaks.getSimpleRepeatInterval().intValue() != newTask.getSimpleRepeatInterval().intValue())) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * 构造任务修改详情
     *
     * @param oldTask
     * @param task
     * @return
     */
    private String generateChangeDetail(DdcTask oldTask, DdcTask task) {

        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(oldTask.getTaskName()) && StringUtils.isNotBlank(task.getTaskName()) && !oldTask.getTaskName().equals(task.getTaskName())) {
            sb.append(String.format("修改任务名称：{%s} -> {%s}", oldTask.getTaskName(), task.getTaskName())).append("\r\n");
        }
        if (StringUtils.isNotBlank(oldTask.getDescription()) && StringUtils.isNotBlank(task.getDescription()) && !oldTask.getDescription().equals(task.getDescription())) {
            sb.append(String.format("修改任务描述：{%s} -> {%s}", oldTask.getDescription(), task.getDescription())).append("\r\n");
        }
        if (StringUtils.isNotBlank(oldTask.getParameters()) && StringUtils.isNotBlank(task.getParameters()) && !oldTask.getParameters().equals(task.getParameters())) {
            sb.append(String.format("修改执行参数：{%s} -> {%s}", oldTask.getParameters(), task.getParameters())).append("\r\n");
        }
        if (oldTask.getTaskStatus() != null && task.getTaskStatus() != null && oldTask.getTaskStatus().intValue() != task.getTaskStatus().intValue()) {
            sb.append(String.format("修改任务状态：{%s} -> {%s}", oldTask.getTaskStatus() == 0 ? "禁用" : "启用", task.getTaskStatus() == 0 ? "禁用" : "启用")).append("\r\n");
        }
        if (oldTask.getTimeout() != null && task.getTimeout() != null && oldTask.getTimeout().longValue() != task.getTimeout().longValue()) {
            sb.append(String.format("修改超时时间：{%d} -> {%d}", oldTask.getTimeout(), task.getTimeout())).append("\r\n");
        }
        if (oldTask.getTimeoutRetry() != null && task.getTimeoutRetry() != null && oldTask.getTimeoutRetry() != task.getTimeoutRetry()) {
            sb.append(String.format("修改执行超时重试：{%s} -> {%s}", oldTask.getTimeoutRetry() == true ? "开启" : "关闭", task.getTimeoutRetry() == true ? "开启" : "关闭")).append("\r\n");
        }
        if (oldTask.getTimeoutRetryTimes() != null && task.getTimeoutRetryTimes() != null && oldTask.getTimeoutRetryTimes().intValue() != task.getTimeoutRetryTimes().intValue()) {
            sb.append(String.format("修改执行超时重试次数：{%d} -> {%d}", oldTask.getTimeoutRetryTimes(), task.getTimeoutRetryTimes())).append("\r\n");
        }
        if (oldTask.getFailedRetry() != null && task.getFailedRetry() != null && oldTask.getFailedRetry() != task.getFailedRetry()) {
            sb.append(String.format("修改执行失败重试：{%s} -> {%s}", oldTask.getFailedRetry() == true ? "开启" : "关闭", task.getFailedRetry() == true ? "开启" : "关闭")).append("\r\n");
        }
        if (oldTask.getFailedRetryTimes() != null && task.getFailedRetryTimes() != null && oldTask.getFailedRetryTimes().intValue() != task.getFailedRetryTimes().intValue()) {
            sb.append(String.format("修改执行失败重试次数：{%d} -> {%d}", oldTask.getFailedRetryTimes(), task.getFailedRetryTimes())).append("\r\n");
        }
        if (oldTask.getFailedRetryInterval() != null && task.getFailedRetryInterval() != null && oldTask.getFailedRetryInterval().longValue() != task.getFailedRetryInterval().longValue()) {
            sb.append(String.format("修改执行失败重试间隔：{%d} -> {%d}", oldTask.getFailedRetryInterval(), task.getFailedRetryInterval())).append("\r\n");
        }
        if (oldTask.getMutithread() != null && task.getMutithread() != null && oldTask.getMutithread() != task.getMutithread()) {
            sb.append(String.format("修改多线程运行：{%s} -> {%s}", oldTask.getMutithread() == true ? "开启" : "关闭", task.getMutithread() == true ? "开启" : "关闭")).append("\r\n");
        }
        if (oldTask.getThreads() != null && task.getThreads() != null && oldTask.getThreads().intValue() != task.getThreads().intValue()) {
            sb.append(String.format("修改线程数：{%d} -> {%d}", oldTask.getThreads(), task.getThreads())).append("\r\n");
        }
        if (oldTask.getConcurrency() != null && task.getConcurrency() != null && oldTask.getConcurrency() != task.getConcurrency()) {
            sb.append(String.format("修改并行运行：{%s} -> {%s}", oldTask.getConcurrency() == true ? "开启" : "关闭", task.getConcurrency() == true ? "开启" : "关闭")).append("\r\n");
        }
        if (StringUtils.isNotBlank(oldTask.getCron()) && StringUtils.isNotBlank(task.getCron()) && !oldTask.getCron().equals(task.getCron())) {
            sb.append(String.format("修改Cron脚本：{%s} -> {%s}", oldTask.getCron(), task.getCron())).append("\r\n");
        }
        //简单任务
        if ((oldTask.getSimpleStartTime() == null && task.getSimpleStartTime() != null) || (oldTask.getSimpleStartTime() != null && task.getSimpleStartTime() == null) || (oldTask.getSimpleStartTime() != null && task.getSimpleStartTime() != null && !oldTask.getSimpleStartTime().equals(task.getSimpleStartTime()))) {
            sb.append(String.format("修改执行开始时间：{%s} -> {%s}", oldTask.getSimpleStartTime(), task.getSimpleStartTime())).append("\r\n");
        }
        if ((oldTask.getSimpleEndTime() == null && task.getSimpleEndTime() != null) || (oldTask.getSimpleEndTime() != null && task.getSimpleEndTime() == null) || (oldTask.getSimpleEndTime() != null && task.getSimpleEndTime() != null && !oldTask.getSimpleEndTime().equals(task.getSimpleEndTime()))) {
            sb.append(String.format("修改执行结束时间：{%s} -> {%s}", oldTask.getSimpleEndTime(), task.getSimpleEndTime())).append("\r\n");
        }
        if (oldTask.getSimpleRepeatCount() != null && task.getSimpleRepeatCount() != null && oldTask.getSimpleRepeatCount().intValue() != task.getSimpleRepeatCount().intValue()) {
            sb.append(String.format("修改执行次数：{%d} -> {%d}", oldTask.getSimpleRepeatCount(), task.getSimpleRepeatCount())).append("\r\n");
        }
        if (oldTask.getSimpleRepeatInterval() != null && task.getSimpleRepeatInterval() != null && oldTask.getSimpleRepeatInterval().intValue() != task.getSimpleRepeatInterval().intValue()) {
            sb.append(String.format("修改执行间隔时间：{%d} -> {%d}", oldTask.getSimpleRepeatInterval(), task.getSimpleRepeatInterval())).append("\r\n");
        }
        return sb.toString();
    }

    @Override
    @DdcPermission
    public List<DdcTask> listSubTask(int appId) {
        DdcTaskExample taskExample = new DdcTaskExample();
        DdcTaskExample.Criteria taskCriteria = taskExample.createCriteria();
        taskCriteria.andAppIdEqualTo(appId);
        taskCriteria.andTaskTypeEqualTo(12);
        return taskMapper.selectByExample(taskExample);
    }

    /**
     * 构造JobKey
     *
     * @param task
     * @param executeBatchId
     * @return
     */
    private JobKey buildJobKey(DdcTask task) {

        JobKey jobKey = new JobKey(Constants.JOB_PREFIX + task.getTaskId(), task.getAppKey());
        return jobKey;
    }

    /**
     * 构造TriggerKey
     *
     * @param task
     * @param executeBatchId
     * @return
     */
    private TriggerKey buildTriggerKey(DdcTask task) {

        TriggerKey triggerKey = new TriggerKey(Constants.TRIGGER_PREFIX + task.getTaskId(), task.getAppKey());
        return triggerKey;
    }

    /**
     * 装配task
     *
     * @param task
     * @return
     */
    private JobDetail buildJob(DdcTask task, JobKey jobKey, JobDataMap map) {

        Class<? extends DmallTask> jobClass = task.getConcurrency() ? DmallTask.class : DmallTaskDisallowConcurrent.class;
        return newJob(jobClass).withIdentity(jobKey).storeDurably().requestRecovery().usingJobData(map).build();
    }

    /**
     * 为task配置触发器
     *
     * @param task
     * @return
     */
    @SuppressWarnings("rawtypes")
    private Trigger buildTrigger(DdcTask task, TriggerKey triggerKey) {

    	TriggerBuilder builder = null;
    	if(task.getTaskCategoty() == Constants.TASK_CATEGORY_BASIC && task.getTaskType() == Constants.TASK_TYPE_SIMPLE) { //简单任务
    		Date start = null;
    		Date end = null;
    		if(StringUtils.isNotBlank(task.getSimpleStartTime())) {
    			try {
					start = DateUtils.parseDate(task.getSimpleStartTime(), DateUtils.FORMAT_DATETIME);
				} catch (Exception e) {
				}    			
    		}
    		if(StringUtils.isNotBlank(task.getSimpleEndTime())) {
    			try {
					end = DateUtils.parseDate(task.getSimpleEndTime(), DateUtils.FORMAT_DATETIME);
				} catch (Exception e) {
				}    			
    		}
    		Integer repeatCount = task.getSimpleRepeatCount();
    		Integer repeatInterval = task.getSimpleRepeatInterval();
    		
    		SimpleScheduleBuilder ssb = simpleSchedule();
    		if(repeatCount != null && repeatCount.intValue() >= 0) {
    			ssb.withRepeatCount(repeatCount.intValue() - 1);
    		}
    		if(repeatInterval != null && repeatInterval.intValue() > 0) {
    			ssb.withIntervalInSeconds(repeatInterval.intValue());
    		}
    		builder = newTrigger().withIdentity(triggerKey).withSchedule(ssb);
    		if(start != null) {
    			builder.startAt(start);
    		}
    		if(end != null) {
    			builder.endAt(end);
    		}
    		
    	} else { //Cron任务
    		builder = newTrigger().withIdentity(triggerKey).withSchedule(cronSchedule(task.getCron()));
    	}
        return builder.build();
    }

    @Override
    @Transactional
    public void saveWorkFlow(String data) {
        JSONObject jsonObject = JSON.parseObject(data);

        Integer taskId = jsonObject.getInteger("taskId");

        DdcTaskWorkflowWithBLOBs taskWorkflowWithBLOBs = new DdcTaskWorkflowWithBLOBs();
        taskWorkflowWithBLOBs.setTaskId(taskId);
        taskWorkflowWithBLOBs.setJson(data);
        Date now = new Date();
        taskWorkflowWithBLOBs.setCreateTime(now);
//        taskWorkflowWithBLOBs.setCreateUser(LoginContext.getLoginContext().getUserName());
        taskWorkflowWithBLOBs.setCreateUser("admin");
        taskWorkflowWithBLOBs.setUpdateTime(now);
//        taskWorkflowWithBLOBs.setUpdateUser(LoginContext.getLoginContext().getUserName());
        taskWorkflowWithBLOBs.setUpdateUser("admin");

        Map<Integer, WorkflowTask> linkMap = new HashMap<>();
        List<WorkFlowNode> nodeList = JSONArray.parseArray(jsonObject.getString("nodeDataArray"), WorkFlowNode.class);
        if (!CollectionUtils.isEmpty(nodeList))
            for (WorkFlowNode node : nodeList) {
                WorkflowTask workflowTask = new WorkflowTask();
                workflowTask.setTaskId(node.getId());
                workflowTask.setChildren(new ArrayList<WorkflowTask>());
                linkMap.put(node.getId(), workflowTask);
            }
        List<WorkFlowLink> linkList = JSONArray.parseArray(jsonObject.getString("linkDataArray"), WorkFlowLink.class);
        if (!CollectionUtils.isEmpty(linkList)) {
            for (WorkFlowLink link : linkList) {
                linkMap.get(link.getFrom()).getChildren().add(linkMap.get(link.getTo()));
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (linkMap.get(-1) != null) {
                taskWorkflowWithBLOBs.setWorkflow(mapper.writeValueAsBytes(linkMap.get(-1)));
            } else {
                WorkflowTask workflowTask = new WorkflowTask();
                workflowTask.setTaskId(taskId);
                workflowTask.setChildren(new ArrayList<WorkflowTask>());
                taskWorkflowWithBLOBs.setWorkflow(mapper.writeValueAsBytes(workflowTask));
            }
        } catch (JsonProcessingException e) {
            logger.error("json转换出错", e);
        }
        taskWorkflowMapper.insertSelective(taskWorkflowWithBLOBs);

        DdcTask ddcTask = new DdcTask();
        ddcTask.setUpdateTime(now);
//        ddcTask.setUpdateUser(LoginContext.getLoginContext().getUserName());
        ddcTask.setUpdateUser("admin");
        ddcTask.setWorkflowId(taskWorkflowWithBLOBs.getWorkflowId());
        ddcTask.setTaskId(taskId);

        taskMapper.updateByPrimaryKeySelective(ddcTask);

        DdcTaskUpdateHistory record = new DdcTaskUpdateHistory();
        record.setTaskId(taskId);
//        record.setUpdateUser(LoginContext.getLoginContext().getUserName());
        record.setUpdateUser("admin");
        record.setUpdateTime(now);
        record.setUpdateDetail("修改子任务执行流程！");
        taskUpdateMapper.insertSelective(record);
    }

    @Override
    public Object getWorkFlow(int taskId) {
        DdcTask task = taskMapper.selectByPrimaryKey(taskId);
        if (task == null) {
            return null;
        }

        DdcTaskWorkflowWithBLOBs ddcTaskWorkflowWithBLOBs = taskWorkflowMapper.selectByPrimaryKey(task.getWorkflowId());
        if (ddcTaskWorkflowWithBLOBs != null && ddcTaskWorkflowWithBLOBs.getJson() != null) {
            return JSON.parse(ddcTaskWorkflowWithBLOBs.getJson());
        }
        return null;
    }

    @Override
    public Map<String, Integer> taskInfo(int appId) {
        Map<String, Integer> map = new HashMap<>();

        DdcTaskExample ddcTaskExample1 = new DdcTaskExample();
        DdcTaskExample.Criteria taskCriteria1 = ddcTaskExample1.createCriteria();
        taskCriteria1.andAppIdEqualTo(appId);
        taskCriteria1.andTaskStatusEqualTo(1);
        map.put("running", taskMapper.countByExample(ddcTaskExample1));

        DdcTaskExample ddcTaskExample2 = new DdcTaskExample();
        DdcTaskExample.Criteria taskCriteria2 = ddcTaskExample2.createCriteria();
        taskCriteria2.andAppIdEqualTo(appId);
        taskCriteria2.andTaskStatusEqualTo(0);
        map.put("stop", taskMapper.countByExample(ddcTaskExample2));


        return map;
    }

    @Override
    public ServiceResult taskUpdateHistoryTable(DdcTaskUpdateHistory history, Pager page) {
        DdcTaskUpdateHistoryExample historyExample = new DdcTaskUpdateHistoryExample();
        historyExample.setOrderByClause("UPDATE_TIME DESC");
        DdcTaskUpdateHistoryExample.Criteria historyCriteria = historyExample.createCriteria();
        historyCriteria.andTaskIdEqualTo(history.getTaskId());
        historyExample.setPage(page);
        return new ServiceResult(page, taskUpdateMapper.selectByExampleWithBLOBs(historyExample), taskUpdateMapper.countByExample(historyExample));
    }

}