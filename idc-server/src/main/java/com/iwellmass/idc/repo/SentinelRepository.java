package com.iwellmass.idc.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.Sentinel;
import com.iwellmass.idc.model.SentinelPK;

@Repository
public interface SentinelRepository extends CrudRepository<Sentinel, SentinelPK> {

	public interface SentinelCheck {
		public String getTaskId();
		public String getGroupId();
		public String getLoadDate();
	}
}
