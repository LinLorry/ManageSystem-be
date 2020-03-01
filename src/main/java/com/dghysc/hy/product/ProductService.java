package com.dghysc.hy.product;

import com.dghysc.hy.product.model.CompleteProduct;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.rep.CompleteProductRepository;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.SpecificationUtil;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.WorkRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;

/**
 * Product Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class ProductService {

    private final WorkRepository workRepository;

    private final ProductRepository productRepository;

    private final CompleteProductRepository completeProductRepository;

    public ProductService(
            WorkRepository workRepository,
            ProductRepository productRepository,
            CompleteProductRepository completeProductRepository) {
        this.workRepository = workRepository;
        this.productRepository = productRepository;
        this.completeProductRepository = completeProductRepository;
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
     * Add Product Service
     * @param serial the serial.
     * @param endTime the endTime.
     * @param workId the work id.
     * @return the product.
     * @throws NullPointerException if {@code serial}  or {@code workId} is null
     * @throws EntityNotFoundException if work id is {@code workId} not exist
     */
    Product add(@NotNull String serial, @Nullable Timestamp endTime, @NotNull Integer workId) {
        Work work = workRepository.findById(Optional.of(workId).get())
                .orElseThrow(EntityNotFoundException::new);
        Product product = new Product(serial, work, SecurityUtil.getUser());

        product.setEndTime(endTime);

        return productRepository.save(product);
    }

    /**
     * Update Product Service.
     * @param id the product id.
     * @param serial the product serial.
     * @param endTime the product endTime.
     * @return the product.
     */
    Product update(@NotNull Long id, @Nullable String serial, @Nullable Timestamp endTime) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User updater = SecurityUtil.getUser();

        Product product = productRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        Optional.ofNullable(serial).ifPresent(product::setSerial);
        Optional.ofNullable(endTime).ifPresent(product::setEndTime);
        product.setUpdateUser(updater);
        product.setUpdateTime(now);

        return productRepository.save(product);
    }

    /**
     * Complete Product
     * @param id the product id.
     * @return the complete product.
     */
    @Transactional
    public CompleteProduct complete(@NotNull Long id) {
        // TODO 判断生产流程是否完成
        Product product = productRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        CompleteProduct completeProduct = completeProductRepository.save(new CompleteProduct(product, SecurityUtil.getUser()));
        productRepository.deleteById(id);

        return completeProduct;
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

    /**
     * Remove Product By Id
     * @param id the product id.
     * @throws org.springframework.dao.EmptyResultDataAccessException
     *      if the product didn't exists throw this exception.
     */
    void removeById(Long id) throws EmptyResultDataAccessException {
        productRepository.deleteById(id);
    }
}
