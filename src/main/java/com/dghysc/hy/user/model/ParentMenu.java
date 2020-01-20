package com.dghysc.hy.user.model;

import javax.persistence.*;
import java.io.Serializable;
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

    public Set<ChildMenu> getChildMenuSet() {
        return childMenuSet;
    }
}
