package com.dghysc.hy.product;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

/**
 * Product Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Create Or Update Product Api
     * @param request {
     *     "id": product id: int,
     *     "serial": product serial: str,
     *     "endTime": product end time: Timestamp,
     *     "workId": product product id: int
     * }
     * @return create or update product success return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": product info: object
     * }
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public JSONObject createOrUpdate(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Long id = request.getLong("id");
        String serial = request.getString("serial");
        Timestamp endTime = request.getTimestamp("endTime");
        Integer workId = null;

        try {
            if (id == null) {
                workId = request.getInteger("workId");
                response.put("data", productService.add(serial, endTime, workId));
                response.put("message", "创建订单成功");
            } else {
                response.put("data", productService.update(id, serial, endTime));
                response.put("message", "修改订单成功");
            }
            response.put("status", 1);
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            if (id == null) {
                response.put("message", "Id为" + workId + "的生产流程不存在");
            } else {
                response.put("message", "Id为" + id + "的订单不存在");
            }
        } catch (NullPointerException e) {
            response.put("status", 0);
            if (serial == null) {
                response.put("message", "订单号不能为空");
            } else if (workId == null) {
                response.put("message", "生产流程Id不能为空");
            }
        }

        return response;
    }

    /**
     * Find Product Api
     * @param id the product id: Integer.
     * @param serial the serial product contains: String.
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
     * Complete Product Api
     * @param id the product id.
     * @return if product exist and finish success return {
     *     "status": 1,
     *     "message": "Finish product success."
     * } else return {
     *     "status": 0,
     *     "message": "message"
     * }
     */
    @PostMapping("/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public JSONObject complete(@RequestParam Long id) {
        JSONObject response = new JSONObject();

        try {
            if (productService.complete(id)) {
                response.put("status", 1);
                response.put("message", "完成订单成功");
            } else {
                response.put("status", 0);
                response.put("message", "完成订单失败，该订单还有工序未完成");
            }
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的订单不存在");
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
