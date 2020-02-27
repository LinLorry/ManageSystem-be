package com.dghysc.hy.product.rep;

import com.dghysc.hy.product.model.CompleteProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Product Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface CompleteProductRepository extends JpaRepository<CompleteProduct, Long> { }
