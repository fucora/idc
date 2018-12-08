package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class TempDefinedBuilder implements PredicateBuilder<Like> {

    @Override
    public <T> Predicate build(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb, CriteriaInfo<Like> criteriaInfo, ArgValueSupplier valueSupplier) {
        Object criteriaValue = valueSupplier.getValue(criteriaInfo.getArgName());
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("taskId"),criteriaValue));
        predicates.add(cb.like(root.get("taskName"),"%" + criteriaValue + "%"));
        return cb.or(predicates.toArray(new Predicate[predicates.size()]));
    }
}
