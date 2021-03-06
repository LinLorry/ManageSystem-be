package com.dghysc.hy.security;

import com.dghysc.hy.util.TokenUtil;
import com.dghysc.hy.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The Authentication Filter
 * set authentication by token.
 * @author lorry
 * @author lin864464995@163.com
 */
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Value("${manage.authentication.name}")
    private String AuthenticationName;

    private final TokenUtil tokenUtil;

    private final UserService userService;

    public AuthenticationFilter(TokenUtil tokenUtil, UserService userService) {
        this.tokenUtil = tokenUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        final String requestTokenHeader = httpServletRequest.getHeader("Authorization");

        if (requestTokenHeader != null && requestTokenHeader.startsWith(AuthenticationName + " ")) {
            String token = requestTokenHeader.substring(AuthenticationName.length() + 1);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                try {
                    SecurityContextHolder.getContext().setAuthentication(
                            userService.getAuthentication(tokenUtil.getUserIdFromToken(token))
                    );
                } catch (ExpiredJwtException e) {
                    logger.warn("JWT Token has expired");
                } catch (SignatureException e) {
                    logger.error("Signature Exception");
                } catch (EntityNotFoundException e) {
                    logger.warn("User " + tokenUtil.getUserIdFromToken(token) + " no exist.");
                } catch (Exception e) {
                    logger.error(e);
                }
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
