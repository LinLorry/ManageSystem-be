package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.User;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseBody
    @PostMapping("/registry")
    public JSONObject registry(@RequestBody User user) {
        JSONObject result = new JSONObject();

        if (userService.getUser(user.getUsername()) != null) {
            result.put("status", 0);
            result.put("message", "Username exist.");
        } else if (userService.addUser(user.getUsername(), user.getName(), user.getPassword())) {
            result.put("status", 1);
            result.put("message", "registry success");
        } else {
            result.put("status", 0);
            result.put("message", "registry failed");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/login")
    public JSONObject login(@RequestBody JSONObject json) {
        JSONObject result = new JSONObject();

        String username = json.getString("username");
        String password = json.getString("password");

        User user = userService.getUser(username);
        if (user != null) {
            if (userService.checkPassword(user, password)) {
                result.put("status", 1);
                result.put("message", "Login success");
            } else {
                result.put("status", 0);
                result.put("message", "Wrong password.");
            }
        } else {
            result.put("status", 0);
            result.put("message", "The user does not exist.");
        }

        return result;
    }

}
