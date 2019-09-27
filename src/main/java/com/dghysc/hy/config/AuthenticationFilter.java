package com.dghysc.hy.config;

import com.dghysc.hy.until.TokenUtil;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    @Value("${Manage.AuthenticationName}")
    private String AuthenticationName;

    private final TokenUtil tokenUtil;

    private final UserRepository userRepository;

    public AuthenticationFilter(TokenUtil tokenUtil, UserRepository userRepository) {
        this.tokenUtil = tokenUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String requestTokenHeader = httpServletRequest.getHeader("Authorization");

        System.out.println(AuthenticationName);

        String username = null;
        String token = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith(AuthenticationName + " ")) {
            token = requestTokenHeader.substring(AuthenticationName.length() + 1);

            try {
                username = tokenUtil.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                System.out.println("Unable to get JWT Token");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            User user = userRepository.findUserByUsername(username);

            // if token is valid configure Spring Security to manually set authentication
            if (tokenUtil.validateToken(token, user)) {

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        user, null, new ArrayList<>());

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
