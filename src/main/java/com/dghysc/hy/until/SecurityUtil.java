package com.dghysc.hy.until;

import com.dghysc.hy.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigInteger;
import java.util.Collection;

public class SecurityUtil {
    public static User getUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static BigInteger getUserId() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
    }

    public static Collection<? extends GrantedAuthority> getAuthorities() {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAuthorities();
    }
}
