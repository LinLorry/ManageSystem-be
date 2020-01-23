package com.dghysc.hy.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * The Role Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 32)
    private String role;

    @Column(unique = true, length = 64)
    private String name;

    @JsonIgnore
    @Column(nullable = false, columnDefinition = "Boolean default false")
    private Boolean isDelete = false;

    @Column(nullable = false, updatable = false)
    private Timestamp createTime;

    @Column(nullable = false)
    private Timestamp updateTime;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private User createUser;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User updateUser;

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private Set<UserRole> userRoleSet = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "role")
    private Set<RoleMenu> roleMenuSet = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public void delete() {
        this.isDelete = true;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }

    public Set<UserRole> getUserRoleSet() {
        return userRoleSet;
    }

    public Set<RoleMenu> getRoleMenuSet() {
        return roleMenuSet;
    }
}
