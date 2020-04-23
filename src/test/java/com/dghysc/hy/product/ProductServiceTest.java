package com.dghysc.hy.product;

import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.product.model.ProductProcessId;
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
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDate;
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
    public ProductService productService;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_PRODUCT_MANAGER");

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
        LocalDate today = LocalDate.now();

        String serial = testUtil.nextString();
        String IGT = testUtil.nextString();
        String ERP = testUtil.nextString();
        String central = testUtil.nextString();
        String area = testUtil.nextString();
        String design = testUtil.nextString();
        Timestamp beginTime = Timestamp.valueOf(today.plusDays(-testUtil.nextInt(365)).atStartOfDay());
        Timestamp demandTime = Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay());
        Timestamp endTime = Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay());
        Integer workId = testUtil.nextId(Work.class);

        Product product = productService.add(
                serial, IGT, ERP,
                central, area, design,
                beginTime, demandTime, endTime,
                workId
        );

        assertEquals(serial, product.getSerial());
        assertEquals(workId, product.getWork().getId());
    }

    @Test
    public void update() {
        LocalDate today = LocalDate.now();

        Long id = testUtil.nextId(Product.class);
        String serial = testUtil.nextString();
        String IGT = testUtil.nextString();
        String ERP = testUtil.nextString();
        String central = testUtil.nextString();
        String area = testUtil.nextString();
        String design = testUtil.nextString();
        Timestamp beginTime = Timestamp.valueOf(today.plusDays(-testUtil.nextInt(365)).atStartOfDay());
        Timestamp demandTime = Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay());
        Timestamp endTime = Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay());

        Product product = productService.update(
                id, serial, IGT,
                ERP, central, area,
                design, beginTime, demandTime,
                endTime
        );

        assertEquals(id, product.getId());
        assertEquals(serial, product.getSerial());
    }

    @Test
    @Rollback(false)
    @Transactional
    public void completeProcess() {
        testUtil.setAuthorities("ROLE_WORKER");
        Product product;

        while (true) {
            product = productRepository.findById(testUtil.nextId(Product.class))
                    .orElseThrow(EntityNotFoundException::new);
            if (product.getWork().getWorkProcesses().size() == product.getProductProcesses().size()) {
                assertFalse(productService.completeProcess(product.getId(),
                        product.getWork().getWorkProcesses().iterator().next().getProcessId()));
            } else {
                break;
            }
        }

        WorkProcess[] workProcesses = product.getWork().getWorkProcesses().toArray(new WorkProcess[0]);
        Arrays.sort(workProcesses, Comparator.comparing(WorkProcess::getSequenceNumber));
        Process process = workProcesses[product.getProductProcesses().size()].getProcess();

        boolean userCanDo = userProcessRepository.existsById(
                new UserProcessId(SecurityUtil.getUserId(), process.getId()));
        boolean result = productService.completeProcess(product.getId(), process.getId());

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
    public void unCompleteProcess() {
        Product product;

        do {
            product = productRepository.findById(testUtil.nextId(Product.class))
                    .orElseThrow(EntityNotFoundException::new);
        } while (product.getProductProcesses().size() == 0);

        int processId = product.getProductProcesses().iterator().next().getProcessId();

        productService.unCompleteProcess(
                product.getId(),
                processId
        );

        assertFalse(productProcessRepository.existsById(new ProductProcessId(product.getId(), processId)));
    }

    @Test
    @Rollback(false)
    @Transactional
    public void complete() {
        productService.complete(testUtil.nextId(Product.class));
    }

    @Test
    public void count() {
        int count = productService.countNotStart();
        System.out.println(count);

        count = productService.countStart();
        System.out.println(count);

        count = productService.countCanComplete();
        System.out.println(count);


        count = productService.countCreateProductDuringTheMonth();
        System.out.println(count);
    }

    @Test
    public void loadProductProcesses() {
        Page<ProductProcess> productProcesses = productService.loadProductProcesses(0, 20);

        assertNotNull(productProcesses);
        for (ProductProcess productProcess : productProcesses) {
            System.out.println(productProcess.getProduct());
            System.out.println(productProcess.getProcess());
        }
    }
}