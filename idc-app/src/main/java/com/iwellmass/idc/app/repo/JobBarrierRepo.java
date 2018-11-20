package com.iwellmass.idc.app.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobBarrier;

@Repository
public interface JobBarrierRepo extends CrudRepository<JobBarrier, Integer>{

	@Modifying
	@Query("DELETE FROM JobBarrier B WHERE B.barrierId = ?1 AND B.barrierGroup = ?2 AND B.shouldFireTime = ?3")
	void deleteBarriers(String barrierId, String barrierGroup, Long shouldFireTime);

	@Modifying
	@Query("DELETE FROM JobBarrier B WHERE B.srcJobId = ?1 AND B.srcJobGroup = ?2")
	void clearJobBarrier(String taskId, String taskGroup);

}
