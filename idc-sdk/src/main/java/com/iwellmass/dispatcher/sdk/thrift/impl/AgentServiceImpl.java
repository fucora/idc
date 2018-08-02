package com.iwellmass.dispatcher.sdk.thrift.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.iwellmass.dispatcher.sdk.base.StatusProcessor;
import com.iwellmass.dispatcher.sdk.model.ExecutingTaskInfo;
import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.dispatcher.sdk.thread.TaskExecuteThread;
import com.iwellmass.dispatcher.sdk.util.ApplicationContext;
import com.iwellmass.dispatcher.sdk.util.DuplicateExecuteChecker;
import com.iwellmass.dispatcher.sdk.util.ExecuteContext;
import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;
import com.iwellmass.dispatcher.thrift.model.CommandResult;
import com.iwellmass.dispatcher.thrift.model.ExecuteStatus;
import com.iwellmass.dispatcher.thrift.model.TaskEntity;
import com.iwellmass.dispatcher.thrift.model.TaskStatusInfo;
import com.iwellmass.dispatcher.thrift.sdk.AgentService;
import com.iwellmass.idc.lookup.SourceLookup;

/**
 * SDK端的thrift实现类，响应调度中心的任务执行请求
 * @author Ming.Li
 *
 */
public class AgentServiceImpl extends StatusProcessor implements AgentService.Iface {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AgentServiceImpl.class);
		
	//当前正在执行的任务列表
	private CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList;

	public AgentServiceImpl(CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList) {
		this.executingTaskList = executingTaskList;
	}

	@Override
	public CommandResult executeTask(TaskEntity taskEntity) throws TException {
			// 这是一个循环检测 
			if (taskEntity.getAppId() == 1234567) {
				return executeSourceLookup(taskEntity);
			}
		
		
			ITaskService service = ApplicationContext.getTaskMap().get(taskEntity.getClassName());
			if(service == null) {
				TaskStatusInfo status = generateTaskStatusInfo(taskEntity);
				ExecuteStatus es = new ExecuteStatus();
				es.setStatus(TaskStatus.BEAN_NOT_FOUND);
				es.setTime(System.currentTimeMillis());
				es.setMessage("任务执行类[" + taskEntity.getClassName() + "]不存在！");
				status.addToStatusList(es);
				sendTaskStatus(status);
				
				CommandResult rtn = new CommandResult(false, false);
				rtn.setMessage("任务执行类[" + taskEntity.getClassName() + "]不存在！");
				return rtn;
			}

			// 去重判断
			if(DuplicateExecuteChecker.checkExecuteId(taskEntity.getTaskId(), taskEntity.getExecuteId(), taskEntity.getDispatchCount())){
				CommandResult rtn = new CommandResult(false, false);
				rtn.setMessage(String.format("该任务已经被执行，不能继续派发，对应执行编号{%d}", taskEntity.getExecuteId()));				
			    return rtn;
			}
			
			ExecutingTaskInfo info = new ExecutingTaskInfo();
            info.setTaskEntity(taskEntity);
			info.setTaskService(service);

			ExecuteContext context = new ExecuteContext(taskEntity.getThreadCount(), taskEntity.getExecuteId(), taskEntity.getExecuteBatchId(), taskEntity.getFireTime(), taskEntity.getParameters());
			context.addExecuteStatus(TaskStatus.RECEIVED, "任务接收成功");
			info.setExecuteContext(context);

			ExecutorService threadPool = ApplicationContext.getExecutorService(taskEntity.getClassName(), taskEntity.getTaskId());
			TaskExecuteThread thread = new TaskExecuteThread(info, executingTaskList);
			try {
				threadPool.execute(thread);
				executingTaskList.add(info);
				return new CommandResult(true, false);
			} catch(RejectedExecutionException e) {
				//删除加入的executeId
				DuplicateExecuteChecker.removeExecuteId(taskEntity.getTaskId(), taskEntity.getExecuteId(), taskEntity.getDispatchCount());
				TaskStatusInfo status = generateTaskStatusInfo(taskEntity);
				ExecuteStatus es = new ExecuteStatus();
				es.setStatus(TaskStatus.REJECTED);
				es.setTime(System.currentTimeMillis());
				es.setMessage("执行队列已满，本次任务被拒绝");
				status.addToStatusList(es);
				sendTaskStatus(status);
				return new CommandResult(false, true);
			}
	}

	private CommandResult executeSourceLookup(TaskEntity taskEntity) {
		CommandResult result = new CommandResult();
		boolean flag = false;
		try {
			String className = taskEntity.getClassName();
			
			JSONObject jo = JSONObject.parseObject(taskEntity.getParameters());
			
			SourceLookup sourceLookup = ApplicationContext.getSourceLookupMap().get(className);
			flag = sourceLookup.lookup(jo.getInteger("jobId").toString(), LocalDateTime.parse(jo.getString("loadDate"), DateTimeFormatter.ISO_DATE_TIME));
		} catch (Throwable e) {
			LOGGER.debug("检测出错", e);
		}
		
		result.setSucceed(true);
		result.setMessage(String.valueOf(flag));
		return result;
	}

}
