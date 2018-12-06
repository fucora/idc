package com.iwellmass.idc.app.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobBarrier;

import java.util.List;

@Repository
public interface JobBarrierRepo extends CrudRepository<JobBarrier, Integer>{

	@Modifying
	@Query("DELETE FROM JobBarrier B WHERE B.barrierId = ?1 AND B.barrierGroup = ?2 AND B.barrierShouldFireTime = ?3")
	void deleteBarriers(String barrierId, String barrierGroup, Long shouldFireTime);

	@Modifying
	@Query("DELETE FROM JobBarrier B WHERE B.jobId = ?1 AND B.jobGroup = ?2")
	void clearJobBarrier(String taskId, String taskGroup);

	public List<JobBarrier> findByJobIdAndJobGroup(String jobId,String jobGroup);

}
