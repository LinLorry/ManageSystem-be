package com.dghysc.hy.user.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * The Role Menu Model
 * @author lorry
 * @author lin84464995@163.com
 */
@Entity
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private Role role;

    @Id
    @ManyToOne(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @PrimaryKeyJoinColumn
    private ChildMenu menu;

    public RoleMenu() {
    }

    public RoleMenu(Role role, ChildMenu menu) {
        this.role = role;
        this.menu = menu;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public ChildMenu getMenu() {
        return menu;
    }

    public void setMenu(ChildMenu menu) {
        this.menu = menu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleMenu roleMenu = (RoleMenu) o;
        return Objects.equals(role, roleMenu.role) &&
                Objects.equals(menu, roleMenu.menu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(role, menu);
    }
}
