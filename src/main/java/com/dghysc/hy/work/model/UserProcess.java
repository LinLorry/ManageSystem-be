package com.dghysc.hy.work.model;


import com.dghysc.hy.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Optional;

/**
 * The User Process Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
@Table(name = "user_process")
@IdClass(UserProcessId.class)
public class UserProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Id
    @Column(name = "process_id", nullable = false, updatable = false)
    private Integer processId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "process_id", insertable = false, updatable = false)
    private Process process;

    public UserProcess() { }

    public UserProcess(@NotNull Long userId, @NotNull Integer processId) {
        Optional.of(userId).ifPresent(this::setUserId);
        Optional.of(processId).ifPresent(this::setProcessId);
    }

    public Long getUserId() {
        return userId;
    }

    private void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getProcessId() {
        return processId;
    }

    private void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public User getUser() {
        return user;
    }

    public Process getProcess() {
        return process;
    }
}
