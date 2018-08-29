package com.iwellmass.idc.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.ExecutionLog;

@Repository
public interface ExecutionLogRepository extends CrudRepository<ExecutionLog, Long> {
	
	
	
	

}
