package com.dghysc.hy.work.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Embeddable
public class WorkProcessKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "work_id", nullable = false, updatable = false)
    private Integer workId;

    @Id
    @Column(name = "process_id", nullable = false, updatable = false)
    private Integer processId;

    public WorkProcessKey() { }

    public WorkProcessKey(@NotNull Integer workId, @NotNull Integer processId) {
        Optional.of(workId).ifPresent(this::setWorkId);
        Optional.of(processId).ifPresent(this::setProcessId);
    }

    public Integer getWorkId() {
        return workId;
    }

    private void setWorkId(Integer workId) {
        this.workId = workId;
    }

    public Integer getProcessId() {
        return processId;
    }

    private void setProcessId(Integer processId) {
        this.processId = processId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorkProcessKey that = (WorkProcessKey) o;
        return workId.equals(that.workId) &&
                processId.equals(that.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workId, processId);
    }
}
