package com.iwellmass.idc.app.vo;

import com.iwellmass.common.criteria.ArgValueSupplier;
import com.iwellmass.common.criteria.CriteriaInfo;
import com.iwellmass.common.criteria.CustomCriteria;
import com.iwellmass.common.criteria.PredicateBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author nobita
 * @email nobita0522@qq.com
 * @date 2019/8/5 16:49
 * @description
 */
public class CustomCriteriaBuilder implements PredicateBuilder<CustomCriteria> {
    @Override
    public <T> Predicate build(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder, CriteriaInfo<CustomCriteria> criteriaInfo, ArgValueSupplier argValueSupplier) {
        Object argValue = argValueSupplier.getValue(criteriaInfo.getArgName());
        String value = String.valueOf(criteriaInfo.getConverter().convert(argValue));
        return criteriaBuilder.or(criteriaBuilder.like(root.get("taskName"), "%" + value.replace("_", "\\_") + "%"),
                criteriaBuilder.equal(root.get("workflowId"), value.replace("_", "\\_")));
    }
}
