package com.dghysc.hy.product;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.util.ZoneIdUtil;
import com.dghysc.hy.work.model.WorkProcess;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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

        String IGT = request.getString("IGT");
        String ERP = request.getString("ERP");
        String central = request.getString("central");
        String area = request.getString("area");
        String design = request.getString("design");
        Timestamp beginTime = request.getTimestamp("beginTime");
        Timestamp demandTime = request.getTimestamp("demandTime");
        Timestamp endTime = request.getTimestamp("endTime");
        Integer workId = null;

        try {
            if (id == null) {
                workId = request.getInteger("workId");

                response.put("data", productService.add(
                        serial, IGT, ERP,
                        central, area, design,
                        beginTime, demandTime, endTime,
                        workId
                ));

                response.put("message", "创建订单成功");
            } else {

                response.put("data", productService.update(
                        id, serial, IGT,
                        ERP, central, area,
                        design, beginTime, demandTime,
                        endTime
                ));

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
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date beginTimeAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date beginTimeBefore,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date demandTimeAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date demandTimeBefore,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date endTimeAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date endTimeBefore,
            @RequestParam(defaultValue = "0") int accord,
            @RequestParam(defaultValue = "0") boolean create,
            @RequestParam(defaultValue = "0") boolean end,
            @RequestParam(defaultValue = "0") boolean withProcesses,
            @RequestParam(defaultValue = "0") boolean complete,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        JSONObject response = new JSONObject();

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
            Map<String, Date> dateGreaterMap = new HashMap<>();
            Map<String, Date> dateLesserMap = new HashMap<>();

            Optional.ofNullable(serial).ifPresent(s -> likeMap.put("serial", s));

            Optional.ofNullable(beginTimeAfter).ifPresent(t -> dateGreaterMap.put("beginTime", t));
            Optional.ofNullable(beginTimeBefore).ifPresent(t -> dateLesserMap.put("beginTime", t));
            Optional.ofNullable(demandTimeAfter).ifPresent(t -> dateGreaterMap.put("demandTime", t));
            Optional.ofNullable(demandTimeBefore).ifPresent(t -> dateLesserMap.put("demandTime", t));
            Optional.ofNullable(endTimeAfter).ifPresent(t -> dateGreaterMap.put("endTime", t));
            Optional.ofNullable(endTimeBefore).ifPresent(t -> dateLesserMap.put("endTime", t));

            if (create || end) {
                response.put("data", getAccordProducts(likeMap, dateGreaterMap, dateLesserMap, create, accord, pageNumber, pageSize));
            } else {
                response.put("data", formatPage(productService.load(
                        likeMap, dateGreaterMap, dateLesserMap, complete, pageNumber, pageSize
                )));
            }

            response.put("message", "获取订单成功");
        }

        response.put("status", 1);

        return response;
    }

    /**
     * Complete Product Process Api.
     * @param request {
     *     "productId": the product id: int,
     *     "processId": the processes id: int
     * }
     * @return {
     *     "status": if success is 1 else 0,
     *     "message": message: str
     * }
     * @throws MissingServletRequestParameterException the {@code productId} or {@code processId}
     *                                                 is {@literal null}
     */
    @PostMapping("/completeProcess")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER', 'WORKER')")
    public JSONObject completeProcesses(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long productId = Optional.ofNullable(request.getLong("productId"))
                .orElseThrow(() -> new MissingServletRequestParameterException("productId", "int"));
        Integer processId = Optional.ofNullable(request.getInteger("processId"))
                .orElseThrow(() -> new MissingServletRequestParameterException("productId", "int"));

        if (productService.completeProcess(productId, processId)) {
            response.put("status", 1);
            response.put("message", "完成成功");
        } else {
            response.put("status", 0);
            response.put("message", "完成该工序失败，你不能完成这个工序或该工序已经完成");
        }

        return response;
    }

    /**
     * Un Complete Product Process Api.
     * @param request {
     *     "productId": the product id: int,
     *     "processId": the processes id: int
     * }
     * @return {
     *     "status": if success is 1 else 0,
     *     "message": message: str
     * }
     * @throws MissingServletRequestParameterException the {@code productId} or {@code processId}
     *                                                 is {@literal null}
     */
    @PostMapping("/unCompleteProcess")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public JSONObject unCompleteProcesses(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long productId = Optional.ofNullable(request.getLong("productId"))
                .orElseThrow(() -> new MissingServletRequestParameterException("productId", "int"));
        Integer processId = Optional.ofNullable(request.getInteger("processId"))
                .orElseThrow(() -> new MissingServletRequestParameterException("productId", "int"));

        try {
            productService.unCompleteProcess(productId, processId);
            response.put("status", 1);
            response.put("message", "取消工序完成成功！");
        } catch (EmptyResultDataAccessException e) {
            response.put("status", 0);
            response.put("message", "该工序还未被标记完成");
        }

        return response;
    }

    /**
     * Complete Product Api
     * @param request {
     *     "id": the product id: int
     * }
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
    public JSONObject complete(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long id = Optional.ofNullable(request.getLong("id"))
                .orElseThrow(() -> new MissingServletRequestParameterException("id", "int"));

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

    @GetMapping("/processes")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public JSONObject getProcesses(@RequestParam Long id) {
        JSONObject response = new JSONObject();

        Product product = productService.loadWithProcessesById(id);

        response.put("data", formatProcessesWithDetail(
                product.getWork().getWorkProcesses(),
                product.getProductProcesses()
        ));

        response.put("status", 1);
        response.put("message", "获取工序完成情况成");

        return response;
    }

    private JSONObject getAccordProducts(
            @NotNull Map<String, Object> likeMap, @NotNull Map<String, Date> dateGreaterMap,
            @NotNull Map<String, Date> dateLesserMap, boolean flag,
            int accord, int pageNumber, int pageSize
    ) {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneIdUtil.CST);
        ZonedDateTime today = localDateTime
                .toLocalDate()
                .atStartOfDay(ZoneIdUtil.CST)
                .plusDays(accord);

        Timestamp todayTimestamp = Timestamp.from(today.toInstant());
        Timestamp tomorrowTimestamp = Timestamp.from(today.plusDays(1).toInstant());

        if (flag) {
            dateGreaterMap.put("createTime", todayTimestamp);
            dateLesserMap.put("createTime", tomorrowTimestamp);
        } else {
            dateGreaterMap.put("endTime", todayTimestamp);
            dateLesserMap.put("endTime", tomorrowTimestamp);
        }

        return formatPage(productService.load(likeMap, dateGreaterMap, dateLesserMap, false, pageNumber, pageSize));
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
        data.put("igt", product.getIGT());
        data.put("erp", product.getERP());
        data.put("central", product.getCentral());
        data.put("area", product.getArea());
        data.put("design", product.getDesign());
        data.put("beginTime", product.getBeginTime());
        data.put("demandTime", product.getDemandTime());
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

            one.put("id", workProcess.getProcess().getId());
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

    private List<JSONObject> formatProcessesWithDetail(Set<WorkProcess> workProcesses, Set<ProductProcess> productProcesses) {
        List<JSONObject> processes = new ArrayList<>(workProcesses.size());

        for (WorkProcess workProcess : workProcesses) {
            JSONObject one = new JSONObject();

            one.put("id", workProcess.getProcess().getId());
            one.put("name", workProcess.getProcess().getName());
            one.put("sequence", workProcess.getSequenceNumber());

            one.put("complete", false);

            for (ProductProcess productProcess : productProcesses) {
                if (workProcess.getProcessId().equals(productProcess.getProcessId())) {
                    one.put("completeTime", productProcess.getFinishTime());
                    one.put("completeUserId", productProcess.getFinisher().getId());
                    one.put("completeUserName", productProcess.getFinisher().getName());
                    one.put("complete", true);
                    break;
                }
            }

            processes.add(one);
        }

        return processes;
    }
}
