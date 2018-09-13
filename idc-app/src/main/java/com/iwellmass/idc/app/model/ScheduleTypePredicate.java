package com.iwellmass.idc.app.model;

import java.util.Arrays;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.iwellmass.common.criteria.PredicateBuilder;
import com.iwellmass.idc.model.ScheduleType;

public class ScheduleTypePredicate implements PredicateBuilder{

	@Override
	public <T> Predicate build(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb, String name, Object ov) {
		ScheduleType value = (ScheduleType) ov;
		if (value == ScheduleType.CRON) {
			return root.get(name).in(Arrays.asList(ScheduleType.DAILY, ScheduleType.HOURLY, ScheduleType.MONTHLY, ScheduleType.WEEKLY));
		} else {
			return cb.equal(root.get(name), value);
		}
	}

}
