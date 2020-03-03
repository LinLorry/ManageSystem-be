package com.dghysc.hy.product;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.model.CompleteProduct;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.work.model.WorkProcess;
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
     * Get Product Api
     * @param id product id, only get one product if provide this.
     * @param serial serial the serial product contains.
     * @param accord accord the according day number
     * @param create accord the created day number.
     * @param withProcesses get product with processes, only when get one product.
     * @param pageNumber the page number.
     * @param pageSize the page size.
     * @return if id provide return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": only when product exist, product info: object
     * } else return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": {
     *         "total": total page number,
     *         "products": [
     *              product...
     *         ]
     *     }
     * }
     */
    @GetMapping
    public JSONObject get(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String serial,
            @RequestParam(defaultValue = "0") int accord,
            @RequestParam(defaultValue = "0") boolean create,
            @RequestParam(defaultValue = "0") boolean end,
            @RequestParam(defaultValue = "0") boolean withProcesses,
            @RequestParam(defaultValue = "0") boolean complete,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        JSONObject response = new JSONObject();

        if (!complete) {
            if (id != null) {
                try {
                    if (withProcesses) {
                        response.put("data", formatProduct(productService.loadWithProcessesById(id)));
                    } else {
                        response.put("data", productService.loadById(id));
                    }

                    response.put("message", "获取订单成功");
                } catch (EntityNotFoundException e) {
                    response.put("message", "Id为" + id + "的订单不存在");
                }
            } else {
                Map<String, Object> likeMap = new HashMap<>();
                Optional.ofNullable(serial).ifPresent(s -> likeMap.put("serial", s));

                if (create) {
                    response.put("data", getAccordProducts(likeMap, true, accord, pageNumber, pageSize));
                } else if (end) {
                    response.put("data", getAccordProducts(likeMap, false, accord, pageNumber, pageSize));
                } else {
                    response.put("data", formatPage(productService.load(likeMap, pageNumber, pageSize)));
                }

                response.put("message", "获取订单成功");
            }
        } else {
            if (id == null) {
                Map<String, Object> likeMap = new HashMap<>();
                Optional.ofNullable(serial).ifPresent(s -> likeMap.put("serial", s));

                response.put("data", formatPage(productService.loadComplete(likeMap, pageNumber, pageSize)));
                response.put("message", "获取订单成功");
            } else {
                try {
                    if (withProcesses) {
                        response.put("data", formatProduct(productService.loadCompleteWithProcessesById(id)));
                    } else {
                        response.put("data", productService.loadCompleteById(id));
                    }

                    response.put("message", "获取订单成功");
                } catch (EntityNotFoundException e) {
                    response.put("message", "Id为" + id + "的订单不存在");
                }
            }
        }

        response.put("status", 1);

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

    private JSONObject getAccordProducts(
            Map<String, Object> likeMap,
            boolean flag, int accord,
            int pageNumber, int pageSize
    ) {
        LocalDate today = LocalDate.now().plusDays(accord);

        Timestamp todayTimestamp = Timestamp.valueOf(today.atStartOfDay());
        Timestamp tomorrowTimestamp = Timestamp.valueOf(today.plusDays(1).atStartOfDay());

        if (flag) {
            return formatPage(productService.loadByCreateTimeInterval(
                    todayTimestamp, tomorrowTimestamp, likeMap, pageNumber, pageSize
            ));
        } else {
            return formatPage(productService.loadByEndTimeInterval(
                    todayTimestamp, tomorrowTimestamp, likeMap, pageNumber, pageSize
            ));
        }
    }

    private <T> JSONObject formatPage(Page<T> page) {
        JSONObject data = new JSONObject();

        data.put("total", page.getTotalPages());
        data.put("products", page.getContent());

        return data;
    }

    private JSONObject formatProduct(Product product) {
        JSONObject data = new JSONObject();

        data.put("id", product.getId());
        data.put("serial", product.getSerial());
        data.put("endTime", product.getEndTime());
        data.put("workName", product.getWorkName());

        data.put("processes", formatProcesses(
                product.getWork().getWorkProcesses(), product.getProductProcesses()
        ));

        return data;
    }

    private JSONObject formatProduct(CompleteProduct product) {
        JSONObject data = new JSONObject();

        data.put("id", product.getId());
        data.put("serial", product.getSerial());
        data.put("endTime", product.getEndTime());
        data.put("workName", product.getWorkName());

        data.put("processes", formatProcesses(
                product.getWork().getWorkProcesses(), product.getProductProcesses()
        ));

        return data;
    }

    private List<JSONObject> formatProcesses(Set<WorkProcess> workProcesses, Set<ProductProcess> productProcesses) {
        List<JSONObject> processes = new ArrayList<>(workProcesses.size());

        for (WorkProcess workProcess : workProcesses) {
            JSONObject one = new JSONObject();

            one.put("name", workProcess.getProcess().getName());
            one.put("sequence", workProcess.getSequenceNumber());
            one.put("complete", false);

            for (ProductProcess productProcess : productProcesses) {
                if (workProcess.getProcessId().equals(productProcess.getProcessId())) {
                    one.put("complete", true);
                    break;
                }
            }

            processes.add(one);
        }

        return processes;
    }
}
