package com.iwellmass.idc.repo;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobInstance;
import com.iwellmass.idc.model.JobInstanceStatus;

@Repository
public interface JobInstanceRepository extends PagingAndSortingRepository<JobInstance, Integer>, JpaSpecificationExecutor<JobInstance>{
	
	@Query("SELECT u FROM JobInstance u WHERE u.instanceId = ?1")
	JobInstance findOne(Integer id);
	
	@Query("SELECT u FROM JobInstance u WHERE u.taskId = ?1 and u.groupId = ?2 and loadDate = ?3")
	JobInstance findOne(String taskId, String groupId, LocalDateTime loadDate);
	
	@Query("SELECT u FROM JobInstance u WHERE u.taskId = ?1 and u.groupId = ?2 AND status IN ?3")
	List<JobInstance> findInstanceByStatus(String taskId, String groupId, List<JobInstanceStatus> status);
	
	@Query("SELECT DISTINCT assignee FROM JobInstance WHERE assignee IS NOT NULL")
	List<String> findAllAssignee();

	@Query("UPDATE JobInstance SET status = ?3 WHERE taskId = ?1 AND groupId = ?2 AND status IN ?4")
	int resetStatusFrom(String taskId, String groupId, JobInstanceStatus status, List<JobInstanceStatus> olds);
}
