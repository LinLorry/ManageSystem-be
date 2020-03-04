package com.dghysc.hy.product.rep;

import com.dghysc.hy.product.model.CompleteProduct;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.WorkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.sql.Timestamp;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CompleteProductRepositoryTest {

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CompleteProductRepository completeProductRepository;

    @Before
    public void setUp() {
        testUtil.setAuthorities();

        User creator = SecurityUtil.getUser();

        if (workRepository.count() == 0) {
            workRepository.save(new Work(testUtil.nextString(), creator,
                    new Timestamp(System.currentTimeMillis())));
        }

        if (productRepository.count() == 0) {
            productRepository.save(new Product(testUtil.nextString(),
                    workRepository.findById(testUtil.nextId(Work.class))
                            .orElseThrow(EntityNotFoundException::new),
                    creator));
        }
    }

    @Test
    @Rollback(false)
    @Transactional
    public void save() {
        Product product = productRepository.findById(testUtil.nextId(Product.class))
                .orElseThrow(EntityNotFoundException::new);
        User creator = SecurityUtil.getUser();
        product.setSerial(testUtil.nextString());

        CompleteProduct completeProduct = completeProductRepository
                .save(new CompleteProduct(product, creator));
        productRepository.delete(product);

        System.out.println(completeProduct.getProductProcesses());
        assertEquals(product.getSerial(), completeProduct.getSerial());
        assertEquals(creator.getId(), completeProduct.getCreateUser().getId());
    }
}