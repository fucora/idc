package com.iwellmass.idc.app.model;

import java.util.Arrays;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.iwellmass.common.criteria.ArgValueSupplier;
import com.iwellmass.common.criteria.CriteriaInfo;
import com.iwellmass.common.criteria.In;
import com.iwellmass.common.criteria.PredicateBuilder;
import com.iwellmass.idc.model.ScheduleType;

public class ScheduleTypePredicate implements PredicateBuilder<In>{

	@Override
	public <T> Predicate build(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb, CriteriaInfo<In> criteriaInfo, ArgValueSupplier valueSupplier) {
		String attributeName = criteriaInfo.getAttributeName();
		String argName =criteriaInfo.getArgName();
		ScheduleType value = valueSupplier.getAndCast(argName);
		if (value == ScheduleType.CRON) {
			return root.get(attributeName).in(Arrays.asList(ScheduleType.DAILY, ScheduleType.HOURLY, ScheduleType.MONTHLY, ScheduleType.WEEKLY));
		} else {
			return cb.equal(root.get(attributeName), value);
		}
	}

}
