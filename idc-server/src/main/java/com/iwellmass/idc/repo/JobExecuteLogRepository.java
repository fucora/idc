package com.iwellmass.idc.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobExecutionLog;

@Repository
public interface JobExecuteLogRepository extends CrudRepository<JobExecutionLog, Long> {

}
