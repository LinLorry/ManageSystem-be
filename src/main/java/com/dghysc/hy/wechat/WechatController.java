package com.dghysc.hy.wechat;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.WechatConfigWrongException;
import com.dghysc.hy.exception.WechatServiceDownException;
import com.dghysc.hy.exception.WechatUserCodeWrongException;
import com.dghysc.hy.util.TokenUtil;
import com.dghysc.hy.wechat.model.WechatUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The Wechat Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/wechat")
public class WechatController {

    private final String loginBaseUrl;

    private final String infoBaseUrl;

    private final String successUrl;

    private final TokenUtil tokenUtil;

    private final WechatServer wechatServer;

    private final WechatUserService wechatUserService;

    public WechatController(
            @Value("${manage.loginUrl}") String loginBaseUrl,
            @Value("${manage.wechat.infoUrl}") String infoBaseUrl,
            @Value("${manage.wechat.successUrl}") String successUrl,
            TokenUtil tokenUtil, WechatServer wechatServer,
            WechatUserService wechatUserService
    ) {
        this.loginBaseUrl = loginBaseUrl;
        this.infoBaseUrl = infoBaseUrl;
        this.successUrl = successUrl;
        this.tokenUtil = tokenUtil;
        this.wechatServer = wechatServer;
        this.wechatUserService = wechatUserService;
    }

    @GetMapping("/refreshToken")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject refreshToken() {
        JSONObject response = new JSONObject();

        try {
            wechatServer.refreshToken();

            response.put("status", 1);
            response.put("message", "Refresh wechat access token success.");
            response.put("data", wechatServer.loadToken());
        } catch (WechatServiceDownException | WechatConfigWrongException e) {
            response.put("status", 0);
            response.put("message", e.getMessage());
        }

        return response;
    }

    @GetMapping("/login/")
    public void login(@RequestParam(name = "code") String code,
                      HttpServletResponse response)
            throws IOException {
        try {
            WechatUser wechatUser = wechatUserService.loadByCode(code);

            if (wechatUser.getUser() == null) {
                response.sendRedirect(infoBaseUrl);
            } else {
                response.sendRedirect(
                        loginBaseUrl + "?token=" + tokenUtil.generateToken(wechatUser.getUser())
                );
            }
        } catch (WechatServiceDownException e) {
            response.sendError(0, "微信服务异常，请稍后重试");
        } catch (WechatUserCodeWrongException e) {
            response.sendError(0, "Code invalid.");
        }
    }

    @GetMapping("/info/{name}/")
    public void submit(@PathVariable String name,
                       @RequestParam(name = "code") String code,
                       HttpServletResponse response) throws IOException {
        try {
            WechatUser wechatUser = wechatUserService.loadByCode(code);

            wechatUser = wechatUserService.update(wechatUser.getId(), name);

            response.sendRedirect(successUrl + "?name=" + wechatUser.getName());
        } catch (WechatServiceDownException e) {
            response.sendError(0, "微信服务异常，请稍后重试");
        } catch (WechatUserCodeWrongException e) {
            response.sendError(0, "Code invalid.");
        }
    }
}
