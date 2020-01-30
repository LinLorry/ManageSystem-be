package com.dghysc.hy.user.model;

import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * The User Role Model
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.security.core.GrantedAuthority
 */
@Entity
public class UserRole implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private User user;

    @Id
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private Role role;

    public UserRole() { }

    public UserRole(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return role.getRole();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRole)) return false;
        UserRole userRole = (UserRole) o;
        return Objects.equals(user, userRole.user) &&
                Objects.equals(role, userRole.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }
}
