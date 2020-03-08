package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.*;
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

    @GetMapping("/user")
    @PreAuthorize("hasRole('ADMIN')")
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

    /**
     * Update Wechat User Api
     * @param request {
     *     "id": the wechat user id: str[not null]
     * }
     * @return if success return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": wechat user data: object
     * } else return {
     *     "status": 0,
     *     "message": error message: str
     * }
     * @throws MissingServletRequestParameterException if id is null.
     */
    @PostMapping("/user/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject enableWechatUser(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();
        String id = Optional.ofNullable(request.getString("id"))
                .orElseThrow(() -> new MissingServletRequestParameterException("id", "str"));

        try {
            response.put("data", wechatUserService.addUser(id));
            response.put("status", 1);
            response.put("message", "更新微信用户成功");
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的微信用户不存在");
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
        } else if (wechatUser.getUser().isEnabled()) {
            response.put("message", "登陆成功");
            response.put("token", tokenUtil.generateToken(wechatUser.getUser()));
        } else {
            response.put("message", "被禁用");
        }

        return response;
    }

    @PostMapping("/info")
    public JSONObject submit(@RequestBody JSONObject request)
            throws WechatServiceDownException, WechatUserCodeWrongException,
            WechatRefreshTokenExpireException, MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        String code = Optional.ofNullable(request.getString("code"))
                .orElseThrow(() -> new MissingServletRequestParameterException("code", "str"));
        String name = Optional.ofNullable(request.getString("name"))
                .orElseThrow(() -> new MissingServletRequestParameterException("name", "str"));

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
