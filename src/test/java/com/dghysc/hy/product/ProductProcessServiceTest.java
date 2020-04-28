package com.dghysc.hy.product;

import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductProcessServiceTest {

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ProductProcessService productProcessService;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_ADMIN");
    }

    @Test
    public void loadAllSelfFinish() {
        Page<ProductProcess> productProcesses = productProcessService.loadAllSelfFinish(0, 20);
        assertNotNull(productProcesses);

        for (ProductProcess productProcess : productProcesses) {
            System.out.println(productProcess.getFinishTime());
        }
    }

    @Test
    public void loadAllByFinishTimeAfterAndFinishTimeBefore() {
        LocalDate today = LocalDate.now();
        List<ProductProcess> productProcesses = productProcessService
                .loadAllByFinishTimeAfterAndFinishTimeBefore(
                        Timestamp.valueOf(today.plusDays(-testUtil.nextInt(365)).atStartOfDay()),
                        Timestamp.valueOf(today.plusDays(testUtil.nextInt(365)).atStartOfDay())
                );

        assertNotNull(productProcesses);
        for (ProductProcess productProcess : productProcesses) {
            System.out.println(
                    productProcess.getProductId() + " : " +
                    productProcess.getProcessId());
        }
    }
}