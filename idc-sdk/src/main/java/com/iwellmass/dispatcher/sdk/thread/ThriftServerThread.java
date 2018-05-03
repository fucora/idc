package com.iwellmass.dispatcher.sdk.thread;

import org.apache.thrift.server.TServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行器启动thrift服务接收来自调度服务器的任务执行事件
 * 定时检查服务是否在运行，若没运行将重新启动服务
 * @author Ming.Li
 *
 */
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
				server.serve();
			} catch(Throwable e) {
				log.error("DDC-Thrift服务异常停止！", e);
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
