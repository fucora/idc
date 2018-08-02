package com.iwellmass.dispatcher.sdk.base;

import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.sdk.model.ExecutingTaskInfo;
import com.iwellmass.dispatcher.sdk.util.ApplicationContext;
import com.iwellmass.dispatcher.sdk.util.Constants;
import com.iwellmass.dispatcher.sdk.util.ExecuteContext;
import com.iwellmass.dispatcher.sdk.util.ServerAddressUtils;
import com.iwellmass.dispatcher.thrift.model.ExecuteStatus;
import com.iwellmass.dispatcher.thrift.model.SendResult;
import com.iwellmass.dispatcher.thrift.model.ServerAddress;
import com.iwellmass.dispatcher.thrift.model.TaskEntity;
import com.iwellmass.dispatcher.thrift.model.TaskStatusInfo;
import com.iwellmass.dispatcher.thrift.server.StatusServerService;

/**
 * 状态上报的基类，提供状态处理的公共方法
 * @author Ming.Li
 *
 */
public class StatusProcessor {

	private final static Logger log = LoggerFactory.getLogger(StatusProcessor.class);
	
	protected final TaskStatusInfo getTaskStatusInfo(ExecutingTaskInfo taskInfo) {

		TaskEntity taskEntity = taskInfo.getTaskEntity();
		ExecuteContext executeContext = (ExecuteContext)taskInfo.getExecuteContext();
		List<ExecuteStatus> statusList = executeContext.pollExecuteStatus();
		if(statusList == null) {
			return null;
		}
		TaskStatusInfo status = generateTaskStatusInfo(taskEntity);
		status.setStatusList(statusList);
		
		return status;
	}

	
	/**
	 * 获取任务状态的公共字段
	 * @param taskEntry
	 * @return
	 */
	protected final TaskStatusInfo generateTaskStatusInfo(TaskEntity taskEntity) {
		
		TaskStatusInfo status = new TaskStatusInfo();
		status.setIp(ApplicationContext.getIp());
		status.setPort(ApplicationContext.getPort());
		status.setNodeCode(ApplicationContext.getNodeCode());
		status.setTaskEntity(taskEntity);

		return status;
	}
	
	/**
	 * 上报任务执行状态
	 * @param serverList
	 * @param status
	 * @return
	 */	
	protected final boolean sendTaskStatus(TaskStatusInfo status) {
		
		List<ServerAddress> serverList = ServerAddressUtils.getServerList();
		if(serverList == null || serverList.isEmpty()) {
			log.error("DDC- 发送任务状态数据失败，最新状态服务器地址列表为空!");
			return false;
		}
		SendResult result = sendTaskStatus(serverList, status);
		if(result==null || !result.isSucceed()) {
			serverList = ServerAddressUtils.getLatestServerList();
			result = sendTaskStatus(serverList, status);
		}
		
		if(result==null || !result.isSucceed()) {
			TaskEntity taskEntity = status.getTaskEntity();
			int taskId = taskEntity.getTaskId(); // required
			long executeId = taskEntity.getExecuteId(); // required
			String executeBatchId = taskEntity.getExecuteBatchId(); // required
			int workflowId = taskEntity.getWorkflowId(); // optional
			long workflowExecuteId = taskEntity.getWorkflowExecuteId(); // optional
			List<ExecuteStatus> statusList = status.getStatusList();

			log.error("DDC- 发送任务状态数据失败，最新状态服务器地址列表：{}，错误信息：{}，任务编号：{}，执行编号：{}，执行批次编号：{}，流程编号：{}，流程任务执行编号：{}, 状态列表：{}", serverList, result==null ? "" : result.getMessage(), taskId, executeId, executeBatchId, workflowId, workflowExecuteId, statusList);
		}
		
		return result==null ? false : result.isSucceed();
	}
	
	/**
	 * 批量上报任务执行状态
	 * @param status
	 * @return
	 */
	protected final void sendTaskStatusList(List<TaskStatusInfo> status) {
		
		List<ServerAddress> serverList = ServerAddressUtils.getServerList();
		if(serverList == null || serverList.isEmpty()) {
			log.error("DDC- 发送任务状态数据失败，最新状态服务器地址列表为空!");
			return;
		}
		SendResult result = sendTaskStatusList(serverList, status);
		if(!result.isSucceed()) {
			serverList = ServerAddressUtils.getLatestServerList();
			if(serverList == null || serverList.isEmpty()) {
				log.error("DDC- 发送任务状态数据失败，最新状态服务器地址列表为空!");
				return;
			}
			result = sendTaskStatusList(serverList, status);
		}
		if(!result.isSucceed()) {
			log.error("DDC- 发送任务状态数据失败，最新状态服务器地址列表：{}，错误信息：{}", serverList, result.getMessage());		
		}
	}
	
	/**
	 * 上报任务状态
	 * @param serverList
	 * @param status
	 * @return
	 */
	private SendResult sendTaskStatus(List<ServerAddress> serverList, TaskStatusInfo status) {
		
		SendResult result = null;
		
		for(ServerAddress serverInfo : serverList) {
			for(int i=0; i<2; i++) { //单个节点重试2次
				TSocket socket = new TSocket(serverInfo.getIp(), serverInfo.getPort(), Constants.THRIFT_TIMEOUT);
		        TTransport transport = new TFramedTransport(socket);
		        try {
		        	TProtocol protocol = new TBinaryProtocol(transport);
		            StatusServerService.Client client = new StatusServerService.Client(protocol);
		            transport.open();
		            result = client.sendTaskStatus(status);
		            return result;
		        } catch (Exception ex) {
		        	result = new SendResult();
		        	result.setSucceed(false);
		        	result.setMessage(ex.getMessage());
		        } finally {
		            if (transport != null) {
		                transport.close();
		            }
		        }
			}
		}
		
		return result;
	}
	
	/**
	 * 批量上报任务状态
	 * @param serverList
	 * @param status
	 * @return
	 */
	private SendResult sendTaskStatusList(List<ServerAddress> serverList, List<TaskStatusInfo> status) {

		SendResult result = null;

		for(ServerAddress serverInfo : serverList) {
			for(int i=0; i<2; i++) { //单个节点重试2次
				TSocket socket = new TSocket(serverInfo.getIp(), serverInfo.getPort(), Constants.THRIFT_TIMEOUT * 2);
				TTransport transport = new TFramedTransport(socket);
				try {
					TProtocol protocol = new TBinaryProtocol(transport);
					StatusServerService.Client client = new StatusServerService.Client(protocol);
					transport.open();
					result = client.sendTaskStatusList(status);
					return result;
				} catch (Exception ex) {
					result = new SendResult();
					result.setSucceed(false);
					result.setMessage(ex.getMessage());
				} finally {
					if (transport != null) {
						transport.close();
					}
				}
			}

		}
		return result;
	}

}
