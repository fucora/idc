package com.iwellmass.dispatcher.sdk.thrift;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.sdk.model.DDCException;
import com.iwellmass.dispatcher.sdk.service.ITaskService;
import com.iwellmass.dispatcher.sdk.util.ApplicationContext;
import com.iwellmass.dispatcher.sdk.util.Constants;
import com.iwellmass.dispatcher.sdk.util.NetUtils;
import com.iwellmass.dispatcher.sdk.util.ServerAddressUtils;
import com.iwellmass.dispatcher.sdk.util.StringUtils;
import com.iwellmass.dispatcher.thrift.model.ExecutorRegisterResult;
import com.iwellmass.dispatcher.thrift.model.NodeInfo;
import com.iwellmass.dispatcher.thrift.model.ServerAddress;
import com.iwellmass.dispatcher.thrift.sdk.AgentService;
import com.iwellmass.dispatcher.thrift.server.StatusServerService;
import com.iwellmass.idc.lookup.SourceLookup;

/**
 * SDK Thrift服务类
 * @author Ming.Li
 *
 */
public class AgentThriftService {

	private final static Logger log = LoggerFactory.getLogger(AgentThriftService.class);
	
	//Thrift服务的启动端口
	private int port;
	
	//重试次数，防止多应用启动端口瞬时占用
	private int retry = 0;
	
	private AgentService.Iface agentService;
	
	public AgentThriftService(AgentService.Iface agentService) {
		this.agentService = agentService;
	}
	
	/**
	 * 获取执行器的Thrift服务地址，用于接收来自调度服务器的任务执行请求
	 * @return
	 */
	public TServer initThriftServer() {

		TServer server = null;
		TNonblockingServerTransport serverTransport = null;
		do {
			try {
				port = NetUtils.getAvailablePort();
				if(port == -1) {
					log.error("DDC-初始化Thrift服务失败，不能找到可用端口！");
					return null;
				}
				//设置工作线程数为CPU核数, THsHaServer默认worker线程数量为5
	            //int workTheadCount = Runtime.getRuntime().availableProcessors();
				serverTransport  = new TNonblockingServerSocket(port);
				final AgentService.Processor<AgentService.Iface> processor = new AgentService.Processor<AgentService.Iface>(agentService);
				THsHaServer.Args arg = new THsHaServer.Args(serverTransport);
				arg.protocolFactory(new TBinaryProtocol.Factory());
				arg.transportFactory(new TFramedTransport.Factory());
				arg.processorFactory(new TProcessorFactory(processor));
				//arg.workerThreads(workTheadCount);
				arg.workerThreads(2);
				server = new THsHaServer(arg);	
				log.info("DDC-初始化Thrift服务成功, IP: {}，端口: {}", ApplicationContext.getIp(), port);
			} catch (Exception e) {
				log.error("DDC-初始化Thrift出错，错误信息: {}", e);
			}
		} while(server == null && ++retry < 3);
		
		return server;
	}
	
	/**
	 * 注册执行器
	 * 1. 获取当前状态服务器地址列表（随机列表）
	 * 2. 轮询服务器地址进行注册，成功就返回
	 * 3. 若所有服务器地址都注册失败，重新获取最新服务器地址重复步骤2
	 * @param projectCode
	 * @param modelCode
	 * @param taskMap
	 * @return
	 */
	public ExecutorRegisterResult registerSourceLookup(String appKey, Map<String, SourceLookup> taskMap) throws DDCException {
		
		//执行器对象
		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setAppKey(appKey);
		nodeInfo.setNodeCode(NetUtils.NODE_CODE);
		nodeInfo.setPath(NetUtils.APPLICATION_PATH);
		nodeInfo.setIp(NetUtils.CURRENT_HOST_IP);
		nodeInfo.setPort(port);
		Set<String> classNames = taskMap.keySet();
		nodeInfo.setClassNames(StringUtils.join(classNames, ","));
		nodeInfo.setOsName(System.getProperty("os.name"));
		nodeInfo.setCoreSize(Runtime.getRuntime().availableProcessors());
		nodeInfo.setVersion(Constants.SDK_VERSION);
		
		//状态服务器地址列表
		List<ServerAddress> serverList = ServerAddressUtils.getServerList();
		if(serverList == null || serverList.isEmpty()) {
			log.error("DDC-注册任务执行客户端失败！最新状态服务器地址列表为空!");
			throw new DDCException("DDC-注册任务执行客户端失败！最新状态服务器地址列表为空!");
		}
		ExecutorRegisterResult result = registerExecutorWithRetry(nodeInfo, serverList);
		if(result == null || !result.isSucceed()) {
			serverList = ServerAddressUtils.getLatestServerList();
			if(serverList == null || serverList.isEmpty()) {
				log.error("DDC-注册任务执行客户端失败！最新状态服务器地址列表为空!");
				throw new DDCException("DDC-注册任务执行客户端失败！最新状态服务器地址列表为空!");
			}
			result = registerExecutorWithRetry(nodeInfo, serverList);
		}
		return result;
		
	}
	/**
	 * 注册执行器
	 * 1. 获取当前状态服务器地址列表（随机列表）
	 * 2. 轮询服务器地址进行注册，成功就返回
	 * 3. 若所有服务器地址都注册失败，重新获取最新服务器地址重复步骤2
	 * @param projectCode
	 * @param modelCode
	 * @param taskMap
	 * @return
	 */
	public ExecutorRegisterResult registerExecutor(String appKey, Map<String, ITaskService> taskMap) throws DDCException {
		
		//执行器对象
		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setAppKey(appKey);
		nodeInfo.setNodeCode(NetUtils.NODE_CODE);
		nodeInfo.setPath(NetUtils.APPLICATION_PATH);
		nodeInfo.setIp(NetUtils.CURRENT_HOST_IP);
		nodeInfo.setPort(port);
		Set<String> classNames = taskMap.keySet();
		nodeInfo.setClassNames(StringUtils.join(classNames, ","));
		nodeInfo.setOsName(System.getProperty("os.name"));
		nodeInfo.setCoreSize(Runtime.getRuntime().availableProcessors());
		nodeInfo.setVersion(Constants.SDK_VERSION);
		
		//状态服务器地址列表
		List<ServerAddress> serverList = ServerAddressUtils.getServerList();
		if(serverList == null || serverList.isEmpty()) {
			log.error("DDC-注册任务执行客户端失败！最新状态服务器地址列表为空!");
			throw new DDCException("DDC-注册任务执行客户端失败！最新状态服务器地址列表为空!");
		}
		ExecutorRegisterResult result = registerExecutorWithRetry(nodeInfo, serverList);
		if(result == null || !result.isSucceed()) {
			serverList = ServerAddressUtils.getLatestServerList();
			if(serverList == null || serverList.isEmpty()) {
				log.error("DDC-注册任务执行客户端失败！最新状态服务器地址列表为空!");
				throw new DDCException("DDC-注册任务执行客户端失败！最新状态服务器地址列表为空!");
			}
			result = registerExecutorWithRetry(nodeInfo, serverList);
		}
		return result;
        
	}
	
	/**
	 * 遍历当前所有的可用服务器进行执行器注册，直到成功
	 * @param executorInfo
	 * @param serverList
	 * @return
	 */
	private ExecutorRegisterResult registerExecutorWithRetry(NodeInfo nodeInfo, List<ServerAddress> serverList) {
		
		int index = 0;
		ExecutorRegisterResult result = null;
		do {
			ServerAddress serverInfo = serverList.get(index);
			TSocket socket = new TSocket(serverInfo.getIp(), serverInfo.getPort(), Constants.THRIFT_TIMEOUT);
	        TTransport transport = new TFramedTransport(socket);
	        try {
	            TProtocol protocol = new TBinaryProtocol(transport);
	            StatusServerService.Client client = new StatusServerService.Client(protocol);
	            transport.open();
	            result = client.registerExecutor(nodeInfo);
	        } catch (Exception ex) {
	        	result = new ExecutorRegisterResult();
	        	result.setSucceed(false);
	        	result.setMessage(ex.getMessage());
	        } finally {
	            if (null != transport) {
	                transport.close();
	            }
	        }
		} while (!result.isSucceed() && ++index < serverList.size());
		
		return result;
	}

	public int getPort() {
		return port;
	}
}
