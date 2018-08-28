package com.iwellmass.idc.server.quartz;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.service.JobExecutorService;

public class IDCContextKey<T> {

	// 组信息
	public static final String GROUP_AUTO_INJECT = "AUTO_INJECT";
	// 缓存
	private static final Map<String, IDCContextKey<?>> cache = new HashMap<>();
	// 
	private static final Object NULL_VALUE = new Object();
	
	// ~ key 信息 ~
	/**
	 * 获取 JOB 信息
	 */
	public static final IDCContextKey<Job> CONTEXT_JOB = IDCContextKey.def("job.context.job", null, Job.class);
	/**
	 * Job 实例
	 */
	public static final IDCContextKey<JobInstance> CONTEXT_INSTANCE = IDCContextKey.def("job.context.instance", null, JobInstance.class);
	/**
	 * Job 日志组件
	 */
	public static final IDCContextKey<IDCJobLogger> CONTEXT_LOGGER = IDCContextKey.def("job.context.logger", null, IDCJobLogger.class);
	/**
	 * 执行组件
	 */
	public static final IDCContextKey<JobExecutorService> CONTEXT_EXECUTOR = IDCContextKey.def("job.context.executor", GROUP_AUTO_INJECT, JobExecutorService.class);
	
	// ~~ 运行时 ~~
	/**
	 * 是否跳过本次执行
	 */
	public static final IDCContextKey<Boolean> EXECUTION_BLOCK = IDCContextKey.def("job.execution.block", null, Boolean.class).defaultValue(false);
	
	private String key;
	private String group;
	private Class<T> type;
	private Object defaultValue = NULL_VALUE;


	private IDCContextKey(String key, String group, Class<T> valueType) {
		this.key = key;
		this.group = group;
		this.type = valueType;
	}

	public final String key() {
		return this.key;
	}

	public final String group() {
		return group;
	}

	public Class<T> valueType() {
		return this.type;
	}
	
	public IDCContextKey<T> defaultValue(T t) {
		this.defaultValue = t;
		return this;
	}

	@SuppressWarnings("unchecked")
	public final T applyGet(JobExecutionContext context) {
 		Object o = context.get(this.key());
		if (o == null) {
			if (defaultValue != NULL_VALUE) {
				return (T) defaultValue;
			}
			throw new NullPointerException("未设置 " + this.key + " 值");
		}
		return (T) context.get(this.key());
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IDCContextKey) {
			return key.equals(((IDCContextKey<?>) obj).key);
		}
		return key.equals(obj);
	}
	
	@Override
	public String toString() {
		return key;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}

	public static final <T> IDCContextKey<T> def(String key, String group, Class<T> valueType) {
		IDCContextKey<T> contextKey = new IDCContextKey<>(key, group, valueType);
		cache.put(key, contextKey);
		return contextKey;
	}

	public static final Collection<IDCContextKey<?>> keys() {
		return cache.values();
	}

	public final void applyPut(JobExecutionContext context, T v) {
		context.put(this.key, v);
	}
}
