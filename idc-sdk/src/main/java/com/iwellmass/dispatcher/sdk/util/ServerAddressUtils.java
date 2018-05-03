package com.iwellmass.dispatcher.sdk.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.thrift.model.ServerAddress;
import com.iwellmass.dispatcher.thrift.server.StatusServerService;

public class ServerAddressUtils {
	
	private static volatile List<ServerAddress> serverList = new ArrayList<ServerAddress>();
	
	private static Logger logger = LoggerFactory.getLogger(ServerAddressUtils.class);

	static {
		serverList = initServerList();
	}
	
	/**
	 * 初始化状态服务器地址列表
	 * @return
	 */
	private static List<ServerAddress> initServerList() {

		List<ServerAddress> list = new ArrayList<ServerAddress>();

		boolean succeed = false;
		int retry = 0;
		
		do {
			TSocket socket = new TSocket(ApplicationContext.getServerUrl(), Constants.THRIFT_PORT, Constants.THRIFT_TIMEOUT);
	        TTransport transport = new TFramedTransport(socket);
	        try {
	        	TProtocol protocol = new TBinaryProtocol(transport);
	            StatusServerService.Client client = new StatusServerService.Client(protocol);
	            transport.open();
	            list = client.queryServerAddress();
	            succeed = true;
	        } catch (Exception ex) {
	        	logger.error("DDC- 获取状态服务器地址列表出错！当前重试次数：{}，错误信息：{}", retry + 1, ex);
	        	try {
					Thread.sleep(500);
				} catch (InterruptedException e) {}
	        } finally {
	            if (transport != null) {
	                transport.close();
	            }
	        }
		} while (!succeed && ++retry < 5);

		return list;
	}

	/**
	 * 获取状态服务器地址列表
	 * @return
	 */
	public static List<ServerAddress> getServerList() {

		List<ServerAddress> localList = serverList;		
		if(localList.isEmpty()) {
			localList = refreshServerList();
		}
		List<ServerAddress> copiedList = new ArrayList<ServerAddress>(localList);
		Collections.shuffle(copiedList);		
		return copiedList;
	}
	
	/**
	 * 获取最新的状态服务器地址列表
	 * @return
	 */
	public static List<ServerAddress> getLatestServerList() {
		List<ServerAddress> newList = initServerList();
		if(newList != null && newList.size() > 0){
		    serverList = newList;
		}
		
		return getServerList();
	}
	
	public static List<ServerAddress> refreshServerList() {
        List<ServerAddress> newList = initServerList();
        if(newList != null && newList.size() > 0){
            serverList = newList;
        }
        
		return serverList;
	}
}
