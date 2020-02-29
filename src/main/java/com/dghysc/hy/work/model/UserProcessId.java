package com.dghysc.hy.work.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

/**
 * The User Process Id
 * @author lorry
 * @author lin864464995@163.com
 */
@Embeddable
public class UserProcessId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Id
    @Column(name = "process_id", nullable = false, updatable = false)
    private Integer processId;

    public UserProcessId() { }

    public UserProcessId(Long userId, Integer processId) {
        this.userId = userId;
        this.processId = processId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProcessId userProcessId = (UserProcessId) o;
        return userId.equals(userProcessId.userId) &&
                processId.equals(userProcessId.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, processId);
    }
}
