package com.dghysc.hy.product.rep;

import com.dghysc.hy.product.model.Product;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

/**
 * Product Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 * @see org.springframework.data.jpa.repository.JpaSpecificationExecutor  .
 */
@Repository
public interface ProductRepository extends CrudRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT COUNT(p) " +
            "FROM Product p " +
            "WHERE p.id in (" +
            "SELECT product.id " +
            "FROM Product product " +
            "LEFT OUTER JOIN product.productProcesses productProcesses " +
            "GROUP BY product.id " +
            "HAVING COUNT(productProcesses)=0" +
            ")")
    int countAllNotStart();

    @Query("SELECT COUNT(p) " +
            "FROM Product p " +
            "WHERE p.id in (" +
            "SELECT product.id " +
            "FROM Product product " +
            "JOIN product.productProcesses productProcesses " +
            "GROUP BY product.id" +
            ")")
    int countALLStart();

    @Query("SELECT COUNT(p) " +
            "FROM Product p " +
            "WHERE p.id IN (" +
                "SELECT product.id " +
                "FROM Product product " +
                "JOIN product.work work " +
                "JOIN work.workProcesses workProcesses " +
                "LEFT JOIN ProductProcess productProcess " +
                    "on productProcess.processId = workProcesses.processId " +
                        "and productProcess.productId = product.id " +
                "WHERE product.complete=false " +
                "GROUP BY product " +
                "HAVING COUNT(productProcess.productId) = count(workProcesses)" +
            ")")
    int countAllCanComplete();

    int countAllByCreateTimeAfterAndCreateTimeBefore(Timestamp after, Timestamp before);
}
