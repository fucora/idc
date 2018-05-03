package com.iwellmass.dispatcher.sdk.thrift.impl;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import org.apache.thrift.TException;

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

/**
 * SDK端的thrift实现类，响应调度中心的任务执行请求
 * @author Ming.Li
 *
 */
public class AgentServiceImpl extends StatusProcessor implements AgentService.Iface {
		
	//当前正在执行的任务列表
	private CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList;

	public AgentServiceImpl(CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList) {
		this.executingTaskList = executingTaskList;
	}

	@Override
	public CommandResult executeTask(TaskEntity taskEntity) throws TException {
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

}
