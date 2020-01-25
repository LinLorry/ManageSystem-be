package com.dghysc.hy.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The Child Menu Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class ChildMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(updatable = false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, length = 32, nullable = false)
    private String name;

    @Column(unique = true, length = 64, nullable = false)
    private String url;

    @JsonIgnore
    @ManyToOne(optional = false)
    private ParentMenu parent;

    @JsonIgnore
    @OneToMany(mappedBy = "menu", orphanRemoval = true,
            cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    private Set<RoleMenu> roleMenuSet = new HashSet<>();

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ParentMenu getParent() {
        return parent;
    }

    public void setParent(ParentMenu parent) {
        this.parent = parent;
    }

    public Set<RoleMenu> getRoleMenuSet() {
        return roleMenuSet;
    }
}
