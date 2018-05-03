package com.iwellmass.dispatcher.sdk.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jmx.mbeanserver.JmxMBeanServer;

@SuppressWarnings("restriction")
public class NetUtils {
    private final static Logger logger = LoggerFactory.getLogger(NetUtils.class);

    private static final int MIN_PORT = 20000;

    private static final int MAX_PORT = 30100;

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
     * 当前实例的ID，如果为tomcat则为tomcat的端口，如果不是tomcat则为classpath的hashcode
     */
    public final static String NODE_CODE = getInstanceCode();

    /**
     * 当前运行的application路径
     */
    public final static String APPLICATION_PATH = getApplicationPath();

    /**
     * 当前主机的IP地址
     */
    public final static String CURRENT_HOST_IP = getCurrentLocalHostIP();

    /**
     * 当前可用的端口
     * 
     * @return
     */
    public static int getAvailablePort() {
        int startPort = MIN_PORT + Integer.valueOf(NODE_CODE) % 10000;
        for (int port = startPort; port < MAX_PORT; port++) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(port);
                return port;
            } catch (IOException e) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                }
            } finally {
                if (ss != null) {
                    try {
                        ss.close();
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException ex) {
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }
        return -1;
    }

    /**
     * 获取实例ID
     * 
     * @return
     */
    private static String getInstanceCode() {
        // 先去检测当前应用是否是tomcat，如果是返回端口号
        Integer port = getTomcatPortByMBean();
        if (port != null) {
            return port.toString();
        }
        int instanceValue = getApplicationPath().hashCode();
        if (instanceValue < 0) {
            instanceValue = Math.abs(instanceValue);
        }
        return String.valueOf(instanceValue);
    }

    /**
     * 从MBean获取tomcat运行端口
     * 
     * @return
     */
    private static Integer getTomcatPortByMBean() {

        final String SCHEMA = "http";
        MBeanServer mBeanServer = null;
        try {
            ArrayList<MBeanServer> mBeanServers = MBeanServerFactory.findMBeanServer(null);
            if (mBeanServers.size() > 0) {
                for (MBeanServer _mBeanServer : mBeanServers) {
                    if (_mBeanServer instanceof JmxMBeanServer) {
                        mBeanServer = _mBeanServer;
                        break;
                    }
                }
            }
            if (mBeanServer == null) {
                return null;
            }
            Set<ObjectName> objectNames = mBeanServer.queryNames(new ObjectName("Catalina:type=Connector,*"), null);
            if (objectNames == null || objectNames.size() == 0) {
                return null;
            }
            for (ObjectName objectName : objectNames) {
                Object scheme = mBeanServer.getAttribute(objectName, "scheme");
                if (SCHEMA.equals(scheme)) {
                    return (Integer) mBeanServer.getAttribute(objectName, "port");
                }
            }
        } catch (Throwable e) {
        }
        return null;
    }

    /**
     * 工程路径（web应用） classes路径 （java应用）
     */
    private static String getApplicationPath() {
        String classPath = null;
        String catalinaBase = System.getProperty("catalina.base");
        if (catalinaBase != null && catalinaBase.trim().length() > 0) {
            classPath = catalinaBase;
        } else {
        	classPath = NetUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
            classPath = classPath.replaceAll("\\\\", "/");
        }

        return classPath;
    }

    /**
     * 获取本机IP 每次获取都会访问服务器网卡信息，不建议频繁调用 非特殊需求建议调用getStaticLocalHostIP
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
            logger.warn("DDC - Failed to get ip address, " + e.getMessage(), e);
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
                                    logger.warn("DDC - Failed to get network card ip address. cause:" + e.getMessage());
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logger.warn("DDC - Failed to get network card ip address. cause:" + e.getMessage());
                    }
                }
                if (localIP != null && hostIPList.contains(localIP)) {
                    return localIP;
                } else {
                    return hostIPList.get(0);
                }
            }
        } catch (IOException e) {
            logger.error("DDC - Failed to get network card ip address. cause:" + e.getMessage(), e);
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

    public static void main(String[] args) {
        System.out.println(NetUtils.NODE_CODE);
        System.out.println(NetUtils.CURRENT_HOST_IP);
        System.out.println(NetUtils.APPLICATION_PATH);
        System.out.println(NetUtils.getAvailablePort());
    }
}
