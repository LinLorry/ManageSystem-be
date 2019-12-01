package com.dghysc.hy.until;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class SpecificationUtil<E> {
    public Specification<E> getSpecification(Map<String, Object> equalMap, Map<String, Object> likeMap) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            equalMap.forEach((key, value) -> {
                if (key.length() != 0) {
                    predicates.add(criteriaBuilder.equal(root.get(key), value));
                }
            });

            likeMap.forEach((key, value) -> {
                if (key.length() != 0) {
                    predicates.add(criteriaBuilder.like(root.get(key), "%" + value + "%"));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
