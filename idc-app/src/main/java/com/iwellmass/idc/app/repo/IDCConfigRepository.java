package com.iwellmass.idc.app.repo;

import java.sql.Timestamp;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.iwellmass.idc.model.IDCProp;

@Repository
public interface IDCConfigRepository extends CrudRepository<IDCProp, String>{

	@Query("SELECT P FROM IDCProp P where name='version' AND updatetime = ?1")
	boolean checkDirty(Timestamp configVersion);

}
