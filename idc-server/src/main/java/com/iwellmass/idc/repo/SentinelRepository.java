package com.iwellmass.idc.repo;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Sentinel;
import com.iwellmass.idc.model.SentinelPK;

@Repository
public interface SentinelRepository extends CrudRepository<Sentinel, SentinelPK> {

	@Transactional
	@Modifying
	@Query("DELETE FROM Sentinel B WHERE B.taskId = ?1 AND B.groupId = ?2 AND loadDate = ?3")
	void deleteDependency(String taskId, String groupId, LocalDateTime loadDate);

	@Query("SELECT taskId, groupId, loadDate, COUNT(*) AS cnt FROM Sentinel B GROUP BY B.taskId, B.groupId, B.loadDate HAVING COUNT(*) = 1")
	List<SentinelCheck> sentinelCheck();

	public interface SentinelCheck {
		public String getTaskId();
		public String getGroupId();
		public String getLoadDate();
	}
}
