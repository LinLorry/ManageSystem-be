package com.dghysc.hy.product;

import com.dghysc.hy.product.model.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.time.LocalDate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Test
    public void loadByEndTimeToday() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Page<Product> result = productService.loadByEndTimeInterval(
                Timestamp.valueOf(today.atStartOfDay()),
                Timestamp.valueOf(tomorrow.atStartOfDay()),
                0);

        for (Product product : result) {
            System.out.println(product);
        }
    }

    @Test
    public void loadByCreateTimeToday() {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Page<Product> result = productService.loadByCreateTimeInterval(
                Timestamp.valueOf(today.atStartOfDay()),
                Timestamp.valueOf(tomorrow.atStartOfDay()),
                0);

        for (Product product : result) {
            System.out.println(product);
        }
    }
}