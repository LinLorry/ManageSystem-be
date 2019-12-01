package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Process Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
public interface ProcessRepository extends CrudRepository<Process, Integer>, JpaSpecificationExecutor<Process> {
    Page<Process> findAll(Pageable pageable);

    boolean existsByName(String name);
}
