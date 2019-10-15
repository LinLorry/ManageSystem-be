package com.dghysc.hy.work.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Process {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String name;

    private String comment;

    private Date updateTime;

    private Integer updateUser;

    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
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
