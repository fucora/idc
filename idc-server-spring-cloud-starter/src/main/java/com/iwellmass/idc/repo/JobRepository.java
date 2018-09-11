package com.iwellmass.idc.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Job;
import com.iwellmass.idc.model.JobPK;
import com.iwellmass.idc.model.TaskType;

@Repository
public interface JobRepository extends CrudRepository<Job, JobPK>, JpaSpecificationExecutor<Job>{

	@Query("SELECT j FROM Job j WHERE j.taskId = ?1 and j.groupId = ?2")
	Job findOne(String taskId, String groupId);

	@Query("SELECT DISTINCT assignee FROM Job WHERE assignee IS NOT NULL")
	List<String> findAllAssignee();

	List<Job> findByTaskType(TaskType workflow);
	
	@Query("SELECT J FROM Job J WHERE J.taskId = ?1 AND J.groupId = ?2 AND J.taskType = com.iwellmass.idc.model.TaskType.WORKFLOW_TASK")
	List<Job> findSubJobs(String taskId, String groupId);

}
