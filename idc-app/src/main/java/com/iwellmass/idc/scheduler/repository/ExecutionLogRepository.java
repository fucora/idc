package com.iwellmass.idc.scheduler.repository;

import com.iwellmass.idc.executor.IDCJobEvent;
import com.iwellmass.idc.scheduler.model.ExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ExecutionLogRepository extends PagingAndSortingRepository<ExecutionLog, Long> {
	
	default void log(String instanceId, String message, Object... args) {
		ExecutionLog log = ExecutionLog.createLog(instanceId, message, args);
		save(log);
	}
	
	default void log(IDCJobEvent event) {
		ExecutionLog log = ExecutionLog.createLog(event.getInstanceId(), event.getMessage());
		save(log);
	}

	Page<ExecutionLog> findByInstanceId(String id, Pageable page);

//	@Modifying
//	@Query("DELETE FROM ExecutionLog WHERE instanceId IN ( SELECT instanceId FROM JobInstance WHERE jobId = :#{#jk.jobId} AND jobGroup = :#{#jk.jobGroup})")
//	void deleteByJob(@Param("jk") String jobId);

	@Modifying
	@Transactional
	void deleteByInstanceId(String instanceId);

}
