package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.model.WorkProcessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

/**
 * Work Process Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
public interface WorkProcessRepository extends
        JpaRepository<WorkProcess, WorkProcessKey>,
        JpaSpecificationExecutor<WorkProcess> {
}
