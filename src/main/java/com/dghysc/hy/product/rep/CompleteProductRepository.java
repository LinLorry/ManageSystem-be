package com.dghysc.hy.product.rep;

import com.dghysc.hy.product.model.CompleteProduct;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Complete Product Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor
 */
@Repository
public interface CompleteProductRepository extends CrudRepository<CompleteProduct, Long>,
        JpaSpecificationExecutor<CompleteProduct> { }
