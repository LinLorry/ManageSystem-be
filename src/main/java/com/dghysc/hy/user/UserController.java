package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.ProductService;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.TokenUtil;
import com.dghysc.hy.work.UserProcessService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * User Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final TokenUtil tokenUtil;

    private final UserService userService;

    private final UserProcessService userProcessService;

    private final ProductService productService;

    public UserController(
            TokenUtil tokenUtil, UserService userService,
            UserProcessService userProcessService, ProductService productService
    ) {
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.userProcessService = userProcessService;
        this.productService = productService;
    }

    /**
     * User Login, Get Token Api
     * @param request {
     *     "username": username: str,
     *     "password": password: str,
     * }
     * @return if login success return {
     *      "status": 1,
     *      "message": "登陆成功",
     *      "token": token: str
     * } else return {
     *      "status": 0,
     *      "message": message: str
     * }
     */
    @PostMapping("/token")
    public JSONObject token(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject result = new JSONObject();

        String username = request.getString("username");
        String password = request.getString("password");

        try {
            User user = userService.loadByUsername(username);
            if (userService.checkPassword(user, password)) {

                if (user.isEnabled()) {
                    result.put("status", 1);
                    result.put("message", "登陆成功");
                    result.put("token", tokenUtil.generateToken(user));
                } else {
                    result.put("status", 0);
                    result.put("message", "该用户已被禁用");
                }
            } else {
                result.put("status", 0);
                result.put("message", "密码错误");
            }
        } catch (EntityNotFoundException e) {
            result.put("status", 0);
            result.put("message", "The user does not exist.");
        } catch (NullPointerException e) {
            throw new MissingServletRequestParameterException("username", "str");
        }

        return result;
    }

    /**
     * Edit Password Api
     * @param request {
     *      "oldPassword": user old password: str,
     *      "newPassword": user new password: str
     * }
     * @return if edit password success return {
     *      "status": 1,
     *      "message": "更新用户秘密成功"
     * }
     */
    @PostMapping("/password")
    public JSONObject editPassword(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        String oldPassword = request.getString("oldPassword");
        if (userService.checkPassword(SecurityUtil.getUser(), oldPassword)) {
            String newPassword = Optional.of(request.getString("newPassword"))
                    .orElseThrow(() -> new MissingServletRequestParameterException("newPassword", "str"));

            userService.updatePassword(newPassword);
            response.put("status", 1);
            response.put("message", "更新密码成功");
        } else {
            response.put("status", 0);
            response.put("message", "原始秘密错误");
        }

        return response;
    }

    /**
     * Get User Self Profile Api
     * @return {
     *      "status": 1,
     *      "message": "Get profile success.",
     *      "data": {
     *          "id": user id: int,
     *          "username": username: str,
     *          "name": name: str
     *      }
     * }
     */
    @GetMapping("/profile")
    public JSONObject getProfile() {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        User user = SecurityUtil.getUser();

        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("name", user.getName());

        response.put("status", 1);
        response.put("message", "Get profile success.");
        response.put("data", data);

        return response;
    }

    @GetMapping("/dynamic")
    public JSONObject getDynamic() {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();
        boolean isAdmin = false;
        boolean isProductManager = false;
        boolean isWorkerManager = false;

        for (GrantedAuthority authority : SecurityUtil.getAuthorities()) {
            switch (authority.getAuthority()) {
                case "ROLE_ADMIN":
                    isAdmin = true; break;
                case "ROLE_PRODUCT_MANAGER":
                    isProductManager = true; break;
                case "ROLE_WORKER_MANAGER":
                    isWorkerManager = true; break;
            }
            if (isAdmin) break;
        }

        if (isAdmin || isProductManager) {
            data.put("product", getProductDynamic());
        }

        if (isAdmin || isWorkerManager) {
            data.put("worker", getWorkerDynamic());
        }

        response.put("status", 1);
        response.put("message", "获取今日动态信息成功");
        response.put("data", data);

        return response;
    }

    /**
     * Get Level Api
     * @return {
     *     "status": 1,
     *     "message": "获取权限信息成功",
     *     "data": [ level: str, ... ]
     * }
     */
    @GetMapping("/level")
    public JSONObject getLevel() {
        JSONObject response = new JSONObject();

        List<String> levelList = new ArrayList<>(SecurityUtil.getAuthorities().size());

        SecurityUtil.getAuthorities().forEach(authority -> {
            switch (authority.getAuthority()) {
                case "ROLE_ADMIN":
                    levelList.add("admin");
                    break;
                case "ROLE_WORKER_MANAGER":
                    levelList.add("worker manager");
                    break;
                case "ROLE_PRODUCT_MANAGER":
                    levelList.add("product manager");
                    break;
                case "ROLE_WORKER":
                    levelList.add("worker");
            }
        });


        response.put("status", 1);
        response.put("message", "获取权限信息成功");
        response.put("data", levelList);

        return response;
    }

    /**
     * Get User Processes Api
     * @return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": [
     *          process info...: object
     *     ]
     * }
     */
    @GetMapping("/processes")
    public JSONObject getProcesses() {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "获取工序成功");
        response.put("data", userProcessService.loadByUserId(SecurityUtil.getUserId()));

        return response;
    }

    private JSONObject getProductDynamic() {
        JSONObject productInfo = new JSONObject();

        productInfo.put("start", productService.countStart());
        productInfo.put("notStart", productService.countNotStart());
        productInfo.put("canComplete", productService.countCanComplete());
        productInfo.put("created", productService.countCreateProductDuringTheMonth());

        return productInfo;
    }

    public JSONObject getWorkerDynamic() {
        JSONObject workerInfo = new JSONObject();
        JSONArray finishInfo = new JSONArray();
        JSONArray finisherInfo = new JSONArray();

        Map<Long, List<ProductProcess>> finisherProductProcessesMap = new HashMap<>();
        List<ProductProcess> productProcesses = userProcessService.loadAllTodayFinish();

        productProcesses.forEach(productProcess -> {
            JSONObject one = new JSONObject();
            Product product = productProcess.getProduct();

            one.put("finisher", productProcess.getFinisher().getName());
            one.put("finishTime", productProcess.getFinishTime());
            one.put("productSerial", product.getSerial());
            one.put("productId", product.getId());
            one.put("processName", productProcess.getProcess().getName());

            finishInfo.add(one);
        });

        productProcesses.forEach(productProcess ->
                finisherProductProcessesMap.compute(
                        productProcess.getFinisher().getId(),
                        (id, oldValue) -> {
                            List<ProductProcess> list = Optional.ofNullable(oldValue).orElse(new ArrayList<>());
                            list.add(productProcess);
                            return list;
                        })
        );

        finisherProductProcessesMap.forEach((id, list) -> {
            JSONObject one = new JSONObject();

            one.put("size", list.size());
            one.put("name", list.get(0).getFinisher().getName());

            finisherInfo.add(one);
        });

        workerInfo.put("finishInfo", finishInfo);
        workerInfo.put("finisherInfo", finisherInfo);

        return workerInfo;
    }
}
