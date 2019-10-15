package com.dghysc.hy.work.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Work {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    private String comment;

    private Date updateDate;

    private Integer updateUser;

    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<WorkProcess> workProcesses = new HashSet<>();

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public Set<WorkProcess> getWorkProcesses() {
        return workProcesses;
    }

    public void setWorkProcesses(Set<WorkProcess> workProcesses) {
        this.workProcesses = workProcesses;
    }
}
