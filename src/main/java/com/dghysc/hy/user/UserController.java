package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.until.SecurityUtil;
import com.dghysc.hy.until.TokenUtil;
import com.dghysc.hy.user.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final TokenUtil tokenUtil;

    private final UserService userService;

    public UserController(UserService userService, TokenUtil tokenUtil) {
        this.userService = userService;
        this.tokenUtil = tokenUtil;
    }

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
            response.put("message", "Registry Success");
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

    @ResponseBody
    @PostMapping("/token")
    public JSONObject token(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();

        String username = json.getString("username");
        String password = json.getString("password");

        try {
            User user = userService.loadUserByUsername(username);
            if (userService.checkPassword(user, password)) {
                result.put("status", 1);
                result.put("message", "Login success");
                result.put("token", tokenUtil.generateToken(user));
            } else {
                result.put("status", 0);
                result.put("message", "Wrong password.");
            }
        } catch (UsernameNotFoundException e) {
            result.put("status", 0);
            result.put("message", "The user does not exist.");
        }

        return result;
    }

    @ResponseBody
    @GetMapping("/profile")
    public JSONObject getProfile() {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Get profile success.");
        response.put("data", SecurityUtil.getUser());

        return response;
    }

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
}
