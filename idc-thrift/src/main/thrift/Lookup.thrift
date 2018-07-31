namespace java com.iwellmass.idc.lookup


// 数据事件
struct SourceEvent {
	1: required string jobId,
	2: required string loadDate,
}

# 执行上下文
service LookupContext {
	string jobId();
	string jobParameter();
	string loadDate();
	void   fireSourceEvent(1: SourceEvent event);
}

# 检测器
service SourceLookup {
	void lookup(1: LookupContext context);
}