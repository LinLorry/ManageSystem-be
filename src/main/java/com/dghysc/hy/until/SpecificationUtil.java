package com.dghysc.hy.until;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpecificationUtil {
    private Map<String, Object> equalMap = new HashMap<>();
    private Map<String, Object> likeMap = new HashMap<>();
    private Map<String, Object> greaterMap = new HashMap<>();
    private Map<String, Object> lessMap = new HashMap<>();

    public void addEqualMap(String key, Object value) {
        equalMap.put(key, value);
    }

    public void addEqualMap(Map<String, Object> map) {
        equalMap.putAll(map);
    }

    public void addLikeMap(String key, Object value) {
        likeMap.put(key, value);
    }

    public void addLikeMap(Map<String, Object> map) {
        likeMap.putAll(map);
    }

    public <T extends Comparable<T>> void addGreaterMap(String key, T value) {
        greaterMap.put(key, value);
    }

    public void addGreaterMap(Map<String, Object> map) {
        greaterMap.putAll(map);
    }

    public <T extends Comparable<T>> void addLessMap(String key, T value) {
        lessMap.put(key, value);
    }

    public void addLessMap(Map<String, Object> map) {
        greaterMap.putAll(map);
    }

    public <E> Specification<E> getSpecification() {
        return ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (equalMap.size() != 0) {
                equalMap.forEach((key, value) -> {
                    if (key.length() != 0) {
                        predicates.add(criteriaBuilder.equal(root.get(key), value));
                    }
                });
            }

            if (likeMap.size() != 0) {
                likeMap.forEach((key, value) -> {
                    if (key.length() != 0) {
                        predicates.add(criteriaBuilder.like(root.get(key), "%" + value + "%"));
                    }
                });
            }

            if (greaterMap.size() != 0) {
                greaterMap.forEach((key, value) -> {
                    if (key.length() != 0) {
                        predicates.add(criteriaBuilder.greaterThan(root.get(key), value.toString()));
                    }
                });
            }

            if (lessMap.size() != 0) {
                lessMap.forEach((key, value) -> {
                    if (key.length() != 0) {
                        predicates.add(criteriaBuilder.lessThan(root.get(key), value.toString()));
                    }
                });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
