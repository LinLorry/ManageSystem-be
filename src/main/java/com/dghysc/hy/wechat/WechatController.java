package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.*;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TokenUtil;
import com.dghysc.hy.wechat.model.WechatUser;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * The Wechat Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/wechat")
public class WechatController {

    private final TokenUtil tokenUtil;

    private final WechatServer wechatServer;

    private final WechatUserService wechatUserService;

    public WechatController(
            TokenUtil tokenUtil, WechatServer wechatServer,
            WechatUserService wechatUserService
    ) {
        this.tokenUtil = tokenUtil;
        this.wechatServer = wechatServer;
        this.wechatUserService = wechatUserService;
    }

    @GetMapping
    public JSONObject getWechatUser(
            @RequestParam(required = false) String id,
            @RequestParam(required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        JSONObject response = new JSONObject();

        if (id != null) {
            try {
                response.put("data", wechatUserService.loadById(id));
                response.put("status", 1);
                response.put("message", "获取微信用户详细信息成功");
            } catch (EntityNotFoundException e) {
                response.put("status", 0);
                response.put("message", "Id为" + id + "的微信用户不存在");
            }
        } else {
            JSONObject data = new JSONObject();

            Page<WechatUser> wechatUsers = wechatUserService.loadAll(pageNumber, pageSize);

            data.put("size", pageSize);
            data.put("total", wechatUsers.getTotalPages());
            data.put("wechatUsers", wechatUsers.getContent());

            response.put("data", data);
            response.put("message", "获取微信用户列表成功");
            response.put("status", 1);
        }

        return response;
    }

    @PostMapping
    public JSONObject addOrUpdateWechatUser(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();
        String id = Optional.ofNullable(request.getString("id"))
                .orElseThrow(() -> new MissingServletRequestParameterException("id", "str"));
        Long userId = request.getLong("userId");

        User user = new User();
        user.setId(userId);

        response.put("status", 1);

        try {
            response.put("data", wechatUserService.addOrUpdateUser(id, user));
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的微信用户不存在");
        } catch (DuplicateUserException e) {
            response.put("status", 0);
            response.put("message", "Id为" + userId + "的用户已经被绑定");
        } catch (UserNoFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为" + userId + "的用户不存在");
        }

        return response;
    }

    @GetMapping("/refreshToken")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject refreshToken() throws WechatServiceDownException {
        JSONObject response = new JSONObject();

        try {
            wechatServer.refreshToken();

            response.put("status", 1);
            response.put("message", "Refresh wechat access token success.");
            response.put("data", wechatServer.loadToken());
        } catch (WechatConfigWrongException e) {
            response.put("status", 0);
            response.put("message", e.getMessage());
        }

        return response;
    }

    @GetMapping("/login")
    public JSONObject login(@RequestParam(name = "code") String code)
            throws WechatServiceDownException, WechatUserCodeWrongException {
        JSONObject response = new JSONObject();

        WechatUser wechatUser = wechatUserService.loadByCode(code);
        response.put("status", 1);
        response.put("data", wechatUser);

        if (wechatUser.getUser() == null) {
            response.put("message", "还未注册");
        } else {
            response.put("message", "登陆成功");
            response.put("token", tokenUtil.generateToken(wechatUser.getUser()));
        }

        return response;
    }

    @GetMapping("/info/{name}")
    public JSONObject submit(@PathVariable String name,
                             @RequestParam(name = "code") String code)
            throws WechatServiceDownException, WechatUserCodeWrongException {
        JSONObject response = new JSONObject();
        WechatUser wechatUser = wechatUserService.loadByCode(code);

        wechatUser = wechatUserService.update(wechatUser.getId(), name);

        response.put("status", 1);
        response.put("message", "获取个人信息成功");
        response.put("data", wechatUser);

        return response;
    }

    @ExceptionHandler(value = WechatServiceDownException.class)
    public void wechatServiceDownExceptionHandler(HttpServletResponse response)
            throws IOException {
        response.sendError(0, "微信服务异常，请稍后重试");
    }

    @ExceptionHandler(value = WechatUserCodeWrongException.class)
    public JSONObject wechatUserCodeWrongExceptionHandler() {
        JSONObject response = new JSONObject();
        response.put("status", 0);
        response.put("message", "Code invalid");
        return response;
    }
}
