package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.model.WorkProcessKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

/**
 * Work Process Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
@Repository
public interface WorkProcessRepository extends
        JpaRepository<WorkProcess, WorkProcessKey>,
        JpaSpecificationExecutor<WorkProcess> {
}
