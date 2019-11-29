package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.until.TokenUtil;
import com.dghysc.hy.user.model.User;
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
        JSONObject result = new JSONObject();

        String username = request.getString("username");
        String name = request.getString("name");
        String password = request.getString("password");

        User user = new User();

        user.setUsername(username);
        user.setName(name);
        user.setPassword(password);

        if (userService.checkUsername(username)) {
            result.put("status", 0);
            result.put("message", "Username exist.");
        } else if (userService.addUser(user)) {
            result.put("status", 1);
            result.put("message", "registry success");
        } else {
            result.put("status", 0);
            result.put("message", "registry failed");
        }
        return result;
    }

    @ResponseBody
    @PostMapping("/token")
    public JSONObject login(@RequestBody JSONObject json) {

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

}
