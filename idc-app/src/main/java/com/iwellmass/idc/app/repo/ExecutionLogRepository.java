package com.iwellmass.idc.app.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.iwellmass.idc.executor.IDCJobEvent;
import com.iwellmass.idc.model.ExecutionLog;
import com.iwellmass.idc.model.JobKey;

@Repository
public interface ExecutionLogRepository extends PagingAndSortingRepository<ExecutionLog, Long> {
	
	default void log(Integer instanceId, String message, Object... args) {
		ExecutionLog log = ExecutionLog.createLog(instanceId, message, args);
		save(log);
	}
	
	default void log(IDCJobEvent event) {
		ExecutionLog log = ExecutionLog.createLog(event.getInstanceId(), event.getMessage());
		save(log);
	}

	Page<ExecutionLog> findByInstanceId(Integer id, Pageable page);

	@Modifying
	@Query("DELETE FROM ExecutionLog WHERE instanceId IN ( SELECT instanceId FROM JobInstance WHERE jobId = :#{#jk.jobId} AND jobGroup = :#{#jk.jobGroup})")
	void deleteByJob(@Param("jk") JobKey jobPk);

	@Modifying
	@Transactional
	void deleteByInstanceId(Integer instanceId);

}
