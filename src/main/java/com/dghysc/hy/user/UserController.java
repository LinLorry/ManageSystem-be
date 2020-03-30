package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.product.ProductService;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.TokenUtil;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.work.UserProcessService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * Create Or Update User Api
     * @param request {
     *     "id": update user id: int,
     *     "username": the user username: str,
     *     "password": create user password: str,
     *     "name": the user name: str,
     * }
     * @return if create or update success return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": user info: object
     * }
     * @throws MissingServletRequestParameterException when create user
     *      {@code username} or {@code password} is {@literal null}
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject createOrUpdate(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long id = request.getLong("id");
        String username = request.getString("username");
        String password = request.getString("password");
        String name = request.getString("name");

        try {
            if (id == null) {
                response.put("data", userService.add(username, password, name));
                response.put("message", "创建用户成功");
            } else {
                JSONArray tmp = request.getJSONArray("roles");
                List<Integer> roleIds = tmp == null ? null : tmp.toJavaList(Integer.TYPE);
                response.put("data", userService.update(id, username, name, roleIds));
                response.put("message", "更新用户成功");
            }
            response.put("status", 1);
        } catch (NullPointerException e) {
            if (username == null) {
                throw new MissingServletRequestParameterException("username", "str");
            } else if (password == null) {
                throw new MissingServletRequestParameterException("password", "str");
            }
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "用户或权限不存在");
        } catch (DataIntegrityViolationException e) {
            if (userService.checkByUsername(username)) {
                response.put("status", 0);
                response.put("message", "用户名为" + username + "的用户已存在");
            } else {
                throw e;
            }
        }

        return response;
    }

    /**
     * Get Users Api
     * @param id the user id.
     * @param name the user name.
     * @param pageNumber the page number.
     * @param pageSize the page size.
     * @return if id provide {
     *     "status": 1,
     *     "message": message: str,
     *     "data": user info: object
     * } else return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": {
     *         "total": page total number: int,
     *         "users": [ user info...: object ]
     *     }
     * }
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject get(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        JSONObject response = new JSONObject();

        if (id == null) {
            JSONObject data = new JSONObject();
            Map<String, Object> likeMap = new HashMap<>();
            Optional.ofNullable(name).ifPresent(n -> likeMap.put("name", n));

            Page<User> users = userService.load(likeMap, pageNumber, pageSize);

            data.put("total", users.getTotalPages());
            data.put("size", users.getSize());
            data.put("users", users.getContent());
            response.put("data", data);
            response.put("message", "获取用户列表成功");
        } else {
            try {
                response.put("data", userService.loadById(id));
                response.put("message", "获取用户详情成功");
            } catch (EntityNotFoundException e) {
                response.put("message", "Id为" + id + "的用户不存在");
            }
        }

        response.put("status", 1);

        return response;
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
     * Edit Password Api
     * @param request if admin change other user password {
     *      "id": the user id: int,
     *      "password": new password: str
     * } else if user change his password {
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

        Long id = request.getLong("id");

        if (id == null) {
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
        } else {
            String password = Optional.of(request.getString("password"))
                    .orElseThrow(() -> new MissingServletRequestParameterException("password", "str"));
            try {
                userService.updatePassword(id, password);
                response.put("status", 1);
                response.put("message", "更新用户秘密成功");
            } catch (NullPointerException e) {
                throw new MissingServletRequestParameterException("id", "int");
            } catch (EntityNotFoundException e) {
                response.put("status", 0);
                response.put("message", "Id为：" + id + "的用户不存在");
            }
        }

        return response;
    }

    /**
     * Get User Authority Api
     * @param id the user id.
     * @return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": [ role info: object ]
     * }
     */
    @GetMapping("/authority")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject getAuthority(@RequestParam Long id) {
        JSONObject response = new JSONObject();

        try {
            response.put("status", 1);
            response.put("message", "获取用户权限信息成功");
            response.put("data", userService.loadWithRolesById(id).getAuthorities());
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为：" + id + "的用户不存在");
        }

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

    /**
     * Disable or Enable User Api
     * @param request {
     *     "id": the user id: long[must],
     *     "operation": true is disable, false is enable: bool[must]
     * }
     * @return {
     *     "status": 1,
     *     "message": message: str
     * }
     * @throws MissingServletRequestParameterException id or operation not exist.
     */
    @PostMapping("/disable")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
    public JSONObject disableUser(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long id = Optional.ofNullable(request.getLong("id")).orElseThrow(
                () -> new MissingServletRequestParameterException("id", "int"));
        Boolean disable = Optional.ofNullable(request.getBoolean("operation")).orElseThrow(
                () -> new MissingServletRequestParameterException("operation", "bool"));

        if (disable) {
            userService.disable(id);
            response.put("message", "禁用用户成功");
        } else {
            userService.enable(id);
            response.put("message", "解禁用户成功");
        }

        response.put("status", 1);

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
