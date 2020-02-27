package com.dghysc.hy.product.rep;

import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.ProcessRepository;
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
public class ProductRepositoryTest {

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp() {
        testUtil.setAuthorities();

        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        if (workRepository.count() == 0) {
            workRepository.save(new Work(testUtil.nextString(), creator, now));
        }

        if (productRepository.count() == 0) {
            save();
        }
    }

    @Test
    public void save() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String serial = testUtil.nextString();
        Work work = workRepository.findById(testUtil.nextId(Work.class))
                .orElseThrow(EntityNotFoundException::new);
        User creator = SecurityUtil.getUser();

        Product product = productRepository.save(new Product(serial, work, creator, now));

        assertEquals(serial, product.getSerial());
        assertEquals(creator.getId(), product.getCreateUser().getId());
    }

    @Test
    @Rollback(false)
    @Transactional
    public void updateProductProcess() {
        User creator = SecurityUtil.getUser();

        Long id = testUtil.nextId(Product.class);

        Product product = productRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        Work work = workRepository.findById(testUtil.nextId(Work.class))
                .orElseThrow(EntityNotFoundException::new);

        work.getWorkProcesses().forEach(workProcess -> {
            Process process = workProcess.getProcess();
            product.getProductProcesses().add(new ProductProcess(product.getId(), process, creator));
        });
        processRepository.flush();

        Product result = productRepository.save(product);
        assertEquals(work.getWorkProcesses().size(), result.getProductProcesses().size());
    }
}