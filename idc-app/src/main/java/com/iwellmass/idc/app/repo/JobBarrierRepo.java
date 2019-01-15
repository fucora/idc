package com.iwellmass.idc.app.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobBarrier;

import java.util.List;

import javax.transaction.Transactional;

@Repository
public interface JobBarrierRepo extends CrudRepository<JobBarrier, Integer>{

	@Transactional
	@Modifying
	@Query("DELETE FROM JobBarrier B WHERE B.barrierId = ?1 AND B.barrierGroup = ?2 AND B.barrierShouldFireTime = ?3")
	void deleteByBarrierKey(String barrierId, String barrierGroup, Long shouldFireTime);

	@Transactional
	@Modifying
	@Query("DELETE FROM JobBarrier B WHERE B.jobId = ?1 AND B.jobGroup = ?2")
	void deleteByJobIdAndJobGroup(String taskId, String taskGroup);

	@Transactional
	@Modifying
	void deleteByJobGroup(String jobGroup);
	
	public List<JobBarrier> findByJobIdAndJobGroup(String jobId,String jobGroup);


}
