package com.dghysc.hy.product;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductStatus;
import com.dghysc.hy.until.SecurityUtil;
import com.dghysc.hy.work.WorkService;
import com.dghysc.hy.work.model.Work;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

/**
 * Product Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@Controller
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    private final WorkService workService;

    public ProductController(ProductService productService, WorkService workService) {
        this.productService = productService;
        this.workService = workService;
    }

    /**
     * Create Product Api
     * @param request {
     *     "serial": product serial: String[must],
     *     "workId": product product id: Integer[must],
     *     "endTime": product end time: Timestamp[must],
     * }
     * @return create product success return {
     *     "status": 1,
     *     "message": "Create product success."
     *     "data": {
     *         "id": product id: Integer,
     *         "serial": product serial: String,
     *         "workId": product product id: Integer,
     *         "workName": product product name: String,
     *         "status": product status: String,
     *         "createUser": create user name: String,
     *         "createTime": product create time: Timestamp,
     *         "endTime": product end time: Timestamp
     *     }
     * }
     */
    @ResponseBody
    @PostMapping("/create")
    public JSONObject create(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        String serial = request.getString("serial");
        Integer workId = request.getInteger("workId");
        Timestamp endTime = request.getTimestamp("endTime");

        if (serial == null || serial.length() == 0 ) {
            response.put("status", 0);
            response.put("message", "Must have serial.");
            return response;
        } else if (workId == null) {
            response.put("status", 0);
            response.put("message", "Must have work id.");
            return response;
        } else if (endTime == null) {
            response.put("status", 0);
            response.put("message", "Must have end time.");
            return response;
        }

        Product product = new Product();

        product.setSerial(serial);
        product.setCreateTime(new Timestamp(System.currentTimeMillis()));
        product.setCreateUser(SecurityUtil.getUser());
        product.setEndTime(endTime);
        product.setStatus(ProductStatus.PROGRESS);

        try {
            Work work = workService.loadById(workId);
            product.setWork(work);

            response.put("data", productService.addOrUpdate(product));
            response.put("status", 1);
            response.put("message", "Create product success.");
        } catch (DataIntegrityViolationException e) {
            if (!productService.checkBySerial(serial)) throw e;

            response.put("status", 0);
            response.put("message", "Process name exist.");
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "The work isn't exist.");
        }

        return response;
    }

    /**
     * Update Product Api
     * @param request {
     *     "id": product id: Integer[must]
     *     "serial": product serial: String,
     *     "workId": product work id: Integer,
     *     "endTime": product end time: Timestamp
     * }
     * @return if update success return {
     *     "status": 1,
     *     "message": "Update product success."
     *     "data": {
     *         "id": product id: Integer,
     *         "serial": product serial: String,
     *         "workId": product product id: Integer,
     *         "workName": product product name: String,
     *         "status": product status: String,
     *         "createUser": create user name: String,
     *         "createTime": product create time: Timestamp,
     *         "endTime": product end time: Timestamp
     *     }
     * }
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseBody
    @PostMapping("/update")
    @Transactional
    public JSONObject update(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Long id = request.getLong("id");
        String serial = request.getString("serial");
        Timestamp endTime = request.getTimestamp("endTime");
        Integer workId = request.getInteger("workId");
        Product product;

        if (id == null) {
            response.put("status", 0);
            response.put("message", "Must provide product id.");
            return response;
        }

        try {
            product = productService.loadById(id);

            if (serial != null) product.setSerial(serial);

            if (endTime != null) product.setEndTime(endTime);

            if (serial != null) product.setSerial(serial);

            if (workId != null) {
                Work work = workService.loadById(workId);
                product.setWork(work);
            }

            response.put("data", productService.addOrUpdate(product));
            response.put("status", 1);
            response.put("message", "Update product success.");
        } catch (DataIntegrityViolationException e) {
            if (!productService.checkBySerial(serial)) throw e;

            response.put("status", 0);
            response.put("message", "Product serial exist.");
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "This product isn't exist.");
        }

        return response;
    }

    /**
     * Find Product Api
     * @param id the product id: Integer.
     * @param serial the serial product contains: String.
     * @param status the product status: String.
     * @param pageNumber the page number: Integer.
     * @return {
     *     "status": 1,
     *     "message": "Get product success.",
     *     "data":{
     *         "total": page total number: Integer,
     *         "products": [
     *             {
     *                 "id": product id: Integer,
     *                 "serial": product serial: String,
     *                 "workId": product product id: Integer,
     *                 "workName": product product name: String,
     *                 "status": product status: String,
     *                 "createUser": create user name: String,
     *                 "createTime": product create time: Timestamp,
     *                 "endTime": product end time: Timestamp
     *             },
     *             ...
     *         ]
     *     }
     * }
     */
    @ResponseBody
    @GetMapping("/find")
    public JSONObject find(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String serial,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject response = new JSONObject();

        Map<String, Object> equalMap = new HashMap<>();
        Map<String, Object> likeMap = new HashMap<>();

        if (id != null) {
            equalMap.put("id", id);
        }

        if (serial != null) {
            likeMap.put("serial", serial);
        }

        if (status != null) {
            switch (status) {
                case "progress":
                    equalMap.put("status", ProductStatus.PROGRESS);
                case "finish":
                    equalMap.put("status", ProductStatus.FINISH);
            }
        }

        JSONObject data = new JSONObject();
        Page<Product> page = productService.load(equalMap, likeMap, pageNumber);
        data.put("total", page.getTotalPages());
        data.put("products", page.getContent());

        response.put("data", data);
        response.put("status", 1);
        response.put("message", "Get product success.");

        return response;
    }

    /**
     * Finish Product Api
     * @param id the product id.
     * @return if product exist and finish success return {
     *     "status": 1,
     *     "message": "Finish product success."
     * } else return {
     *     "status": 0,
     *     "message": "message"
     * }
     */
    @ResponseBody
    @PostMapping("/finish")
    @Transactional
    public JSONObject finish(@RequestParam Long id) {
        JSONObject response = new JSONObject();

        try {
            Product product = productService.loadById(id);

            if (product.getStatus() != ProductStatus.FINISH) {
                product.setStatus(ProductStatus.FINISH);
                product.setFinishTime(new Timestamp(System.currentTimeMillis()));
            }

            productService.addOrUpdate(product);
            response.put("status", 1);
            response.put("message", "Finish product success,");
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "This product isn't exist.");
        }

        return response;
    }

    /**
     * Get Today Create Product Api
     * @param pageNumber the page number.
     * @return {
     *     "status": 1,
     *     "message": "Get today create product success.",
     *     "data":{
     *         "total": page total number: Integer,
     *         "products": [
     *             {
     *                 "id": product id: Integer,
     *                 "serial": product serial: String,
     *                 "workId": product product id: Integer,
     *                 "workName": product product name: String,
     *                 "status": product status: String,
     *                 "createUser": create user name: String,
     *                 "createTime": product create time: Timestamp,
     *                 "endTime": product end time: Timestamp
     *             },
     *             ...
     *         ]
     *     }
     * }
     */
    @ResponseBody
    @GetMapping("/todayCreate")
    public JSONObject todayCreate(@RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject response = new JSONObject();

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        Timestamp todayTimestamp = Timestamp.valueOf(today.atStartOfDay());
        Timestamp tomorrowTimestamp = Timestamp.valueOf(tomorrow.atStartOfDay());

        JSONObject data = new JSONObject();
        Page<Product> page = productService.loadByCreateTimeInterval(
                todayTimestamp, tomorrowTimestamp, pageNumber);
        data.put("total", page.getTotalPages());
        data.put("products", page.getContent());

        response.put("data", data);
        response.put("status", 1);
        response.put("message", "Get today create product success.");

        return response;
    }

    /**
     * Get According End Time Product Api
     * @param accord the according day number.
     * @param pageNumber the page number.
     * @return {
     *     "status": 1,
     *     "message": "Get products success.",
     *     "data":{
     *         "total": page total number: Integer,
     *         "products": [
     *             {
     *                 "id": product id: Integer,
     *                 "serial": product serial: String,
     *                 "workId": product product id: Integer,
     *                 "workName": product product name: String,
     *                 "status": product status: String,
     *                 "createUser": create user name: String,
     *                 "createTime": product create time: Timestamp,
     *                 "endTime": product end time: Timestamp
     *             },
     *             ...
     *         ]
     *     }
     * }
     */
    @ResponseBody
    @GetMapping("/accordEnd")
    public JSONObject accordEnd(
            @RequestParam(defaultValue = "0") Integer accord,
            @RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject response = new JSONObject();

        LocalDate today = LocalDate.now().plusDays(accord);
        LocalDate tomorrow = today.plusDays(1);

        Timestamp todayTimestamp = Timestamp.valueOf(today.atStartOfDay());
        Timestamp tomorrowTimestamp = Timestamp.valueOf(tomorrow.atStartOfDay());

        JSONObject data = new JSONObject();
        Page<Product> page = productService.loadByEndTimeInterval(
                todayTimestamp, tomorrowTimestamp, pageNumber);
        data.put("total", page.getTotalPages());
        data.put("products", page.getContent());

        response.put("data", data);
        response.put("status", 1);
        response.put("message", "Get products success.");

        return response;
    }

    /**
     * Delete Product Api.
     * @param id the product id.
     * @return {
     *     "status": 1,
     *     "message": "Delete product success."
     * }
     */
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @DeleteMapping("/delete")
    public JSONObject delete(@RequestParam Long id) {
        JSONObject response = new JSONObject();

        productService.removeById(id);

        response.put("status", 1);
        response.put("message", "Delete product success.");

        return response;
    }
}
