package com.iwellmass.idc.app.vo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.iwellmass.common.criteria.ArgValueSupplier;
import com.iwellmass.common.criteria.CriteriaInfo;
import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.PredicateBuilder;

public class IdOrNameCriteria implements PredicateBuilder<CustomCriteria> {

	@Override
	public <T> Predicate build(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb,
			CriteriaInfo<CustomCriteria> context, ArgValueSupplier argValueSupplier) {
		String v = argValueSupplier.getAndCast(context.getArgName(), String.class);
		Predicate p1 = cb.like(root.get("jobName"), "%" + v.replace("_", "\\_") + "%", '\\');
		Predicate p2 = cb.equal(root.get("jobId"), v);
		return cb.or(p1, p2);
	}

}
