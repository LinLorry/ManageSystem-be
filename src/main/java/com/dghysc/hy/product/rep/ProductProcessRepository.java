package com.dghysc.hy.product.rep;

import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.product.model.ProductProcessId;
import com.dghysc.hy.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * Product Process Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ProductProcessRepository extends JpaRepository<ProductProcess, ProductProcessId> {

    List<ProductProcess> findAllByFinishTimeAfterAndFinishTimeBefore(Timestamp after, Timestamp before);

    Page<ProductProcess> findAllByFinisher(User finisher, Pageable pageable);

}
