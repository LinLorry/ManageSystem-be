package com.dghysc.hy.wechat;

import com.dghysc.hy.util.TokenUtil;
import com.dghysc.hy.wechat.model.WechatUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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

    private final WechatUserService wechatUserService;

    public WechatController(
            @Value("${manage.loginUrl}") String loginBaseUrl,
            @Value("${manage.wechat.infoUrl}") String infoBaseUrl,
            @Value("${manage.wechat.successUrl}") String successUrl,
            TokenUtil tokenUtil, WechatUserService wechatUserService
    ) {
        this.loginBaseUrl = loginBaseUrl;
        this.infoBaseUrl = infoBaseUrl;
        this.successUrl = successUrl;
        this.tokenUtil = tokenUtil;
        this.wechatUserService = wechatUserService;
    }

    @GetMapping("/login/")
    public void login(@RequestParam(name = "code") String code,
                        HttpServletResponse response)
            throws Exception {
        WechatUser wechatUser = wechatUserService.loadByCode(code);

        if (wechatUser.getUser() == null) {
            response.sendRedirect(infoBaseUrl);
        } else {
            response.sendRedirect(
                    loginBaseUrl + "?token=" + tokenUtil.generateToken(wechatUser.getUser())
            );
        }
    }

    @GetMapping("/info/{name}/")
    public void submit(@PathVariable String name,
                       @RequestParam(name = "code") String code,
                       HttpServletResponse response) throws Exception {
        WechatUser wechatUser = wechatUserService.loadByCode(code);

        wechatUser = wechatUserService.update(wechatUser.getId(), name);

        response.sendRedirect(successUrl + "?name=" + wechatUser.getName());
    }
}
