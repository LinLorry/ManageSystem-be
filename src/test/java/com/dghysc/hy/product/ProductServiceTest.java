package com.dghysc.hy.product;

import com.dghysc.hy.product.model.CompleteProduct;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcessId;
import com.dghysc.hy.product.rep.CompleteProductRepository;
import com.dghysc.hy.product.rep.ProductProcessRepository;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.model.UserProcessId;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.repo.UserProcessRepository;
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
import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.*;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductServiceTest {

    @Autowired
    public TestUtil testUtil;

    @Autowired
    public UserProcessRepository userProcessRepository;

    @Autowired
    public WorkRepository workRepository;

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public ProductProcessRepository productProcessRepository;

    @Autowired
    public CompleteProductRepository completeProductRepository;

    @Autowired
    public ProductService productService;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_WORKER");

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
    @Rollback(false)
    @Transactional
    public void completeProcess() {
        Product product;

        while (true) {
            product = productRepository.findById(testUtil.nextId(Product.class))
                    .orElseThrow(EntityNotFoundException::new);
            if (product.getWork().getWorkProcesses().size() == product.getProductProcesses().size()) {
                assertFalse(productService.completeProcess(product.getId()));
            } else {
                break;
            }
        }

        WorkProcess[] workProcesses = product.getWork().getWorkProcesses().toArray(new WorkProcess[0]);
        Arrays.sort(workProcesses, Comparator.comparing(WorkProcess::getSequenceNumber));
        Process process = workProcesses[product.getProductProcesses().size()].getProcess();

        boolean userCanDo = userProcessRepository.existsById(
                new UserProcessId(SecurityUtil.getUserId(), process.getId()));
        boolean result = productService.completeProcess(product.getId());

        if (userCanDo) {
            assertTrue(result);
            assertTrue(productProcessRepository.existsById(new ProductProcessId(product.getId(), process.getId())));
        } else {
            assertFalse(result);
        }
    }

    @Test
    @Rollback(false)
    @Transactional
    public void complete() {
        Long id = testUtil.nextId(Product.class);
        Product product = productRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        boolean complete = product.getProductProcesses().size() == product.getWork().getWorkProcesses().size();
        boolean result = productService.complete(id);

        if (complete) {
            assertTrue(result);
            CompleteProduct completeProduct = completeProductRepository.findById(id)
                    .orElseThrow(EntityNotFoundException::new);

            assertEquals(product.getSerial(), completeProduct.getSerial());
        } else {
            assertFalse(result);
            assertTrue(productRepository.existsById(id));
            assertFalse(completeProductRepository.existsById(id));
        }
    }
}