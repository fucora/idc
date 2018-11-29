package com.iwellmass.idc.quartz;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;

import com.iwellmass.idc.ParameterParser;
import com.iwellmass.idc.model.JobInstance;

public class IDCContextKey<T> {

	// 缓存
	private static final Map<String, IDCContextKey<?>> cache = new HashMap<>();
	// NULL_VALUE
	private static final Object NIL = new Object();

	// ~~ scheduler ~~
	public static final IDCContextKey<IDCPlugin> IDC_PLUGIN = defReq("idc.plugin", IDCPlugin.class);
	
	// ~~ JOB ~~
	public static final IDCContextKey<String> JOB_JSON = defOpt("idc.job.json", String.class, null);
	public static final IDCContextKey<String> JOB_RUNTIME = defOpt("idc.job.jobRuntime", String.class, null);
	public static final IDCContextKey<Boolean> JOB_REOD = defOpt("idc.job.redo", Boolean.class, false);
	/** 参数解析 */
	public static final IDCContextKey<ParameterParser> JOB_PARAMETER_PARSER = defOpt("idc.job.parameterParser", ParameterParser.class, new ParameterParser());
	
	// ~~ Context ~~
	/** 任务实例 */
	public static final IDCContextKey<JobInstance> CONTEXT_INSTANCE = defReq("idc.context.jobInstance", JobInstance.class);
	
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
	public T applyGet(Scheduler scheduler) {
		SchedulerContext context;
		try {
			context = scheduler.getContext();
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		Object o = context.get(this.key);
		if (o == null) {
			if (defaultValue != NIL) {
				return (T) defaultValue;
			}
			throw new NullPointerException("未设置 " + this.key + " 值");
		}
		return (T) o;
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
	
	@SuppressWarnings("unchecked")
	public T applyGet(JobExecutionContext context) {
		Object o = context.get(this.key);
		if (o == null) {
			if (defaultValue != NIL) {
				return (T) defaultValue;
			}
			throw new NullPointerException("未设置 " + this.key + " 值");
		}
		return (T) o;
	}
	
	public final void applyPut(Scheduler scheduler, T v) {
		SchedulerContext context;
		try {
			context = scheduler.getContext();
		} catch (SchedulerException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		context.put(this.key, v);
	}
	
	public final void applyPut(Map<String, Object> map, T v) {
		map.put(this.key, v);
	}
	
	public final void applyPut(JobExecutionContext context, T v) {
		context.put(this.key, v);
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
