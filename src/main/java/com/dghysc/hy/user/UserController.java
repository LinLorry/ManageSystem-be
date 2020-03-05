package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.TokenUtil;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.work.UserProcessService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * User Controller
 *
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final TokenUtil tokenUtil;

    private final UserService userService;

    private final UserProcessService userProcessService;

    public UserController(TokenUtil tokenUtil, UserService userService,
                          UserProcessService userProcessService) {
        this.tokenUtil = tokenUtil;
        this.userService = userService;
        this.userProcessService = userProcessService;
    }

    /**
     * Registry Api
     *
     * @param request {
     *                "username": username: String,
     *                "password": password: String,
     *                "name": name: String
     *                }
     * @return if registry success return {
     * "status": 1,
     * "message": "Registry Success.",
     * "data": {
     * "id": user id: BigInteger,
     * "username": username: String,
     * "name": name: String
     * }
     * } else return {
     * "status: 0,
     * "message": message: String
     * }
     */
    @ResponseBody
    @PostMapping("/registry")
    public JSONObject registry(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        String username = request.getString("username");
        String name = request.getString("name");
        String password = request.getString("password");

        User user = new User();

        user.setUsername(username);
        user.setName(name);
        user.setPassword(password);

        try {
            response.put("data", userService.add(user));
            response.put("status", 1);
            response.put("message", "Registry Success.");
        } catch (DataIntegrityViolationException e) {
            if (userService.checkByUsername(username)) {
                response.put("status", 0);
                response.put("message", "Registry failed: username exits");
            } else {
                throw e;
            }
        }

        return response;
    }

    /**
     * User Login, Get Token Api
     *
     * @param request {
     *                "username": username: String,
     *                "password": password: String,
     *                }
     * @return if login success return {
     * "status": 1,
     * "message": "Login success.",
     * "token": token: String
     * } else return {
     * "status": 0,
     * "message": message: String
     * }
     */
    @ResponseBody
    @PostMapping("/token")
    public JSONObject token(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();

        String username = request.getString("username");
        String password = request.getString("password");

        try {
            User user = userService.loadByUsername(username);
            if (userService.checkPassword(user, password)) {

                if (user.isEnabled()) {
                    result.put("status", 1);
                    result.put("message", "Login success.");
                    result.put("token", tokenUtil.generateToken(user));
                } else {
                    result.put("status", 0);
                    result.put("message", "该用户已被禁用");
                }
            } else {
                result.put("status", 0);
                result.put("message", "Wrong password.");
            }
        } catch (EntityNotFoundException e) {
            result.put("status", 0);
            result.put("message", "The user does not exist.");
        }

        return result;
    }

    /**
     * Get User Self Profile Api
     *
     * @return {
     * "status": 1,
     * "message": "Get profile success.",
     * "data": {
     * "id": user id: BigInteger,
     * "username": username: String,
     * "name": name: String
     * }
     * }
     */
    @ResponseBody
    @GetMapping("/profile")
    public JSONObject getProfile() {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Get profile success.");
        response.put("data", SecurityUtil.getUser());

        return response;
    }

    /**
     * Update User Profile Api
     *
     * @param request {
     *                "name": name: String
     *                }
     * @return if update user profile success return {
     * "status": 1,
     * "message": "Update profile success.",
     * "data": {
     * "id": user id: BigInteger
     * "username": username: String,
     * "name": name: String
     * }
     * } else return {
     * "status": 0,
     * "message": "Update profile failed."
     * }
     */
    @ResponseBody
    @PostMapping("/profile")
    public JSONObject editProfile(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        String name = request.getString("name");
        User user = userService.loadById(SecurityUtil.getUserId());
        user.setName(name);

        try {
            response.put("data", userService.update(user));
            response.put("status", 1);
            response.put("message", "Update profile success.");
        } catch (Exception e) {
            response.put("status", 0);
            response.put("message", "Update profile failed.");
        }

        return response;
    }

    /**
     * Edit Password Api
     *
     * @param request {
     *                "oldPassword": oldPassword: String,
     *                "newPassword": newPassword: String
     *                }
     * @return if edit password success return {
     * "status": 1,
     * "message": "Edit Password Success"
     * } else if old password wrong return {
     * "status": 0,
     * "message": "Old Password Wrong"
     * } else return {
     * "status": 0,
     * "message": "Edit Password Failed"
     * }
     */
    @ResponseBody
    @PostMapping("/password")
    public JSONObject editPassword(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        String oldPassword = request.getString("oldPassword");
        String newPassword = request.getString("newPassword");

        User user = userService.loadById(SecurityUtil.getUserId());

        if (userService.checkPassword(user, oldPassword)) {
            user.setPassword(newPassword);
            userService.updatePassword(user);
            response.put("status", 1);
            response.put("message", "Edit Password Success");
        } else {
            response.put("status", 0);
            response.put("message", "Old Password Wrong");
        }

        return response;
    }

    /**
     * Judge Admin Api.
     *
     * @return if user is admin return {
     * "status": 1,
     * "message": "Get success.",
     * "data": true
     * } else return {
     * "status": 1,
     * "message": "Get success.",
     * "data": false
     * }
     */
    @ResponseBody
    @GetMapping("/isAdmin")
    public JSONObject isAdmin() {
        JSONObject response = new JSONObject();

        if (SecurityUtil.getUser().getAuthorities().size() == 0) {
            response.put("data", false);
        } else {
            response.put("data", true);
        }

        response.put("status", 1);
        response.put("message", "Get success.");

        return response;
    }

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
    @PreAuthorize("hasRole('ADMIN')")
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
