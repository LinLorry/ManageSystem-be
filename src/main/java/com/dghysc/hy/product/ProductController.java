package com.dghysc.hy.product;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.model.WorkProcess;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.sql.Timestamp;
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

    private final static Map<String, PutValueInJSONObject> fieldActionMap = new HashMap<>();

    static {
        // TODO special field.
        fieldActionMap.put("serial", (json, cell) -> json.put("serial", cell.getStringCellValue()));
        fieldActionMap.put("IGT", (json, cell) -> json.put("IGT", cell.getStringCellValue()));
        fieldActionMap.put("ERP", (json, cell) -> json.put("ERP", cell.getStringCellValue()));
        fieldActionMap.put("central", (json, cell) -> json.put("central", cell.toString()));
        fieldActionMap.put("area", (json, cell) -> json.put("area", cell.getStringCellValue()));
        fieldActionMap.put("design", (json, cell) -> json.put("design", cell.getStringCellValue()));
        fieldActionMap.put("beginTime", (json, cell) -> json.put("beginTime", cell.getDateCellValue()));
        fieldActionMap.put("demandTime", (json, cell) -> json.put("demandTime", cell.getDateCellValue()));
        fieldActionMap.put("endTime", (json, cell) -> json.put("endTime", cell.getDateCellValue()));
    }

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
     * Analysis Excel Api
     * @param file the excel file.
     * @return {
     *     "status": 1 is success, 0 is error,
     *     "message": str,
     *     "data": [
     *          analysis result: object, contains file provide data
     *          ...
     *     ]: only on success.
     * }
     */
    @PostMapping("/analysisExcel")
    public JSONObject analysisExcel(@RequestParam("file") MultipartFile file) {
        JSONObject response = new JSONObject();
        JSONArray data = new JSONArray();
        Workbook workbook;

        String fileName = file.getOriginalFilename();
        String extension = null;

        if (fileName == null) {
            response.put("status", 0);
            response.put("message", "上传文件有误。");
            return response;
        }

        int index = fileName.lastIndexOf('.');
        if (index > 0) {
            extension = fileName.substring(index+1);
        }

        try {
            if ("xls".equals(extension)) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if ("xlsx".equals(extension)) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else {
                response.put("status", 0);
                response.put("message", "上传文件格式不正确，仅支持.xls和.xlsx格式的文件。");
                return response;
            }
        } catch (IOException e) {
            response.put("status", 0);
            response.put("message", "服务出现错误，请重试！");
            return response;
        }

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        if (!rowIterator.hasNext()) {
            response.put("status", 0);
            response.put("message", "没有数据。");
            return response;
        }

        Row field = rowIterator.next();
        Map<Integer, PutValueInJSONObject> locationActionMap = new HashMap<>();
        for (int i = 0; locationActionMap.size() != fieldActionMap.size() &&
                i < field.getPhysicalNumberOfCells() + 5; ++i) {
            final int finalI = i;
            Optional.ofNullable(field.getCell(i))
                    .flatMap(cell -> Optional.ofNullable(cell.getStringCellValue()))
                    .flatMap(str -> Optional.ofNullable(fieldActionMap.get(str)))
                    .ifPresent(action -> locationActionMap.put(finalI, action));
        }

        if (locationActionMap.size() == 0) {
            response.put("status", 0);
            response.put("message", "提供的数据不足或命名不规范，请调整后再尝试导入！");
            return response;
        }

        while (rowIterator.hasNext()) {
            final Row one = rowIterator.next();
            final JSONObject productInfo = new JSONObject();

            locationActionMap.forEach((key, value) ->
                    Optional.ofNullable(one.getCell(key))
                            .ifPresent(cell -> value.put(productInfo, cell)));

            data.add(productInfo);
        }

        response.put("status", 1);
        response.put("message", "解析数据成功");
        response.put("data", data);

        return response;
    }

    /**
     * Get Product Api
     * @param id product id, only get one product if provide this.
     * @param serial serial the serial product contains.
     * @param withProcesses get product with processes, only when get one product.
     * @param complete return the complete products.
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
     *         "pageSize: page size,
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
            @RequestParam(required = false) Date createTimeAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date createTimeBefore,
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
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date completeTimeAfter,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam(required = false) Date completeTimeBefore,
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

            setDateMap(createTimeAfter, "createTime", dateGreaterMap);
            setDateMap(createTimeBefore, "createTime", dateLesserMap);
            setDateMap(beginTimeAfter, "beginTime", dateGreaterMap);
            setDateMap(beginTimeBefore, "beginTime", dateLesserMap);
            setDateMap(demandTimeAfter, "demandTime", dateGreaterMap);
            setDateMap(demandTimeBefore, "demandTime", dateLesserMap);
            setDateMap(endTimeAfter, "endTime", dateGreaterMap);
            setDateMap(endTimeBefore, "endTime", dateLesserMap);
            setDateMap(completeTimeAfter, "completeTime", dateGreaterMap);
            setDateMap(completeTimeBefore, "completeTime", dateLesserMap);

            response.put("data", formatPage(productService.load(
                    likeMap, dateGreaterMap, dateLesserMap, complete, pageNumber, pageSize
            )));

            response.put("message", "获取订单成功");
        }

        response.put("status", 1);

        return response;
    }

    /**
     * Get Complete Processes Api
     * @param pageNumber the page number.
     * @param pageSize the page size.
     * @return {
     *     "status": 1,
     *     "message": "获取最近完成工序信息成功",
     *     "data": {
     *         "total": total page number,
     *         "pageSize: page size,
     *         "productProcesses": [
     *              {
     *                  "productId": product id: int,
     *                  "processId": process id: int,
     *                  "finisherId": finisher id: int,
     *                  "serial": product serial: str,
     *                  "finishTime": finish time: timestamp,
     *                  "processName": process name: str,
     *                  "finisherName": finisher name: str
     *              },
     *              ...
     *         ]
     *     }
     * }
     */
    @GetMapping("/completeProcess")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER', 'WORKER_MANAGER')")
    public JSONObject getCompleteProcesses(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        JSONArray productProcesses = new JSONArray();

        Page<ProductProcess> page = productService
                .loadProductProcesses(pageNumber, pageSize);

        data.put("total", page.getTotalPages());
        data.put("pageSize", page.getSize());
        data.put("productProcesses", productProcesses);

        for (ProductProcess productProcess : page) {
            final Product product = productProcess.getProduct();
            final Process process = productProcess.getProcess();
            final User finisher = productProcess.getFinisher();

            JSONObject one = new JSONObject();

            one.put("productId", product.getId());
            one.put("processId", process.getId());
            one.put("finisherId", finisher.getId());

            one.put("serial", product.getSerial());
            one.put("finishTime", productProcess.getFinishTime());
            one.put("processName", process.getName());
            one.put("finisherName", finisher.getName());

            productProcesses.add(one);
        }

        response.put("status", 1);
        response.put("message", "获取最近完成工序信息成功");
        response.put("data", data);

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
     *     "message": message: str,
     *     "data": {
     *         "completeUserName": complete user name: str,
     *         "completeTime": complete time: time
     *     }
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
            JSONObject data = new JSONObject();

            data.put("completeUserName", SecurityUtil.getUser().getName());
            data.put("completeTime", new Timestamp(System.currentTimeMillis()));

            response.put("status", 1);
            response.put("message", "完成成功");
            response.put("data", data);
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
            productService.complete(id);
            response.put("status", 1);
            response.put("message", "完成订单成功");
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

    @FunctionalInterface
    private interface PutValueInJSONObject {
        void put(JSONObject json, Cell value);
    }

    private static void setDateMap(@Nullable Date date, @NotNull String key,
                                   @NotNull Map<String, Date> dateMap) {
        Optional.ofNullable(date).ifPresent(d -> dateMap.put(key, date));
    }

    private <T> JSONObject formatPage(Page<T> page) {
        JSONObject data = new JSONObject();

        data.put("total", page.getTotalPages());
        data.put("pageSize", page.getSize());
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
