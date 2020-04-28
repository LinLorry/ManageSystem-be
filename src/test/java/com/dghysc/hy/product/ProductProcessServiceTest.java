package com.dghysc.hy.product;

import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProductProcessServiceTest {

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ProductProcessService productProcessService;

    @Test
    public void loadAllTodayFinish() {
        testUtil.setAuthorities("ROLE_WORKER_MANAGER");

        List<ProductProcess> productProcesses = productProcessService.loadAllTodayFinish();

        assertNotNull(productProcesses);
    }

    @Test
    public void loadAllSelfFinish() {
        testUtil.setAuthorities("ROLE_ADMIN");
        Page<ProductProcess> productProcesses = productProcessService.loadAllSelfFinish(0, 20);
        assertNotNull(productProcesses);

        for (ProductProcess productProcess : productProcesses) {
            System.out.println(productProcess.getFinishTime());
        }
    }
}