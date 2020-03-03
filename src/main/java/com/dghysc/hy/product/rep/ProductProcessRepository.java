package com.dghysc.hy.product.rep;

import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.product.model.ProductProcessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Product Process Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ProductProcessRepository extends JpaRepository<ProductProcess, ProductProcessId> {

    List<ProductProcess> findAllByProductId(Long productId);

}
