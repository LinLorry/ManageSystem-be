package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.Process;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Process Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
@Repository
public interface ProcessRepository extends CrudRepository<Process, Integer>, JpaSpecificationExecutor<Process> {
    boolean existsByName(String name);

    void deleteById(Integer id);
}
