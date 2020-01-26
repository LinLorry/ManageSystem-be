package com.dghysc.hy.user.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Parent Menu Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class ParentMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 16)
    private String name;

    @Column(nullable = false, updatable = false)
    private Timestamp createTime;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    private User createUser;

    @Column(nullable = false)
    private Timestamp updateTime;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private User updateUser;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER,
            cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    private Set<ChildMenu> childMenuSet;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public User getCreateUser() {
        return createUser;
    }

    public void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }

    public Set<ChildMenu> getChildMenuSet() {
        return childMenuSet;
    }

    @JsonAnyGetter
    public Map<String, Object> getInfo() {
        Map<String, Object> map = new HashMap<>();

        map.put("createUserName", createUser.getName());
        map.put("createUserId", createUser.getId());
        map.put("updateUserName", updateUser.getName());
        map.put("updateUserId", updateUser.getId());

        return map;
    }
}
