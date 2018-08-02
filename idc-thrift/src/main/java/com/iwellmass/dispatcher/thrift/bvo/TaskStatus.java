package com.iwellmass.dispatcher.thrift.bvo;

public class TaskStatus {
	
	/**
	 * 因为前置条件不满足而等待
	 */
	public final static String WAITING = "WAITING";
    
	public final static String DISPATCHED = "DISPATCHED"; //客户端接收到任务
	
	public final static String RECEIVED = "RECEIVED"; //客户端接收到任务
	
	public final static String REJECTED = "REJECTED"; //线程池队列满，该任务被拒绝
	
	public final static String JVM_STOPED = "JVM_STOPED"; //虚拟机停止

    public final static String STARTED = "STARTED"; //开始

    public final static String SUCCEED = "SUCCEED"; //成功

    public final static String FAILED = "FAILED"; //失败

    public final static String RETRY = "RETRY"; //重试
    
    public final static String EXECUTE_TIMEOUT = "EXECUTE_TIMEOUT"; //执行超时
    
    public final static String RETRY_SUCCEED = "RETRY_SUCCEED"; //重试成功

    public final static String RETRY_FAIL = "RETRY_FAIL"; //重试失败

    public final static String BEAN_NOT_FOUND = "BEAN_NOT_FOUND"; //bean未发现
    
    public final static String PARAMETER_ERROR = "PARAMETER_ERROR"; //参数格式错误JSON

    public final static String PRE_TASK_UNCOMPLETED = "PRE_TASK_UNCOMPLETED"; //前置任务未完成
}
