package com.dghysc.hy.product;

import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.until.SpecificationUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
     * @return the list of query result.
     */
    List<Product> load(Map<String, Object> equalMap,
                       Map<String, Object> likeMap, Integer pageNumber) {

        SpecificationUtil specificationUtil = new SpecificationUtil();

        specificationUtil.addEqualMap(equalMap);
        specificationUtil.addLikeMap(likeMap);

        Specification<Product> specification = specificationUtil.getSpecification();

        return productRepository.findAll(specification, PageRequest.of(pageNumber, 20)).getContent();
    }

    Product loadById(Long id) throws NoSuchElementException {
        return productRepository.findById(id).orElseThrow(NoSuchElementException::new);
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
