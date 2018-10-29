package com.iwellmass.idc.quartz;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.iwellmass.idc.model.DispatchType;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.ScheduleType;

public class IDCContextKey<T> {

	// 缓存
	private static final Map<String, IDCContextKey<?>> cache = new HashMap<>();
	// NULL_VALUE
	private static final Object NIL = new Object();

	// ~~ 调度器 ~~
	public static final IDCContextKey<IDCPlugin> IDC_PLUGIN = defReq("idc.plugin", IDCPlugin.class);

	// ~~ TASK ~~
	public static final IDCContextKey<String> JOB_JSON = defReq("idc.task.json", String.class);
	
	// ~~ JOB ~~
	public static final IDCContextKey<Boolean> JOB_REOD = defOpt("idc.job.redo", Boolean.class, false);
	/** JobName */
	public static final IDCContextKey<String> JOB_ID = defReq("idc.job.id", String.class);
	/** JobGroup */
	public static final IDCContextKey<String> JOB_GROUP = defReq("idc.job.jobGroup", String.class);
	/** 调度类型（日、月、周、年） */
	public static final IDCContextKey<ScheduleType> JOB_SCHEDULE_TYPE = defReq("idc.job.scheduleType", ScheduleType.class);
	/** 自动调度 OR 手动调度*/
	public static final IDCContextKey<DispatchType> JOB_DISPATCH_TYPE = defReq("idc.job.dispatchType", DispatchType.class);
	/** 参数解析 */
	public static final IDCContextKey<ParameterParser> JOB_PARAMETER_PARSER = defOpt("idc.job.parameterParser", ParameterParser.class, new ParameterParser());
	// ~~ runtime ~~
	/** 实例 ID */
	public static final IDCContextKey<Integer> CONTEXT_INSTANCE_ID = defReq("idc.context.instanceId", Integer.class);
	/** 获取业务日期 */
	public static final IDCContextKey<LocalDateTime> CONTEXT_LOAD_DATE = defReq("idc.context.loadDate", LocalDateTime.class);
	/** 实例对象 */
	public static final IDCContextKey<JobInstance> CONTEXT_INSTANCE = defReq("idc.context.jobInstance", JobInstance.class);
	/** 运行时参数 */
	public static final IDCContextKey<String> CONTEXT_PARAMETER = defOpt("idc.context.parameter", String.class, null);

	private String key;
	private Class<T> type;
	private Object defaultValue = NIL;

	private IDCContextKey(String key, Class<T> valueType) {
		this.key = key;
		this.type = valueType;
	}

	public final String key() {
		return this.key;
	}

	public Class<T> valueType() {
		return this.type;
	}

	@SuppressWarnings("unchecked")
	public T applyGet(Map<String, Object> jobDataMap) {
		Object o = jobDataMap.get(this.key);
		if (o == null) {
			if (defaultValue != NIL) {
				return (T) defaultValue;
			}
			throw new NullPointerException("未设置 " + this.key + " 值");
		}
		return (T) o;
	}

	public final void applyPut(Map<String, Object> map, T v) {
		map.put(this.key, v);
	}


	/** define required key-value */
	public static final <T> IDCContextKey<T> defReq(String key, Class<T> valueType) {
		IDCContextKey<T> contextKey = new IDCContextKey<>(key, valueType);
		cache.put(key, contextKey);
		return contextKey;
	}

	/** define option key-value */
	public static final <T> IDCContextKey<T> defOpt(String key, Class<T> valueType, T defaultValue) {
		IDCContextKey<T> contextKey = new IDCContextKey<>(key, valueType);
		contextKey.defaultValue = defaultValue;
		cache.put(key, contextKey);
		return contextKey;
	}

	public static final Collection<IDCContextKey<?>> keys() {
		return cache.values();
	}

}
