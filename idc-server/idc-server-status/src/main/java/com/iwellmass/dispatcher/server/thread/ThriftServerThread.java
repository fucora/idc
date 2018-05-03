package com.iwellmass.dispatcher.server.thread;

import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ThriftServerThread implements Runnable {
	
	private final static Logger log = LoggerFactory.getLogger(ThriftServerThread.class);

	private TServer server;

	public ThriftServerThread(TServer server) {
		this.server = server;
	}
	
	@Override
	public void run() {
		if(server != null && !server.isServing()) {
			try {
				log.info("开始启动状态服务器Thrift服务！");
				server.serve();
			} catch(Throwable e) {
				log.error("状态服务器Thrift服务异常停止！", e);
			}
		}
	}
	
	/**
	 * 判断thrift服务是否在运行
	 * @return
	 */
	public boolean isServerServing() {
		
		boolean isServing = false;
		int times = 0;
		do {
			isServing = server.isServing();
			if(isServing) {
				log.info("状态服务器Thrift服务运行正常！");
				break;
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		} while(!isServing && ++times < 5);
		
		return isServing;
	}
}
