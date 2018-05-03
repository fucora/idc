package com.iwellmass.dispatcher.server.thrift.impl;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.thrift.TException;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.iwellmass.dispatcher.common.constants.Constants;
import com.iwellmass.dispatcher.common.context.QuartzContext;
import com.iwellmass.dispatcher.common.dag.SchedulingEngine;
import com.iwellmass.dispatcher.common.dao.DdcApplicationMapper;
import com.iwellmass.dispatcher.common.dao.DdcNodeMapper;
import com.iwellmass.dispatcher.common.dao.DdcRunningTaskMapper;
import com.iwellmass.dispatcher.common.dao.DdcServerMapper;
import com.iwellmass.dispatcher.common.dao.DdcSubtaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteHistoryMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskExecuteStatusMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskMapper;
import com.iwellmass.dispatcher.common.dao.DdcTaskWorkflowMapper;
import com.iwellmass.dispatcher.common.entry.DDCException;
import com.iwellmass.dispatcher.common.entry.TaskInfoTuple;
import com.iwellmass.dispatcher.common.model.DdcApplication;
import com.iwellmass.dispatcher.common.model.DdcApplicationExample;
import com.iwellmass.dispatcher.common.model.DdcNode;
import com.iwellmass.dispatcher.common.model.DdcNodeExample;
import com.iwellmass.dispatcher.common.model.DdcRunningTask;
import com.iwellmass.dispatcher.common.model.DdcRunningTaskExample;
import com.iwellmass.dispatcher.common.model.DdcServer;
import com.iwellmass.dispatcher.common.model.DdcServerExample;
import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcSubtaskExecuteHistoryExample;
import com.iwellmass.dispatcher.common.model.DdcTask;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteHistory;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteHistoryExample;
import com.iwellmass.dispatcher.common.model.DdcTaskExecuteStatus;
import com.iwellmass.dispatcher.common.model.DdcTaskWorkflowWithBLOBs;
import com.iwellmass.dispatcher.common.task.DmallTask;
import com.iwellmass.dispatcher.common.utils.AlarmUtils;
import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;
import com.iwellmass.dispatcher.thrift.model.ExecuteStatus;
import com.iwellmass.dispatcher.thrift.model.ExecutorRegisterResult;
import com.iwellmass.dispatcher.thrift.model.HeartBeatInfo;
import com.iwellmass.dispatcher.thrift.model.NodeEnvInfo;
import com.iwellmass.dispatcher.thrift.model.NodeInfo;
import com.iwellmass.dispatcher.thrift.model.SendResult;
import com.iwellmass.dispatcher.thrift.model.ServerAddress;
import com.iwellmass.dispatcher.thrift.model.TaskEntity;
import com.iwellmass.dispatcher.thrift.model.TaskStatusInfo;
import com.iwellmass.dispatcher.thrift.server.StatusServerService;

public class StatusServerServiceImpl implements StatusServerService.Iface {
	
	@Autowired
	private DdcApplicationMapper appMapper;
	
	@Autowired
	private DdcNodeMapper nodeMapper;
	
	@Autowired
	private DdcTaskMapper taskMapper;
	
	@Autowired
	private DdcTaskWorkflowMapper taskWorkflowMapper;
	
	@Autowired
	private DataSourceTransactionManager txManager;
	
	@Autowired
	private DdcTaskExecuteStatusMapper executeStatusMapper;
	
	@Autowired
	private DdcTaskExecuteHistoryMapper historyMapper;
	
	@Autowired
	private DdcSubtaskExecuteHistoryMapper subHistoryMapper;
	
	@Autowired
	private DdcRunningTaskMapper runningTaskMapper;
	
	@Autowired
	private DdcServerMapper serverMapper;
	
	@Autowired
	private SchedulingEngine engine;
	
	private volatile boolean serverIsRunning = true;
	
	private final static Logger logger = LoggerFactory.getLogger(StatusServerServiceImpl.class);

	/**
	 *SDK注册，接入方应用启动时调用
	 */
	@Override
	public ExecutorRegisterResult registerExecutor(NodeInfo nodeInfo) throws TException {
		
		isServerRunning();
		
		ExecutorRegisterResult result = new ExecutorRegisterResult();
		try {
			DdcApplicationExample appExample = new DdcApplicationExample();
			DdcApplicationExample.Criteria appCriteria = appExample.createCriteria();
			appCriteria.andAppKeyEqualTo(nodeInfo.getAppKey()).andAppStatusEqualTo(Constants.ENABLED);
			List<DdcApplication> appList = appMapper.selectByExample(appExample);
			if(appList == null || appList.isEmpty()) {
				AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_ERRORKEY, 0, "AppKey:{" + nodeInfo.getAppKey() + "}对应的应用不存在，请确认该应用已经被正确创建！");
				result.setSucceed(false);
				result.setMessage("AppKey:{" + nodeInfo.getAppKey() + "}对应的应用不存在，请确认该应用已经被正确创建！");
				return result;
			}
			int appId = appList.get(0).getAppId();

			DdcNodeExample nodeExample = new DdcNodeExample();
			//appId，IP，Port确定唯一实例
			nodeExample.createCriteria().andAppIdEqualTo(appId).andNodeIpEqualTo(nodeInfo.getIp()).andNodePortEqualTo(nodeInfo.getPort());
			List<DdcNode> nodeList = nodeMapper.selectByExample(nodeExample);
			
			DdcNode node = new DdcNode();
			node.setNodeCode(nodeInfo.getNodeCode());
			node.setNodePath(nodeInfo.getPath());
			node.setClassNames(nodeInfo.getClassNames());
			node.setLastStartTime(new Date()); //未设置heartbeat时间，SDK有一个正常的heartbeat之后调度中心再对其进行任务派发
			node.setCpuCores(nodeInfo.getCoreSize());
			node.setOsName(nodeInfo.getOsName());
			node.setSdkVersion(nodeInfo.getVersion()); //更新SDK版本信息
			
			if(nodeList != null && !nodeList.isEmpty()) { //更新已有实例
				node.setId(nodeList.get(0).getId());
				nodeMapper.updateByPrimaryKeySelective(node);
			} else { //新注册实例
				node.setAppId(appId);
				node.setNodeIp(nodeInfo.getIp());
				node.setNodePort(nodeInfo.getPort());
				node.setNodeStatus(Constants.ENABLED); //启用
				nodeMapper.insertSelective(node);
			}
			
			result.setSucceed(true);
			result.setAppId(appId);			
		} catch (Throwable e) {
			logger.error("DDC-注册实例失败！应用编码：{}，IP：{}，端口：{}，失败原因：{}", nodeInfo.getAppKey(), nodeInfo.getIp(), nodeInfo.getPort(), e);
			String errorMsg = String.format("DDC-注册实例失败！应用编码：{%s}，IP：{%s}，端口：{%d}，失败原因：{%s}", nodeInfo.getAppKey(), nodeInfo.getIp(), nodeInfo.getPort(), e);
			result.setSucceed(false);
			result.setMessage(errorMsg);
		} finally {
		}
		
		return result;
	}

	/**
	 * 处理SDK发送的任务执行状态
	 */
	@Override
	public SendResult sendTaskStatus(TaskStatusInfo status) throws TException {
		
		isServerRunning();
		
		SendResult result = new SendResult();
		try {
			processStatus(status);
			result.setSucceed(true);
		} catch(DDCException e) {
			result.setSucceed(false);
			result.setMessage(e.getMessage());
		} finally {
		}
		return result;
	}

	/**
	 * 批量上报任务状态，目前只有应用停止,JVM_SHUTDOWN时若超时后还有任务未执行完成才会调用此服务
	 */
	@Override
	public SendResult sendTaskStatusList(List<TaskStatusInfo> status) throws TException {
		
		isServerRunning();
		
		SendResult result = new SendResult();
		for(TaskStatusInfo taskStatusInfo: status) {
			try {
				processStatus(taskStatusInfo);
			} catch(DDCException e) {
				result.setSucceed(false);
				result.setMessage(e.getMessage());
				return result;
			}
			
		}
		result.setSucceed(true);
		return result;
	}

	/**
	 * 处理SDK心跳
	 */
	@Override
	public SendResult sendHeartBeat(HeartBeatInfo heartBeatInfo) throws TException {

		isServerRunning();
		
		SendResult result = new SendResult();
		try {
			int appId = heartBeatInfo.getAppId();
			String ip = heartBeatInfo.getIp();
			int port = heartBeatInfo.getPort();
			String nodeCode = heartBeatInfo.getNodeCode();
			int taskCount = heartBeatInfo.getTaskCount();
			NodeEnvInfo envInfo = heartBeatInfo.getNodeEnvInfo();
			
			DdcNode record = new DdcNode();
			record.setTaskCount(taskCount);
			record.setThreads(envInfo.getTotalThread());
			record.setTotalMemoryMachine(envInfo.getTotalMemoryMachine());
			record.setFreeMemoryMachine(envInfo.getFreeMemoryMachine());
			record.setTotalMemoryProcess(envInfo.getTotalMemoryProcess());
			record.setFreeMemoryProcess(envInfo.getFreeMemoryProcess());
			record.setCpuRatioMachine(envInfo.getCpuRatioMachine());
			record.setCpuRatioProcess(envInfo.getCpuRatioProcess());
			record.setLastHbTime(new Date());
			
			DdcNodeExample example = new DdcNodeExample();
			DdcNodeExample.Criteria criteria = example.createCriteria();
			criteria.andAppIdEqualTo(appId)
			.andNodeIpEqualTo(ip)
			.andNodePortEqualTo(port)
			.andNodeCodeEqualTo(nodeCode);
			nodeMapper.updateByExampleSelective(record, example);

			result.setSucceed(true);
		} catch(Throwable e) {
			logger.error("DDC-处理心跳数据失败！应用编号：{}，IP：{}，端口：{}，失败原因：{}", heartBeatInfo.getAppId(), heartBeatInfo.getIp(), heartBeatInfo.getPort(), e);
			String errorMsg = String.format("DDC-处理心跳数据失败！应用编号：{%d}，IP：{%s}，端口：{%d}，失败原因：{%s}", heartBeatInfo.getAppId(), heartBeatInfo.getIp(), heartBeatInfo.getPort(), e);
			result.setSucceed(false);
			result.setMessage(errorMsg);
		} finally {
		}

		return result;
	}
	
	/**
	 *  普通定时任务                                             				流程子任务
	 *     	 |														 				|
	 * 成功——————失败									成功——————————————————————————失败
	 *   |               |									 |						 								|
	 * 完成任务       完成任务 						   获取下一步任务											需要重试
	 *                   | 									 |														|
	 *                 需要重试						有———————————无								是——————————否
	 *                	 |							|				   		|								|					  |
	 *                   是					Schedule下一步任务       所有子任务都完成				Schedule新的任务			结束流程任务
	 *                   |   												|
	 *         	  Schedule新的任务   								是————————否
	 * 																|				  |
	 * 															完成流程任务		不做操作	
	 * 
	 * 状态处理公共方法
	 */
	private void processStatus(TaskStatusInfo status) throws DDCException {

		List<ExecuteStatus> statusList = status.getStatusList();
		if(statusList == null || statusList.isEmpty()) {
			return;
		}
		
		TaskEntity taskEntity = status.getTaskEntity();
		int taskId = taskEntity.getTaskId();
		long executeId = taskEntity.getExecuteId();
		long workflowExecuteId = taskEntity.getWorkflowExecuteId(); // optional
		String executeBatchId = taskEntity.getExecuteBatchId(); // required
		int workflowId = taskEntity.getWorkflowId(); // optional
		boolean needRetry = taskEntity.isNeedRetry(); // required
		int retryTimes = taskEntity.getRetryTimes(); // required
		int currentRetryTime = taskEntity.getCurrentRetryTime(); // required
		long retryInterval = taskEntity.getRetryInterval(); // required
		
		DefaultTransactionDefinition def = new DefaultTransactionDefinition();
		def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
		TransactionStatus ts = txManager.getTransaction(def);
		try {
			
			Scheduler scheduler = QuartzContext.getScheduler();
			for(ExecuteStatus each : statusList) {
				Date statusTime = new Date(each.getTime());
				//记录执行状态
				DdcTaskExecuteStatus dtes = new DdcTaskExecuteStatus();
				dtes.setTaskId(taskId);
				dtes.setExecuteId(executeId);
				dtes.setWorkflowExecuteId(workflowExecuteId);
				dtes.setWorkflowId(workflowId);
				dtes.setExecuteBatchId(executeBatchId);
				dtes.setStatus(each.getStatus());
				dtes.setMessage(each.getMessage());
				dtes.setTimestamp(statusTime);
				executeStatusMapper.insertSelective(dtes);
				
				if(each.getStatus().equals(TaskStatus.STARTED)) { 
					DdcTask task = new DdcTask();
					task.setTaskId(taskId);
					task.setLastExecuteStartTime(statusTime);
					taskMapper.updateByPrimaryKeySelective(task);
				} else if(each.getStatus().equals(TaskStatus.SUCCEED) || each.getStatus().equals(TaskStatus.FAILED)) {
					DdcTask task = new DdcTask();
					task.setTaskId(taskId);
					task.setLastExecuteEndTime(statusTime);
					task.setLastExecuteStatus(each.getStatus());
					task.setLastExecuteMessage(each.getMessage());
					task.setLastExecuteIp(status.getIp());
					task.setLastExecutePort(status.getPort());
					task.setLastExecuteNodeCode(status.getNodeCode());
					taskMapper.updateByPrimaryKeySelective(task);
				}
				
				if(workflowId == 0 && workflowExecuteId == 0) { //非流程子任务
					DdcRunningTaskExample example = null;
					if(isSystemStatus(each.getStatus())) {
						example = new DdcRunningTaskExample();
						example.createCriteria().andTaskIdEqualTo(taskId).andExecuteIdEqualTo(executeId).andExecuteBatchIdEqualTo(executeBatchId);
					}
					
					if(each.getStatus().equals(TaskStatus.RECEIVED)) {
						DdcTaskExecuteHistory executeRecord = new DdcTaskExecuteHistory();
						executeRecord.setExecuteId(executeId);
						executeRecord.setReceiveTime(statusTime);
						executeRecord.setIp(status.getIp());
						executeRecord.setPort(status.getPort());
						executeRecord.setNodeCode(status.getNodeCode());
						historyMapper.updateByPrimaryKeySelective(executeRecord);
						
						DdcRunningTask runningTaskRecord = new DdcRunningTask();
						runningTaskRecord.setReceiveTime(statusTime);
						runningTaskMapper.updateByExampleSelective(runningTaskRecord, example);
					} else if(each.getStatus().equals(TaskStatus.STARTED)) {
						DdcTaskExecuteHistory executeRecord = new DdcTaskExecuteHistory();
						executeRecord.setExecuteId(executeId);
						executeRecord.setStartTime(statusTime);
						historyMapper.updateByPrimaryKeySelective(executeRecord);

						DdcRunningTask runningTaskRecord = new DdcRunningTask();
						runningTaskRecord.setStartTime(statusTime);
						runningTaskMapper.updateByExampleSelective(runningTaskRecord, example);
					} else if(each.getStatus().equals(TaskStatus.SUCCEED)) { //执行结束
						DdcTaskExecuteHistory executeRecord = new DdcTaskExecuteHistory();
						executeRecord.setExecuteId(executeId);
						executeRecord.setCompleteTime(statusTime);
						executeRecord.setExecuteResult(each.getStatus());
						executeRecord.setExecuteResultDesc(each.getMessage());
						historyMapper.updateByPrimaryKeySelective(executeRecord);

						runningTaskMapper.deleteByExample(example);
					} else if(each.getStatus().equals(TaskStatus.FAILED)) {
						runningTaskMapper.deleteByExample(example);
						
						//根据条件更新
						DdcTaskExecuteHistoryExample historyExample = new DdcTaskExecuteHistoryExample();
						DdcTaskExecuteHistoryExample.Criteria criteria = historyExample.createCriteria();
						criteria.andExecuteIdEqualTo(executeId).andCompleteTimeIsNull();
						
						DdcTaskExecuteHistory executeRecord = new DdcTaskExecuteHistory();
						executeRecord.setCompleteTime(statusTime);
						executeRecord.setExecuteResult(each.getStatus());
						executeRecord.setExecuteResultDesc(each.getMessage());
						int updateCount = historyMapper.updateByExampleSelective(executeRecord, historyExample);
						
						//有数据更新才执行重试，防止因为某状态处理失败SDK重新发送状态引起的重复调用问题
						if(updateCount > 0) { //执行失败且需要重试
							if(needRetry) {
								if(currentRetryTime < retryTimes) {
									DdcTask task = taskMapper.selectByPrimaryKey(taskId);
									if(task == null) {
										logger.error("任务编号：{}对应的任务不存在！", taskId);
										throw new DDCException(String.format("Schedule流程子任务重试任务失败！任务编号：{%d}对应的任务不存在！", taskId));
									}
									
									JobDataMap map = new JobDataMap();
									map.put("taskId", taskId);
									map.put("executeBatchId", executeBatchId);
									map.put("currentRetryTime", currentRetryTime + 1);
									
									try {
										JobKey jobKey = buildJobKey(task, executeId, each.getTime());
										
										//判断任务是否已经在quartz存在，如果存在就不派发，job的key里包含了状态上报的时间，所以状态因为失败重复上报时jobKey不会变
										//主要是因为Quartz的操作不在当前事务里面，防止quartz操作成功而后续任务操作失败导致的状态重复上报造成的任务重复派发问题
										if(!scheduler.checkExists(jobKey)) {
											TriggerKey triggerKey = buildTriggerKey(task, executeId, each.getTime());
											
											JobDetail job = buildJob(task, jobKey, map);
											Trigger trigger = buildTrigger(task, triggerKey, retryInterval);
											
											scheduler.scheduleJob(job, trigger);
											logger.info("任务执行失败进行重试，任务编号：{}，执行编号：{}，执行批次编号：{}，最大重试次数：{}，当前重试次数：{}", taskId, executeId, executeBatchId, retryTimes, currentRetryTime + 1);
										}								
									} catch(SchedulerException e) {
										logger.error("Schedule重试任务失败！任务编号：{}，执行编号：{}，执行批次编号：{}，错误信息：{}", taskId, executeId, executeBatchId, e);
										String errorMsg = String.format("Schedule重试任务失败！任务编号：{%d}，执行编号：{%d}，执行批次编号：{%s}，错误信息：{%s}", taskId, executeId, executeBatchId, e);
										throw new DDCException(errorMsg, e);
									}
								} else {
									DdcTask ddcTask = taskMapper.selectByPrimaryKey(taskId);									
									AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_OVERERROR, taskId, String.format("任务{%s}执行失败，超过最大失败重试次数：{%d}", (ddcTask != null ? ddcTask.getTaskName() : taskId), retryTimes));
								}								
							} else {
								DdcTask ddcTask = taskMapper.selectByPrimaryKey(taskId);
								AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_OVERERROR, taskId, String.format("任务{%s}执行失败，该任务未开启失败重试机制，将不进行重试，等待下次执行！", (ddcTask != null ? ddcTask.getTaskName() : taskId)));
							}
						}
					}
				} else { //流程子任务
					DdcRunningTaskExample example = null;
					if(isSystemStatus(each.getStatus())) {
						example = new DdcRunningTaskExample();
						example.createCriteria().andTaskIdEqualTo(taskId).andExecuteIdEqualTo(executeId).andExecuteBatchIdEqualTo(executeBatchId).andWorkflowExecuteIdEqualTo(workflowExecuteId);
					}
					if(each.getStatus().equals(TaskStatus.RECEIVED)) {
						DdcSubtaskExecuteHistory executeRecord = new DdcSubtaskExecuteHistory();
						executeRecord.setExecuteId(executeId);
						executeRecord.setReceiveTime(statusTime);
						executeRecord.setIp(status.getIp());
						executeRecord.setPort(status.getPort());
						executeRecord.setNodeCode(status.getNodeCode());
						subHistoryMapper.updateByPrimaryKeySelective(executeRecord);
						
						//如果是第一个子任务则更新流程主任务的开始时间
						List<Integer> firstTaskList = engine.findStartTaskIds(workflowId);
						if(firstTaskList.contains(taskId)) { 
							DdcTaskExecuteHistoryExample historyExample = new DdcTaskExecuteHistoryExample();
							DdcTaskExecuteHistoryExample.Criteria criteria = historyExample.createCriteria();
							criteria.andExecuteIdEqualTo(workflowExecuteId).andReceiveTimeIsNull();
							
							DdcTaskExecuteHistory workflowRecord = new DdcTaskExecuteHistory();
							workflowRecord.setReceiveTime(statusTime);
							historyMapper.updateByExampleSelective(workflowRecord, historyExample);
						}
						
						//更新子任务的正在执行记录表
						DdcRunningTask runningTaskRecord = new DdcRunningTask();
						runningTaskRecord.setReceiveTime(statusTime);
						runningTaskMapper.updateByExampleSelective(runningTaskRecord, example);
					} else if(each.getStatus().equals(TaskStatus.STARTED)) {
						DdcSubtaskExecuteHistory executeRecord = new DdcSubtaskExecuteHistory();
						executeRecord.setExecuteId(executeId);
						executeRecord.setStartTime(statusTime);
						subHistoryMapper.updateByPrimaryKeySelective(executeRecord);
						
						//如果是第一个子任务则更新流程主任务的开始时间
						List<Integer> firstTaskList = engine.findStartTaskIds(workflowId);
						if(firstTaskList.contains(taskId)) { 
							DdcTaskExecuteHistoryExample historyExample = new DdcTaskExecuteHistoryExample();
							DdcTaskExecuteHistoryExample.Criteria criteria = historyExample.createCriteria();
							criteria.andExecuteIdEqualTo(workflowExecuteId).andStartTimeIsNull();
							
							DdcTaskExecuteHistory workflowRecord = new DdcTaskExecuteHistory();
							workflowRecord.setStartTime(statusTime);
							int count = historyMapper.updateByExampleSelective(workflowRecord, historyExample);
							if(count > 0) { //有实际的数据更新
								//从workflow表获取流程对应的流程任务编号
								DdcTaskWorkflowWithBLOBs work = taskWorkflowMapper.selectByPrimaryKey(workflowId);
								int workflowTaskId = work.getTaskId();
								if(workflowTaskId > 0) {
									DdcTask workflowTask = new DdcTask();
									workflowTask.setTaskId(workflowTaskId);
									workflowTask.setLastExecuteStartTime(statusTime);
									taskMapper.updateByPrimaryKeySelective(workflowTask); //更新流程任务最近一次执行的开始时间
								}
							}
						}
						
						//更新子任务的正在执行记录表
						DdcRunningTask runningTaskRecord = new DdcRunningTask();
						runningTaskRecord.setStartTime(statusTime);
						runningTaskMapper.updateByExampleSelective(runningTaskRecord, example);
					} else if(each.getStatus().equals(TaskStatus.SUCCEED)) {
						//删除子任务的正在执行记录表
						runningTaskMapper.deleteByExample(example);
						
						DdcSubtaskExecuteHistoryExample subHistoryExample = new DdcSubtaskExecuteHistoryExample();
						DdcSubtaskExecuteHistoryExample.Criteria criteria = subHistoryExample.createCriteria();
						criteria.andExecuteIdEqualTo(executeId).andCompleteTimeIsNull();
						
						DdcSubtaskExecuteHistory subExecuteRecord = new DdcSubtaskExecuteHistory();
						subExecuteRecord.setCompleteTime(statusTime);
						subExecuteRecord.setExecuteResult(each.getStatus());
						subExecuteRecord.setExecuteResultDesc(each.getMessage());
						
						//提交当前任务状态并获取下一步的子任务
						List<Integer> nextStepTaskIds = new ArrayList<Integer>();
						
						//获取流程主任务的行级锁，状态处理完释放锁，执行成功状态一般在状态列表的最后，所以锁的时间不会很长
						//SELECT * FROM DDC_TASK_EXECUTE_HISTORY WHERE EXECUTE_ID = #{executeId,jdbcType=BIGINT} FOR UPDATE
						historyMapper.acquireWorkflowExecuteLock(workflowExecuteId);
						
						int updateCount = subHistoryMapper.updateByExampleSelective(subExecuteRecord, subHistoryExample);
						
						if(updateCount == 0) {
							continue;
						}

						nextStepTaskIds = engine.findSubsequentTasks(new TaskInfoTuple(workflowExecuteId, taskId, executeBatchId, workflowId));
						
						if(nextStepTaskIds != null) { 
							if(nextStepTaskIds.size() == 1 && nextStepTaskIds.contains(Constants.WORKFLOW_END_TASK_ID)) { //所有流程任务已经完成
								DdcTaskExecuteHistory workflowRecord = new DdcTaskExecuteHistory();
								workflowRecord.setExecuteId(workflowExecuteId);
								workflowRecord.setCompleteTime(statusTime);
								workflowRecord.setExecuteResult(TaskStatus.SUCCEED);
								workflowRecord.setExecuteResultDesc(each.getMessage());
								historyMapper.updateByPrimaryKeySelective(workflowRecord);
								
								DdcTaskWorkflowWithBLOBs work = taskWorkflowMapper.selectByPrimaryKey(workflowId);
								int workflowTaskId = work.getTaskId();
								if(workflowTaskId > 0) {
									DdcTask workflowTask = new DdcTask();
									workflowTask.setTaskId(workflowTaskId);
									workflowTask.setLastExecuteEndTime(statusTime);
									workflowTask.setLastExecuteStatus(each.getStatus());
									workflowTask.setLastExecuteMessage(each.getMessage());
									workflowTask.setLastExecuteIp(status.getIp());
									workflowTask.setLastExecutePort(status.getPort());
									workflowTask.setLastExecuteNodeCode(status.getNodeCode());
									taskMapper.updateByPrimaryKeySelective(workflowTask); //更新流程主任务的最近执行记录
								}
								//删除关联的流程任务
								DdcRunningTaskExample runningExample = new DdcRunningTaskExample();
								runningExample.createCriteria().andTaskIdEqualTo(workflowTaskId).andExecuteIdEqualTo(workflowExecuteId).andExecuteBatchIdEqualTo(executeBatchId).andWorkflowIdLessThan(0);
								runningTaskMapper.deleteByExample(runningExample);
							} else {
								for(int nextStepTaskId : nextStepTaskIds) { //有下一步任务
									
									//去重判断，如果已经执行则不再发起新的任务，防止状态因为推送失败而重复推送的问题
									DdcSubtaskExecuteHistoryExample subExample = new DdcSubtaskExecuteHistoryExample();
									DdcSubtaskExecuteHistoryExample.Criteria subCriteria = subExample.createCriteria();
									subCriteria.andTaskIdEqualTo(nextStepTaskId)
									.andExecuteBatchIdEqualTo(executeBatchId)
									.andWorkflowExecuteIdEqualTo(workflowExecuteId);

									int exists = subHistoryMapper.countByExample(subExample);
									if(exists > 0) {
										continue;
									}
									
									//下一步被执行的任务
									DdcTask nextTask = taskMapper.selectByPrimaryKey(nextStepTaskId);
									if(nextTask == null) {
										logger.error("任务编号：{}对应的任务不存在！", nextStepTaskId);
										throw new DDCException(String.format("Schedule流程子任务重试任务失败！任务编号：{%d}对应的任务不存在！", nextStepTaskId));
									}
									JobDataMap map = new JobDataMap();
									map.put("taskId", nextStepTaskId);
									map.put("executeBatchId", executeBatchId);
									map.put("workflowExecuteId", workflowExecuteId);
									map.put("workflowId", workflowId);
									
									try {
										JobKey jobKey = buildJobKey(nextTask, executeId, each.getTime());
										
										//判断任务是否已经在quartz存在，如果存在就不派发，job的key里包含了状态上报的时间，所以状态因为失败重复上报时jobKey不会变
										//主要是因为Quartz的操作不在当前事务里面，防止quartz操作成功而后续任务操作失败导致的状态重复上报造成的任务重复派发问题
										if(!scheduler.checkExists(jobKey)) {
											TriggerKey triggerKey = buildTriggerKey(nextTask, executeId, each.getTime());
											
											JobDetail job = buildJob(nextTask, jobKey, map);
											Trigger trigger = buildTrigger(nextTask, triggerKey, nextTask.getWaitTime());
											
											scheduler.scheduleJob(job, trigger);
										}
									} catch(SchedulerException e) {
										logger.error("Schedule流程子任务失败！当前任务编号：{}，下一步任务编号{}，执行编号：{}，执行批次编号：{}，错误信息：{}", taskId, nextStepTaskId, executeId, executeBatchId, e);
										String errorMsg = String.format("Schedule重试任务失败！当前任务编号：{%d}，下一步任务编号{%d}，执行编号：{%d}，执行批次编号：{%s}，错误信息：{%s}", taskId, nextStepTaskId, executeId, executeBatchId, e);
										throw new DDCException(errorMsg, e);
									}
								}
							}
						}
					} else if(each.getStatus().equals(TaskStatus.FAILED)) {
						//删除子任务的正在执行记录表
						runningTaskMapper.deleteByExample(example);
						
						DdcSubtaskExecuteHistoryExample subHistoryExample = new DdcSubtaskExecuteHistoryExample();
						DdcSubtaskExecuteHistoryExample.Criteria criteria = subHistoryExample.createCriteria();
						criteria.andExecuteIdEqualTo(executeId).andCompleteTimeIsNull();
						
						DdcSubtaskExecuteHistory subExecuteRecord = new DdcSubtaskExecuteHistory();
						subExecuteRecord.setCompleteTime(statusTime);
						subExecuteRecord.setExecuteResult(each.getStatus());
						subExecuteRecord.setExecuteResultDesc(each.getMessage());
						int updateCount = subHistoryMapper.updateByExampleSelective(subExecuteRecord, subHistoryExample);
						
						if(updateCount == 0) {
							continue;
						}
						
						//判断主任务是否已经失败
						boolean workflowTaskFailed = false;
						if(needRetry) {
							DdcTaskExecuteHistoryExample workflowExample = new DdcTaskExecuteHistoryExample();
							workflowExample.createCriteria().andExecuteIdEqualTo(workflowExecuteId).andCompleteTimeIsNotNull();
							workflowTaskFailed = historyMapper.countByExample(workflowExample) > 0;
						}
						
						if(needRetry && currentRetryTime < retryTimes && !workflowTaskFailed) { //执行失败且需要重试
							DdcTask task = taskMapper.selectByPrimaryKey(taskId);
							if(task == null) {
								logger.error("任务编号：{}对应的任务不存在！", taskId);
								throw new DDCException(String.format("Schedule流程子任务重试任务失败！任务编号：{%d}对应的任务不存在！", taskId));
							}
							JobDataMap map = new JobDataMap();
							map.put("taskId", taskId);
							map.put("executeBatchId", executeBatchId);
							map.put("workflowExecuteId", workflowExecuteId);
							map.put("workflowId", workflowId);
							map.put("currentRetryTime", currentRetryTime + 1);
							
							try {
								JobKey jobKey = buildJobKey(task, executeId, each.getTime());
																
								if(!scheduler.checkExists(jobKey)) { //不存在才创建任务
									TriggerKey triggerKey = buildTriggerKey(task, executeId, each.getTime());
									
									JobDetail job = buildJob(task, jobKey, map);
									Trigger trigger = buildTrigger(task, triggerKey, retryInterval);
									
									scheduler.scheduleJob(job, trigger);
								}
							} catch(SchedulerException e) {
								logger.error("Schedule流程子任务重试任务失败！任务编号：{}，执行编号：{}，执行批次编号：{}，错误信息：{}", taskId, executeId, executeBatchId, e);
								String errorMsg = String.format("Schedule流程子任务重试任务失败！任务编号：{%d}，执行编号：{%d}，执行批次编号：{%s}，错误信息：{%s}", taskId, executeId, executeBatchId, e);
								throw new DDCException(errorMsg, e);
							}
						} else { //结束流程任务，执行结果为失败
							DdcTask ddcTask = taskMapper.selectByPrimaryKey(taskId);
							if(ddcTask == null) {
								logger.error("任务编号：{}对应的任务不存在！", taskId);
								throw new DDCException(String.format("Schedule流程子任务重试任务失败！任务编号：{%d}对应的任务不存在！", taskId));
							}
							DdcTaskExecuteHistory workflowRecord = new DdcTaskExecuteHistory();
							workflowRecord.setExecuteId(workflowExecuteId);
							workflowRecord.setCompleteTime(statusTime);
							workflowRecord.setExecuteResult(TaskStatus.FAILED);							
							workflowRecord.setExecuteResultDesc(String.format("子任务{%s}执行失败，流程任务失败，子任务错误信息：{%s}", ddcTask.getTaskName(), each.getMessage()));
							historyMapper.updateByPrimaryKeySelective(workflowRecord);
							
							DdcTaskWorkflowWithBLOBs work = taskWorkflowMapper.selectByPrimaryKey(workflowId);
							int workflowTaskId = work.getTaskId();
							if(workflowTaskId > 0) {
								DdcTask workflowTask = new DdcTask();
								workflowTask.setTaskId(workflowTaskId);
								workflowTask.setLastExecuteEndTime(statusTime);
								workflowTask.setLastExecuteStatus(each.getStatus());
								workflowTask.setLastExecuteMessage(String.format("子任务{%s}执行失败，流程任务失败，子任务错误信息：{%s}", ddcTask.getTaskName(), each.getMessage()));
								workflowTask.setLastExecuteIp(status.getIp());
								workflowTask.setLastExecutePort(status.getPort());
								workflowTask.setLastExecuteNodeCode(status.getNodeCode());
								taskMapper.updateByPrimaryKeySelective(workflowTask); //更新流程主任务的最近执行记录								
							}
							
							AlarmUtils.sendAndRecordAlarm(Constants.ALARM_KEY_OVERERROR, taskId, String.format("流程子任务{%s}执行失败，未设置失败重试或者超过最大失败重试次数：{%d}，流程任务将被结束，执行结果为失败！", (ddcTask != null ? ddcTask.getTaskName() : taskId), retryTimes));
							//删除关联的流程任务
							DdcRunningTaskExample runningExample = new DdcRunningTaskExample();
							runningExample.createCriteria().andTaskIdEqualTo(workflowTaskId).andExecuteIdEqualTo(workflowExecuteId).andExecuteBatchIdEqualTo(executeBatchId).andWorkflowIdLessThan(0);
							runningTaskMapper.deleteByExample(runningExample);
						}
					}
				}
			}
			
			txManager.commit(ts);
		} catch (Throwable e) {
			txManager.rollback(ts);
			logger.error("处理任务执行状态失败！任务编号：{}，执行编号：{}，执行批次编号：{}，错误信息：{}", taskId, executeId, executeBatchId, e);
			throw new DDCException("处理任务执行状态失败！任务编号：{%d}，执行编号：{%d}，执行批次编号：{%s}，错误信息：{%s}", taskId, executeId, executeBatchId, e.getMessage());
		} finally {
		}
	}
	
	/**
	 * 构造JobKey
	 * @param task
	 * @param executeBatchId
	 * @return
	 */
	private JobKey buildJobKey(DdcTask task, long executeId, long statusTime) {

    	JobKey jobKey = new JobKey(Constants.JOB_PREFIX + task.getTaskId() + "_" + executeId + "_" + statusTime, task.getAppKey());
		return jobKey;
	}
	
	/**
	 * 构造TriggerKey
	 * @param task
	 * @param executeBatchId
	 * @return
	 */
	private TriggerKey buildTriggerKey(DdcTask task, long executeId, long statusTime) {

    	TriggerKey triggerKey = new TriggerKey(Constants.TRIGGER_PREFIX + task.getTaskId() + "_" + executeId + "_" + statusTime, task.getAppKey());
		return triggerKey;
	}
	
	/**
     * 装配task
     *
     * @param task
     * @return
     */
    private JobDetail buildJob(DdcTask task, JobKey jobKey, JobDataMap map) {

        return newJob(DmallTask.class)
                .withIdentity(jobKey)
                .storeDurably(false).requestRecovery().usingJobData(map)
                .build();
    }
    
    /**
     * 为task配置触发器
     *
     * @param task
     * @return
     */
    @SuppressWarnings("rawtypes")
	private Trigger buildTrigger(DdcTask task, TriggerKey triggerKey, long interval) {

        TriggerBuilder builder = newTrigger().withIdentity(triggerKey).withSchedule(simpleSchedule().withRepeatCount(0)); 
        if(interval > 0) {
        	Date now = new Date();
        	now.setTime(now.getTime() + interval * 1000);
        	builder = builder.startAt(now);
        } else {
        	builder = builder.startNow();
        }
        return builder.build();
    }

    /**
     * SDK获取状态服务器地址列表服务，走Thrift通过域名访问
     */
	@Override
	public List<ServerAddress> queryServerAddress() throws TException {

		isServerRunning();
		
		List<ServerAddress> serverList = new ArrayList<ServerAddress>();
		Date threeHbAgo = new Date();
		threeHbAgo.setTime(System.currentTimeMillis() - Constants.THREE_SERVER_HEARTBEAT);
		DdcServerExample example = new DdcServerExample();
		DdcServerExample.Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo(1).andLastHbTimeGreaterThan(threeHbAgo);
		
		List<DdcServer> list = serverMapper.selectByExample(example);
		if(list != null && !list.isEmpty()) {
			for(DdcServer server : list) {
				ServerAddress s = new ServerAddress();
				s.setIp(server.getIp());
				s.setPort(server.getPort());
				serverList.add(s);
			}
		}
		return serverList;
	}

	private void isServerRunning() throws TException {
		if(!serverIsRunning) {
			throw new TException("调度服务器停止中，不再接收新的请求...");
		}
	}
	
	/**
	 * 标记服务器开始停止
	 * @param serverShutdown
	 */
	public void shutdown() {
		this.serverIsRunning = false;
	}
	
	/**
	 * 判断状态是否为系统定义状态
	 * @param status
	 * @return
	 */
	private boolean isSystemStatus(String status) {
		if(StringUtils.isBlank(status)) {
			return false;
		}
		
		if(status.equals(TaskStatus.RECEIVED) || status.equals(TaskStatus.STARTED) || status.equals(TaskStatus.SUCCEED) || status.equals(TaskStatus.FAILED)) {
			return true;
		}
		
		return false;
	}

}
