package com.iwellmass.dispatcher.sdk.thread;

import com.iwellmass.dispatcher.sdk.util.ServerAddressUtils;

/**
 * 更新状态服务器地址信息线程
 * @author Ming.Li
 *
 */
public class ServerListThread implements Runnable {

	@Override
	public void run() {
		ServerAddressUtils.refreshServerList();
	}

}
