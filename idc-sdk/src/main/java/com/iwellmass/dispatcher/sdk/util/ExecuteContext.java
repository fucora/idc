package com.iwellmass.dispatcher.sdk.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iwellmass.dispatcher.thrift.bvo.TaskStatus;
import com.iwellmass.dispatcher.thrift.model.ExecuteStatus;

public class ExecuteContext implements IBasicExecuteContext {
	
    private final String CUSTOM_STATUS_PERFIX = "CUSTOM.";
    
	private int threadCount = 1;
	
	private long executeId;
	
	private String executeBatchId;
	
	private long fireTime;
	
	private String parameters;
	
	private volatile Map<Object, Object> customParam = new HashMap<Object, Object>();

	private volatile List<ExecuteStatus> statusList = new ArrayList<ExecuteStatus>();

	public ExecuteContext(int threadCount, long executeId, String executeBatchId, long fireTime, String parameters) {
		this.threadCount = threadCount;
		this.executeId = executeId;
		this.executeBatchId = executeBatchId;
		this.fireTime = fireTime;
		this.parameters = parameters;
	}

	/**
	 * 提取并移除当前已有状态
	 * @return
	 */
	public List<ExecuteStatus> pollExecuteStatus() {
		synchronized(this) {
			if(statusList.isEmpty()) {
				return null;
			}
			List<ExecuteStatus> status = statusList;
			statusList = new ArrayList<ExecuteStatus>();
			return status;
		}
	}

	/**
	 * 添加任务执行状态
	 * 
	 * 	<p>代码示例:
	 *  <pre>
	 *  public void execute(String params, ExecuteContext ctx) {
	 *  	..........
	 *  	ctx.addExecuteStatus("FETCH_DATA_FINISHED", "获取数据完成");
	 *  	..........
	 *  }
	 *  </pre>
	 * @param status
	 * @param message
	 */
	public void addExecuteStatus(String status, String message) {
	    if(!StringUtils.isBlank(status)){

    		ExecuteStatus es = new ExecuteStatus();
    		es.setStatus(status);
    		es.setTime(System.currentTimeMillis());
    		es.setMessage(message);
    		synchronized(this) {
    			statusList.add(es);        	
    		}
	    }
	}
	
	@Override
	public void addCustomStatus(String status, String message) {
	    if(!StringUtils.isBlank(status)){
	        addExecuteStatus(CUSTOM_STATUS_PERFIX + status, message);
	    }
	}

	@Override
	public void executeSucceed() {
	    executeSucceed("任务执行成功");
	}
	
	@Override
	public void executeSucceed(String message) {
		addExecuteStatus(TaskStatus.SUCCEED, message);
	}

	@Override
	public void executeFailed() {
		executeFailed("任务执行失败");
	}

	@Override
	public void executeFailed(String message) {
		addExecuteStatus(TaskStatus.FAILED, message);
	}

	@Override
	public long getExecuteId() {
		return this.executeId;
	}
	
	@Override
	public String getExecuteBatchId() {
		return this.executeBatchId;
	}

	@Override
	public int getThreadCount() {
		return threadCount;
	}

	@Override
	public long getFireTime() {
		return fireTime;
	}

	@Override
	public String getParameters() {
		return parameters;
	}

	@Override
	public void putCustomParam(Object key, Object value) {
		customParam.put(key, value);
	}

	@Override
	public Object getCustomParam(Object key) {
		return customParam.get(key);
	}
	
}
