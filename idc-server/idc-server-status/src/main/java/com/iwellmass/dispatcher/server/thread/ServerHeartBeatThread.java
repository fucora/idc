package com.iwellmass.dispatcher.server.thread;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.iwellmass.dispatcher.common.dao.DdcServerMapper;
import com.iwellmass.dispatcher.common.model.DdcServer;

/**
 * 状态服务器心跳线程
 * @author Ming.Li
 *
 */
public class ServerHeartBeatThread implements Runnable {

	private int id;
	
	private DdcServerMapper serverMapper;
	
	private final static Logger logger = LoggerFactory.getLogger(ServerHeartBeatThread.class);
	
	public ServerHeartBeatThread(int id, DdcServerMapper serverMapper) {
		this.id = id;
		this.serverMapper = serverMapper;
	}
	
	@Override
	public void run() {

		try {
			DdcServer record = new DdcServer();
			record.setId(id);
			record.setLastHbTime(new Date());
			
			serverMapper.updateByPrimaryKeySelective(record);			
		} catch(Throwable e) {
			logger.error("服务器心跳失败，错误信息：{}", e.getMessage());
		}
	}
}
