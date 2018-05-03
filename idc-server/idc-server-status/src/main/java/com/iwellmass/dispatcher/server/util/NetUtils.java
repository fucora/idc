package com.iwellmass.dispatcher.server.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetUtils {
	
    private final static Logger logger = LoggerFactory.getLogger(NetUtils.class);

    private static final String LOCALHOST_IP = "127.0.0.1";

    private static final String EMPTY_IP = "0.0.0.0";

    private static final List<String> FILTER_IP;

    private static final Pattern IP_PATTERN = Pattern.compile("[0-9]{1,3}(\\.[0-9]{1,3}){3,}");

    static {
        FILTER_IP = new ArrayList<>();
        FILTER_IP.add("192.168.122.1");
        FILTER_IP.add("169.254.95.120");
    }

    /**
     * 当前主机的IP地址
     */
    public final static String CURRENT_HOST_IP = getCurrentLocalHostIP();


    /**
     * 获取本机IP 每次获取都会访问服务器网卡信息
     *
     * @return 本机IP
     */
    private static String getCurrentLocalHostIP() {
        String localIP = null;
        try {
            InetAddress localAddress = InetAddress.getLocalHost();
            if (isValidHostAddress(localAddress)) {
                localIP = localAddress.getHostAddress();
            }
        } catch (Throwable e) {
            logger.warn("DDC-获取IP地址失败，错误信息：{}", e.getMessage());
        }

        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                List<String> hostIPList = new ArrayList<>();
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        if (addresses != null) {
                            while (addresses.hasMoreElements()) {
                                try {
                                    InetAddress address = addresses.nextElement();
                                    if (isValidHostAddress(address)) {
                                        hostIPList.add(address.getHostAddress());
                                    }
                                } catch (Throwable e) {
                                    logger.warn("DDC-获取IP地址失败，错误信息：{}", e.getMessage());
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("DDC-获取IP地址失败，错误信息：{}", e.getMessage());
                    }
                }
                if (localIP != null && hostIPList.contains(localIP)) {
                    return localIP;
                } else {
                    return hostIPList.get(0);
                }
            }
        } catch (IOException e) {
            logger.error("DDC-获取IP地址失败，错误信息：{}", e.getMessage());
        }

        return localIP;
    }

    /**
     * 验证IP是否真实IP
     * 
     * @param address
     * @return 是否是真实IP
     */
    private static boolean isValidHostAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress() || !address.isSiteLocalAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        if (FILTER_IP.contains(name)) {
            return false;
        }
        return (name != null && !EMPTY_IP.equals(name) && !LOCALHOST_IP.equals(name) && IP_PATTERN.matcher(name).matches());
    }

}
