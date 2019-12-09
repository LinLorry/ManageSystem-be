package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.Work;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Work Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
@Repository
public interface WorkRepository extends CrudRepository<Work, Integer>, JpaSpecificationExecutor<Work> {
    boolean existsByName(String name);

    void deleteById(Integer id);
}
