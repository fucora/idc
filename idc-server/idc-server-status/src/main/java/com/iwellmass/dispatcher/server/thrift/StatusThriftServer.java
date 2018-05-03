package com.iwellmass.dispatcher.server.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.server.util.NetUtils;
import com.iwellmass.dispatcher.thrift.server.StatusServerService;

/**
 * 状态服务器Thrift服务
 * @author Ming.Li
 *
 */
public class StatusThriftServer {
	
	private int thriftPort;
	
	private int selectorThreads;
	
	private int workerThreads;
	
	private StatusServerService.Iface statusServerService;

	private final static Logger logger = LoggerFactory.getLogger(StatusThriftServer.class);

	public TServer initThriftServer() {

		TServer server = null;
		try {
			TNonblockingServerTransport serverTransport  = new TNonblockingServerSocket(thriftPort);
			TProcessor processor = new StatusServerService.Processor<StatusServerService.Iface>(statusServerService);
			TThreadedSelectorServer.Args arg = new TThreadedSelectorServer.Args(serverTransport);
            
			// 设置轮询线程数
            arg.selectorThreads(selectorThreads);
            // 设置工作线程数
            arg.workerThreads(workerThreads);
            arg.maxReadBufferBytes = 10 * 1024 * 1024;
			
			arg.protocolFactory(new TBinaryProtocol.Factory());
			arg.transportFactory(new TFramedTransport.Factory());
			arg.processorFactory(new TProcessorFactory(processor));
			server = new TThreadedSelectorServer(arg);	
			logger.info("DDC-初始化状态服务器Thrift服务成功！IP:{}, 端口: {}，轮询线程数: {}, 工作线程数： {}", NetUtils.CURRENT_HOST_IP, thriftPort, selectorThreads, workerThreads);
		} catch (Throwable e) {
			logger.error("DDC-初始化状态服务器Thrift服务失败！错误信息: {}", e);
		}
		
		return server;
	}

	public int getThriftPort() {
		return thriftPort;
	}

	public void setThriftPort(int thriftPort) {
		this.thriftPort = thriftPort;
	}

	public int getSelectorThreads() {
		return selectorThreads;
	}

	public void setSelectorThreads(int selectorThreads) {
		this.selectorThreads = selectorThreads;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public StatusServerService.Iface getStatusServerService() {
		return statusServerService;
	}

	public void setStatusServerService(StatusServerService.Iface statusServerService) {
		this.statusServerService = statusServerService;
	}

	
}
