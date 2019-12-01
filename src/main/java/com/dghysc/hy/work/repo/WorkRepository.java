package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Work Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
public interface WorkRepository extends CrudRepository<Work, Integer>, JpaSpecificationExecutor<Work> {
    Page<Work> findAll(Pageable pageable);

    Page<Work> findWorksByNameContains(Pageable pageable, String name);

    boolean existsByName(String name);
}
