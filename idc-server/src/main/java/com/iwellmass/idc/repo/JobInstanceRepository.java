package com.iwellmass.idc.repo;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstancePK;

@Repository
public interface JobInstanceRepository extends CrudRepository<JobInstance, JobInstancePK>{
	
	@Query("SELECT u FROM JobInstance u WHERE u.taskId = ?1 AND u.groupId = ?2 AND u.loadDate = ?3")
	JobInstance findInstance(String taskId, String group, LocalDateTime loadDate);

	@Query("SELECT u FROM JobInstance u WHERE u.id = ?1")
	JobInstance findOne(Integer id);

}
