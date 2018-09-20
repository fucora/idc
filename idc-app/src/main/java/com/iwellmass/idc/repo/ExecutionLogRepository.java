package com.iwellmass.idc.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.executor.IDCJobEvent;
import com.iwellmass.idc.model.ExecutionLog;

@Repository
public interface ExecutionLogRepository extends CrudRepository<ExecutionLog, Long> {
	
	public default void log(Integer instanceId, String message, Object...args) {
		ExecutionLog log = ExecutionLog.createLog(instanceId, message, args);
		save(log);
	}
	
	public default void log(IDCJobEvent event) {
		ExecutionLog log = ExecutionLog.createLog(event.getInstanceId(), event.getMessage());
		save(log);
	}
	

}
