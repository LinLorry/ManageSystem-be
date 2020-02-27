package com.dghysc.hy.product.rep;

import com.dghysc.hy.product.model.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Product Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor  .
 */
@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    boolean existsBySerial(String serial);

}
