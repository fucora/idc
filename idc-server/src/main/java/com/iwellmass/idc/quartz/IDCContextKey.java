package com.iwellmass.idc.quartz;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class IDCContextKey<T> {
	
	// 缓存
	private static final Map<String, IDCContextKey<?>> cache = new HashMap<>();
	// NULL_VALUE
	private static final Object NULL_VALUE = new Object();
	
	private String key;
	private Class<T> type;
	private Object defaultValue = NULL_VALUE;


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
	
	public IDCContextKey<T> defaultValue(T t) {
		this.defaultValue = t;
		return this;
	}
	
	public T applyGet(JobDataMap jobDataMap) {
		Object o = jobDataMap.get(this.key);
		return get0(o);
	}
	
	public final void applyPut(JobDataMap map, T v) {
		map.put(this.key, v);
	}

	public final T applyGet(JobExecutionContext context) {
 		Object o = context.get(this.key());
 		return get0(o);
	}
	
	public final void applyPut(JobExecutionContext context, T v) {
		context.put(this.key, v);
	}
	
	@SuppressWarnings("unchecked")
	private T get0(Object o) {
		if (o == null) {
			if (defaultValue != NULL_VALUE) {
				return (T) defaultValue;
			}
			throw new NullPointerException("未设置 " + this.key + " 值");
		}
		return (T) o;
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

	public static final <T> IDCContextKey<T> def(String key, Class<T> valueType) {
		IDCContextKey<T> contextKey = new IDCContextKey<>(key, valueType);
		cache.put(key, contextKey);
		return contextKey;
	}

	public static final Collection<IDCContextKey<?>> keys() {
		return cache.values();
	}

}
