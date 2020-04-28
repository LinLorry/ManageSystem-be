package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * Admin Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
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
    @PostMapping("/user")
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
    @GetMapping("/user")
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
    @PostMapping("/user/password")
    public JSONObject editPassword(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long id = Optional.ofNullable(request.getLong("id"))
                .orElseThrow(() -> new MissingServletRequestParameterException("id", "int"));

        String password = Optional.of(request.getString("password"))
                .orElseThrow(() -> new MissingServletRequestParameterException("password", "str"));
        try {
            userService.updatePassword(id, password);

            response.put("status", 1);
            response.put("message", "更新用户秘密成功");
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为：" + id + "的用户不存在");
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
    @GetMapping("/user/authority")
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
    @PostMapping("/user/disable")
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
}
