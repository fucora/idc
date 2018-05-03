namespace java com.dmall.dispatcher.thrift.model

#执行命令发送状态
struct CommandResult {
	#命令是否成功
	1: required bool succeed,
	
	#是否需要重新发送
	2: required bool needRetry,
	
	#信息
	3: optional string message
}

#待执行任务对象
struct TaskEntity {
	#应用编号
	1: required i32 appId,
	
	#任务对应的class
	2: required string className,
	
	#任务执行参数
	3: optional string parameters,
	
	#数据量阈值
	4: optional i32 dataLimit,

	#任务编号
    5: required i32 taskId, 
    
    #流程模板编号
    6: optional i32 workflowId, 
    
    #本次执行编号（执行历史记录）
    7: required i64 executeId, 
    
    #派发次数
    8: required i32 dispatchCount,
    
    #流程子任务对应的流程任务执行编号
    9: optional i64 workflowExecuteId, 
    
    #本次执行批次（执行历史记录）
    10: required string executeBatchId,
    
    #执行线程数（默认为1，任务需并发执行时可在web配置线程数）
    11: required i32 threadCount, 
    
    #是否失败重试
    12: required bool needRetry,
    
    #总重试次数
    13: optional i32 retryTimes,
    
    #当前重试次数
    14: optional i32 currentRetryTime,
    
    #重试间隔时间
    15: optional i64 retryInterval,
    
    #任务触发时间
    16: optional i64 fireTime
}

#节点（SDK）信息
struct NodeInfo {
	#应用标识
	1: required string appKey,
	
	#实例标识（Tomcat为端口号，其他为运行路径Hash值）
	2: required string nodeCode,
	
	#应用运行路径
	3: required string path,
	
	#应用运行IP地址
	4: required string ip,
	
	#SDK接收调度中心请求的thrift端口
	5: required i32 port,
	
	#应用所有任务（Cron、流程子任务）对应类名称的全集
	6: required string classNames,
	
	#SDK版本号
	7: required string version,
	
	# 线程总数.
    8: required i32 coreSize,
    
	#操作系统
    9: required string osName
}

#执行状态
struct ExecuteStatus {
	#任务当前状态
	1: required string status,
	
	#状态对应的时间
	2: required i64 time,
    
    #状态描述
    3: optional string message
}

#任务详情及状态
struct TaskStatusInfo {
	#任务执行的IP
	1: required string ip,
	
	#任务执行的端口
	2: required i32 port,
	
	#实例标识（Tomcat为端口号，其他为运行路径Hash值）
	3: required string nodeCode,
	
	#被执行任务详情
	4: required TaskEntity taskEntity,
	
	#状态列表
	5: required list<ExecuteStatus> statusList
}

#心跳信息
struct HeartBeatInfo {
    #应用编号
	1: required i32 appId,

	2: required string nodeCode,
	
    3: required string ip,
	 
	4: required i32 port,
	
	5: optional i32 taskCount,

    #虚拟机信息
    6: optional NodeEnvInfo NodeEnvInfo
}

#实例信息（为后期服务端负载做数据参考）
struct NodeEnvInfo {
    # 机器总内存
    1: optional i64 totalMemoryMachine,

    # 机器剩余内存
    2: optional i64 freeMemoryMachine,

    # 进程总内存
    3: optional i64 totalMemoryProcess,

    # 进程剩余内存
    4: optional i64 freeMemoryProcess,

    # 线程总数.
    5: optional i32 totalThread,

    # 机器CPU使用率.
    6: optional double cpuRatioMachine,
    
    # 进程CPU使用率.
    7: optional double cpuRatioProcess
}

#SDK实例注册结果
struct ExecutorRegisterResult {
	#注册是否成功
	1: required bool succeed,
	
	#应用编码appKey对应的编号appId
	2: required i32 appId,
    
    #错误信息
    3: optional string message
}

#心跳及状态上报结果结果
struct SendResult {
	#是否成功
	1: required bool succeed,
    
    #错误信息
    2: optional string message
}

#状态服务器地址
struct ServerAddress {
	1: required string ip,
	 
	2: required i32 port
}
