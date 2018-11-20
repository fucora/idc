package com.iwellmass.idc.app.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.PluginVersion;
import com.iwellmass.idc.quartz.IDCPlugin;

@Repository
public interface PluginVersionRepository extends CrudRepository<PluginVersion, String>{

	@Modifying
	@Query("UPDATE PluginVersion set instanceSeq = instanceSeq + 1 where version = " + IDCPlugin.VERSION)
	public int increaseInstanceSeqAndGet();

}
