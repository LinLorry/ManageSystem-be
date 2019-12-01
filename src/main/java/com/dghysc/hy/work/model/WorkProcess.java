package com.dghysc.hy.work.model;

import com.dghysc.hy.user.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * The Work Process Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class WorkProcess implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn
    private Work work;

    @ManyToOne
    @JoinColumn
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

    public WorkProcess(Work work, Process process, Integer sequenceNumber) {
        this.work = work;
        this.process = process;
        this.sequenceNumber = sequenceNumber;
    }

    public WorkProcess(Work work, Process process, Integer sequenceNumber, User createUser, Timestamp createTime) {
        this.work = work;
        this.process = process;
        this.sequenceNumber = sequenceNumber;
        this.createUser = createUser;
        this.createTime = createTime;
        this.updateUser = createUser;
        this.updateTime = createTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof  WorkProcess)) return false;
        WorkProcess tmp = (WorkProcess) o;
        return tmp.work.equals(work)
                && tmp.process.equals(process)
                && tmp.sequenceNumber.equals(sequenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(work.getId(), process.getId(), sequenceNumber);
    }
}
