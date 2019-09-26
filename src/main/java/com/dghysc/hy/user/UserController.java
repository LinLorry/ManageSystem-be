package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

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
}
