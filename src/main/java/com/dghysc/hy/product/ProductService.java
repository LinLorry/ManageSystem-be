package com.dghysc.hy.product;

import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.until.SpecificationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

/**
 * Product Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Add Or Update Product
     * @param product the product will be add.
     * @return the product have be add or update.
     */
    Product addOrUpdate(Product product) {
        return productRepository.save(product);
    }

    /**
     * Load Product By Id, Name, Comment
     * @param equalMap {
     *     "the product field": value have to equal
     * }
     * @param likeMap {
     *     "the product field": value will be equal by "%value%"
     * }
     * @param pageNumber page number.
     * @return the page of query result.
     */
    Page<Product> load(Map<String, Object> equalMap,
                       Map<String, Object> likeMap, Integer pageNumber) {

        SpecificationUtil specificationUtil = new SpecificationUtil();

        specificationUtil.addEqualMap(equalMap);
        specificationUtil.addLikeMap(likeMap);

        Specification<Product> specification = specificationUtil.getSpecification();

        return productRepository.findAll(specification, PageRequest.of(pageNumber, 20));
    }

    /**
     * Load Product By Id Service
     * @param id the product id.
     * @return the product.
     * @throws NoSuchElementException if the product isn't exist throw this exception.
     */
    Product loadById(Long id) throws NoSuchElementException {
        return productRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Load Products By End Time Interval
     * @param after the product's end time after this.
     * @param before the product's end time before this.
     * @param pageNumber the page number.
     * @return the page of the result.
     */
    Page<Product> loadByEndTimeInterval(Timestamp after, Timestamp before, Integer pageNumber) {
        SpecificationUtil specificationUtil = new SpecificationUtil();

        if (after != null) {
            specificationUtil.addGreaterDateMap("endTime", after);
        }

        if (before != null) {
            specificationUtil.addLessDateMap("endTime", before);
        }

        return productRepository.findAll(
                specificationUtil.getSpecification(),
                PageRequest.of(pageNumber, 20));
    }

    /**
     * Load Products By End Time Interval
     * @param after the product's create time after this.
     * @param before the product's create time before this.
     * @param pageNumber the page number.
     * @return the page of the result.
     */
    Page<Product> loadByCreateTimeInterval(Timestamp after, Timestamp before, Integer pageNumber) {
        SpecificationUtil specificationUtil = new SpecificationUtil();

        if (after != null) {
            specificationUtil.addGreaterDateMap("createTime", after);
        }

        if (before != null) {
            specificationUtil.addLessDateMap("createTime", before);
        }

        return productRepository.findAll(
                specificationUtil.getSpecification(),
                PageRequest.of(pageNumber, 20));
    }

    /**
     * Check Product By Name
     * @param serial the product serial.
     * @return if name is exist return true else return false.
     */
    boolean checkBySerial(String serial) {
        return productRepository.existsBySerial(serial);
    }
}
