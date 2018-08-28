package com.iwellmass.idc.repo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.JobExecutionBarrier;
import com.iwellmass.idc.model.JobExecutionBarrierPK;

@Repository
public interface JobBarrierRepository extends CrudRepository<JobExecutionBarrier, JobExecutionBarrierPK> {

	Set<JobExecutionBarrier> findByInstanceId(Integer instanceId);

	@Transactional
	@Modifying
	@Query("DELETE FROM JobExecutionBarrier B WHERE B.taskId = ?1 AND B.gorupId = ?2 AND loadDate = ?3")
	void deleteDependency(String taskId, String groupId, LocalDateTime loadDate);

	@Query("SELECT B.instanceId AS instanceId, COUNT(*) AS cnt FROM JobExecutionBarrier B GROUP BY B.instanceId HAVING COUNT(*) = 1")
	List<SentinelCheck> checkedSentinel();

	public interface SentinelCheck {
		public String getInstanceId();
	}
}
