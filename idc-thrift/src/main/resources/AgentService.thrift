namespace java com.dmall.dispatcher.thrift.sdk

include "ThriftModel.thrift"

#SDK端thrift服务
service AgentService {

	#执行任务（请求来自调度中心）
	ThriftModel.CommandResult executeTask(1: ThriftModel.TaskEntity taskEntity);
}