package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.ProductProcessService;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.user.UserService;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.ZoneIdUtil;
import com.dghysc.hy.work.model.Process;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Worker Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/worker")
@PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
public class WorkerController {

    private final UserService userService;

    private final ProductProcessService productProcessService;

    public WorkerController(UserService userService, ProductProcessService productProcessService) {
        this.userService = userService;
        this.productProcessService = productProcessService;
    }

    /**
     * Get Worker Api
     * @param pageNumber the page number: int
     * @param pageSize the page size: int
     * @return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": {
     *         "size": the page size: int,
     *         "total": the page total number: int,
     *         "workers": [ user info: object... ]
     *     }
     * }
     */
    @GetMapping
    public JSONObject getWorkers(@RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "20") int pageSize) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();

        Page<User> users = userService.loadALLWorkers(pageNumber, pageSize);

        data.put("size", users.getSize());
        data.put("total", users.getTotalPages());
        data.put("workers", users.getContent());

        response.put("data", data);
        response.put("status", 1);
        response.put("message", "获取工人列表成功");

        return response;
    }

    /**
     * Update Worker Name Api
     * @param request {
     *      "id": the worker id: int,
     *      "name": the worker name: int
     * }
     * @return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": user info: object
     * }
     * @throws MissingServletRequestParameterException {@code id} or {@code name} is {@literal null}
     */
    @PostMapping
    public JSONObject updateWorker(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long id = Optional.ofNullable(request.getLong("id"))
                .orElseThrow(() -> new MissingServletRequestParameterException("id", "int"));
        String name = Optional.ofNullable(request.getString("name"))
                .orElseThrow(() -> new MissingServletRequestParameterException("name", "str"));

        try {
            User user = userService.loadWithRolesById(id);

            boolean isWorker = false;
            for (Role role : user.getAuthorities()) {
                if ("ROLE_WORKER".equals(role.getRole())) {
                    isWorker = true;
                    break;
                }
            }

            if (!isWorker) throw new EntityNotFoundException();

            response.put("data", userService.update(id, null, name, null));
            response.put("status", 1);
            response.put("message", "更新员工信息成功");
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为：" + id + "的员工不存在");
        }

        return response;
    }

    @GetMapping("/check")
    public JSONObject checkWorker(@RequestParam Long id) {
        JSONObject response = new JSONObject();

        response.put("data", userService.checkWorker(id));
        response.put("status", 1);
        response.put("message", "Check Success");

        return response;
    }

    /**
     * Set Or Unset User To Worker Api
     * @param request {
     *      "id": user id: int[must],
     *      "operation": true is set, false is unset: bool[must]
     * }
     * @return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": user info: object
     * }
     * @throws MissingServletRequestParameterException {@code id} or {@code operation} is {@literal null}
     */
    @PostMapping("/set")
    public JSONObject setWorker(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long id = Optional.ofNullable(request.getLong("id"))
                .orElseThrow(() -> new MissingServletRequestParameterException("id", "int"));
        Boolean set = Optional.ofNullable(request.getBoolean("operation")).orElseThrow(
                () -> new MissingServletRequestParameterException("operation", "bool"));

        try {
            if (set) {
                response.put("data", userService.enableWorker(id));
                response.put("message", "设置用户为员工成功");
            } else {
                response.put("data", userService.disableWorker(id));
                response.put("message", "取消用户员工身份成功");
            }
            response.put("status", 1);

        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为：" + id + "的用户不存在");
        }

        return response;
    }

    /**
     * Statistical Worker Finish Product Processes Api
     * @param mouth the mouth: timestamp
     * @return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": [
     *          {
     *              "id": finisher id: int,
     *              "name": finisher name: str,
     *              "finishList": [
     *                  {
     *                      "productId": the product id: int,
     *                      "serial": the product serial: str,
     *                      "processId": the process id: int,
     *                      "processName": the process name: str,
     *                      "finishTime": finish time: timestamp
     *                  },
     *                  ...
     *              ]
     *          },
     *          ...
     *     ]
     * }
     */
    @GetMapping("/statistical")
    public JSONObject statisticalWorkerFinishProductProcesses(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            @RequestParam Date mouth
    ) {
        JSONObject response = new JSONObject();
        JSONArray data = new JSONArray();
        Map<Long, JSONObject> workerFinishMap = new HashMap<>();

        ZonedDateTime zonedDateTime = mouth.toInstant().atZone(ZoneIdUtil.CST);

        Timestamp thisMouth = Timestamp.from(zonedDateTime.toInstant());
        Timestamp nextMouth = Timestamp.from(zonedDateTime.plusMonths(1).toInstant());

        List<ProductProcess> productProcesses = productProcessService.loadAllByFinishTimeAfterAndFinishTimeBefore(thisMouth, nextMouth);

        for (ProductProcess productProcess : productProcesses) {
            final Product product = productProcess.getProduct();
            final Process process = productProcess.getProcess();

            final User finisher = productProcess.getFinisher();
            final JSONObject finishInfo;
            final JSONArray finishList;
            final JSONObject one = new JSONObject();

            if (workerFinishMap.containsKey(finisher.getId())) {
                finishInfo = workerFinishMap.get(finisher.getId());
                finishList = finishInfo.getJSONArray("finishList");
            } else {
                finishInfo = new JSONObject();
                finishList = new JSONArray();

                data.add(finishInfo);

                workerFinishMap.put(finisher.getId(), finishInfo);

                finishInfo.put("id", finisher.getId());
                finishInfo.put("name", finisher.getName());
                finishInfo.put("finishList", finishList);
            }
            finishList.add(one);

            one.put("productId", product.getId());
            one.put("serial", product.getSerial());
            one.put("processId", process.getId());
            one.put("processName", process.getName());
            one.put("finishTime", productProcess.getFinishTime());
        }

        response.put("data", data);
        response.put("status", 1);
        response.put("message", "获取员工" + zonedDateTime.getMonth() + "月工作统计成功");

        return response;
    }
}
