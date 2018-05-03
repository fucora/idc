package com.iwellmass.dispatcher.sdk.thread;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
import com.iwellmass.dispatcher.sdk.util.ServerAddressUtils;
import com.iwellmass.dispatcher.thrift.model.HeartBeatInfo;
import com.iwellmass.dispatcher.thrift.model.NodeEnvInfo;
import com.iwellmass.dispatcher.thrift.model.SendResult;
import com.iwellmass.dispatcher.thrift.model.ServerAddress;
import com.iwellmass.dispatcher.thrift.server.StatusServerService;
import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;

/**
 * 执行器心跳线程
 * @author Ming.Li
 *
 */
@SuppressWarnings("restriction")
public class HeartBeatThread implements Runnable {

	private final static Logger log = LoggerFactory.getLogger(HeartBeatThread.class);
	
	private CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList;
	
	public HeartBeatThread(CopyOnWriteArrayList<ExecutingTaskInfo> executingTaskList) {
		this.executingTaskList = executingTaskList;
	}
	
	private Runtime runtime = null;
	
	private OperatingSystemMXBean systemMXBean = null;
	
	private ThreadMXBean threadMXBean = null;
	
	private final int kb = 1024;
	
	@Override
	public void run() {
		if(runtime == null) {
			runtime = Runtime.getRuntime();
		}
		if(systemMXBean == null) {
			systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();			
		}
		if(threadMXBean == null) {
			threadMXBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();			
		}
		List<ServerAddress> serverList = ServerAddressUtils.getServerList();
		if(serverList == null || serverList.isEmpty()) {
			log.error("DDC- 发送心跳数据失败，最新状态服务器地址列表为空!");
			return;
		}
		try {			
			HeartBeatInfo heartBeatInfo = getHeartBeatInfo();
			SendResult result = doHeartBeat(serverList, heartBeatInfo);
			if(result==null || !result.isSucceed()) {
				serverList = ServerAddressUtils.getLatestServerList();
				if(serverList == null || serverList.isEmpty()) {
					log.error("DDC- 发送心跳数据失败，最新状态服务器地址列表为空!");
					return;
				}
				result = doHeartBeat(serverList, heartBeatInfo);
			}
			if(result==null || !result.isSucceed()) {
				log.error("DDC- 发送心跳数据失败，最新状态服务器地址列表：{}，错误信息：{}", serverList, result==null ? "" : result.getMessage());
			}	
		} catch(Throwable e) {
			log.error("DDC- 发送心跳数据失败，最新状态服务器地址列表：{}，错误信息：{}", serverList, e);
		}
			
	}
	
	private SendResult doHeartBeat(List<ServerAddress> serverList, HeartBeatInfo heartBeatInfo) {
		
		SendResult result = null;
		
		for(ServerAddress serverInfo : serverList) {
			TSocket socket = new TSocket(serverInfo.getIp(), serverInfo.getPort(), Constants.THRIFT_TIMEOUT);
	        TTransport transport = new TFramedTransport(socket);
	        try {
	        	TProtocol protocol = new TBinaryProtocol(transport);
	            StatusServerService.Client client = new StatusServerService.Client(protocol);
	            transport.open();
	            result = client.sendHeartBeat(heartBeatInfo);
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
		
		return result;
	}
	
	private HeartBeatInfo getHeartBeatInfo() {
		
		HeartBeatInfo heartBeatInfo = new HeartBeatInfo();
		heartBeatInfo.setAppId(ApplicationContext.getAppId());
		heartBeatInfo.setNodeCode(ApplicationContext.getNodeCode());
		heartBeatInfo.setIp(ApplicationContext.getIp());
		heartBeatInfo.setPort(ApplicationContext.getPort());
		heartBeatInfo.setNodeEnvInfo(getNodeEnvInfo());
		heartBeatInfo.setTaskCount(executingTaskList.size());
		return heartBeatInfo;
	}
	
	//FIXME 修改为正确取数
	private NodeEnvInfo getNodeEnvInfo() {
        NodeEnvInfo envInfo = new NodeEnvInfo();
        envInfo.setTotalMemoryMachine(systemMXBean.getTotalPhysicalMemorySize() / kb / kb);
        envInfo.setFreeMemoryMachine(systemMXBean.getFreePhysicalMemorySize() / kb / kb);
        envInfo.setTotalMemoryProcess(runtime.maxMemory() / kb / kb);
        envInfo.setFreeMemoryProcess((runtime.maxMemory() - runtime.totalMemory()) / kb / kb);
        envInfo.setTotalThread(threadMXBean.getThreadCount());
        double cpuRate = systemMXBean.getSystemCpuLoad();
        envInfo.setCpuRatioMachine(cpuRate > 0 ? cpuRate : 0);
        envInfo.setCpuRatioProcess(systemMXBean.getProcessCpuLoad());

        return envInfo;
	}
}

