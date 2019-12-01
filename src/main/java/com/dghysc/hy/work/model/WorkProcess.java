package com.dghysc.hy.work.model;

import com.dghysc.hy.user.model.User;
import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * The Work Process Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
@IdClass(WorkProcessKey.class)
@JsonIgnoreProperties({"work", "process"})
public class WorkProcess implements Serializable {

    @Id
    private Work work;

    @Id
    private Process process;

    private Integer sequenceNumber;

    @JsonIgnore
    @ManyToOne
    private User createUser;

    private Timestamp createTime;

    @JsonIgnore
    @ManyToOne
    private User updateUser;

    private Timestamp updateTime;

    public WorkProcess() {
    }

    public WorkProcess(Work work, Process process,
                       Integer sequenceNumber,
                       User createUser,
                       Timestamp createTime) {
        this.work = work;
        this.process = process;
        this.sequenceNumber = sequenceNumber;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = createUser;
        this.updateTime = createTime;
    }

    public Work getWork() {
        return work;
    }

    public void setWork(Work work) {
        this.work = work;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public User getCreateUser() {
        return createUser;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @JsonAnyGetter
    public HashMap<String, Object> getInfo() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("workId", work.getId());
        map.put("workName", work.getName());
        map.put("processId", process.getId());
        map.put("processName", process.getName());

        return map;
    }
}
