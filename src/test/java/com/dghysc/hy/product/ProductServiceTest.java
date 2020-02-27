package com.dghysc.hy.product;

import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.rep.CompleteProductRepository;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.WorkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductServiceTest {

    @Autowired
    public TestUtil testUtil;

    @Autowired
    public WorkRepository workRepository;

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public CompleteProductRepository completeProductRepository;

    @Autowired
    public ProductService productService;

    @Before
    public void setUp() {
        testUtil.setAuthorities();

        User creator = testUtil.getUser();

        if (workRepository.count() == 0) {
            workRepository.save(new Work(testUtil.nextString(), creator,
                    new Timestamp(System.currentTimeMillis())));
        }

        if (productRepository.count() == 0) {
            add();
        }
    }

    @Test
    public void add() {
        String serial = testUtil.nextString();
        Timestamp endTime = new Timestamp(System.currentTimeMillis());
        Integer workId = testUtil.nextId(Work.class);

        Product product = productService.add(serial, endTime, workId);

        assertEquals(serial, product.getSerial());
        assertEquals(workId, product.getWork().getId());
    }

    @Test
    public void update() {
        Long id = testUtil.nextId(Product.class);
        String serial = testUtil.nextString();
        Timestamp endTime = new Timestamp(System.currentTimeMillis());

        Product product = productService.update(id, serial, endTime);

        assertEquals(id, product.getId());
        assertEquals(serial, product.getSerial());
    }

    @Test
    public void complete() {
        Long id = testUtil.nextId(Product.class);

        productService.complete(id);

        assertFalse(productRepository.existsById(id));
        assertTrue(completeProductRepository.existsById(id));
    }
}