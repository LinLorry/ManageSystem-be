package com.dghysc.hy.work.model;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.EntityUtil;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

/**
 * The Work Process Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
@Table(name = "work_process")
@IdClass(WorkProcessKey.class)
@JsonIgnoreProperties({"work", "process"})
public class WorkProcess implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "work_id", referencedColumnName = "id")
    private Work work;

    @Id
    @ManyToOne
    @JoinColumn(name = "process_id", referencedColumnName = "id")
    private Process process;

    private Integer sequenceNumber;

    @Column(nullable = false, updatable = false)
    private Timestamp createTime;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private User createUser;

    @Column(nullable = false)
    private Timestamp updateTime;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private User updateUser;

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
    public Map<String, Object> getInfo() {

        Map<String, Object> map = EntityUtil.getCreateAndUpdateInfo(createUser, updateUser);

        map.put("workId", work.getId());
        map.put("workName", work.getName());
        map.put("processId", process.getId());
        map.put("processName", process.getName());

        return map;
    }
}
