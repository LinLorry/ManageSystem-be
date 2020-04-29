package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.*;
import com.dghysc.hy.util.TokenUtil;
import com.dghysc.hy.wechat.model.ScheduleMessageUser;
import com.dghysc.hy.wechat.model.WechatUser;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    private final ScheduleMessageUserService scheduleMessageUserService;

    public WechatController(
            TokenUtil tokenUtil, WechatServer wechatServer,
            WechatUserService wechatUserService,
            ScheduleMessageUserService scheduleMessageUserService) {
        this.tokenUtil = tokenUtil;
        this.wechatServer = wechatServer;
        this.wechatUserService = wechatUserService;
        this.scheduleMessageUserService = scheduleMessageUserService;
    }

    /**
     * Get Wechat User Api
     * @param id the wechat user id.
     * @param name the wechat user name.
     * @param pageNumber the page number.
     * @param pageSize the page size.
     * @return if id is provide {
     *     "status": 1,
     *     "message": message: str,
     *     "data": wechat user info: object
     * } else return {
     *     "status": 0,
     *     "message": message: str,
     *     "data": {
     *         "size": the page size: int,
     *         "total": the page total number: int,
     *         "wechatUsers": [ wechat user info...: object ]
     *     }
     * }
     */
    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
    public JSONObject getWechatUser(
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "20") int pageSize) {
        JSONObject response = new JSONObject();

        if (id != null) {
            try {
                response.put("data", wechatUserService.loadById(id));
                response.put("message", "获取微信用户详细信息成功");
            } catch (EntityNotFoundException e) {
                response.put("message", "Id为" + id + "的微信用户不存在");
            }
        } else {
            JSONObject data = new JSONObject();
            Map<String, Object> likeMap = new HashMap<>();
            Optional.ofNullable(name).ifPresent(n -> likeMap.put("name", n));

            Page<WechatUser> wechatUsers = wechatUserService.load(likeMap, pageNumber, pageSize);

            data.put("size", pageSize);
            data.put("total", wechatUsers.getTotalPages());
            data.put("wechatUsers", wechatUsers.getContent());

            response.put("data", data);
            response.put("message", "获取微信用户列表成功");
        }

        response.put("status", 1);

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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
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
        response.put("message", "提交个人信息成功");
        response.put("data", wechatUser);

        return response;
    }

    /**
     * Get Schedule Message User Api
     * @return {
     *     "status": 1,
     *     "message": "get schedule message users success",
     *     "data": [
     *          {
     *              "id": the user id: int,
     *              "wechatId": the wechat user id: int,
     *              "username": the user username: str,
     *              "name": the user name: str
     *          }
     *     ]
     * }
     */
    @GetMapping("/scheduleMessage")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject getScheduleMessageUser() {
        JSONObject response = new JSONObject();
        JSONArray data = new JSONArray();

        Iterable<ScheduleMessageUser> scheduleMessageUsers = scheduleMessageUserService.loadAll();
        scheduleMessageUsers.forEach(scheduleMessageUser -> {
            final JSONObject one = new JSONObject();
            one.put("wechatId", scheduleMessageUser.getId());
            Optional.ofNullable(scheduleMessageUser.getWechatUser())
                    .flatMap(wechatUser -> Optional.ofNullable(wechatUser.getUser()))
                    .ifPresentOrElse( user -> {
                        one.put("id", user.getId());
                        one.put("name", user.getName());
                        one.put("username", user.getUsername());
                    }, () -> {
                        one.put("id", "null");
                        one.put("name", "null");
                        one.put("username", "null");
                    });
            data.add(one);
        });

        response.put("status", 1);
        response.put("message", "get schedule message users success");
        response.put("data", data);

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
