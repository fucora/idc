namespace java com.dmall.dispatcher.thrift.server

include "ThriftModel.thrift"

#状态服务器对应的thrift服务
service StatusServerService {

	#接入应用启动时的注册服务
	ThriftModel.ExecutorRegisterResult registerExecutor(1: ThriftModel.NodeInfo nodeInfo);
	
	#上报任务的状态信息（单条）
	ThriftModel.SendResult sendTaskStatus(1: ThriftModel.TaskStatusInfo status);
	
	#上报任务的状态信息（批量）
	ThriftModel.SendResult sendTaskStatusList(1: list<ThriftModel.TaskStatusInfo> status);
	
	#应用心跳处理服务
	ThriftModel.SendResult sendHeartBeat(1: ThriftModel.HeartBeatInfo heartBeatInfo);
	
	#获取状态服务器地址服务
	list<ThriftModel.ServerAddress> queryServerAddress();
}