package com.iwellmass.idc.app.vo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.iwellmass.common.criteria.ArgValueSupplier;
import com.iwellmass.common.criteria.CriteriaInfo;
import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.PredicateBuilder;

public class TaskNameLikeOrIdEqualCriteria implements PredicateBuilder<CustomCriteria> {

	@Override
	public <T> Predicate build(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb, CriteriaInfo<CustomCriteria> criteriaInfo, ArgValueSupplier valueSupplier) {
		Object criteriaValue = valueSupplier.getValue(criteriaInfo.getArgName());
		Predicate idEqual = cb.equal(root.get("taskId"), criteriaValue);
		Predicate nameLike = cb.like(root.get("taskName"), "%" + criteriaValue + "%");
		return cb.or(idEqual, nameLike);
	}
}
