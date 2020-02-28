package com.dghysc.hy.work.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Optional;

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
    @Column(name = "work_id", nullable = false, updatable = false)
    private Integer workId;

    @Id
    @Column(name = "process_id", nullable = false, updatable = false)
    private Integer processId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "work_id", referencedColumnName = "id",
            updatable = false, insertable = false)
    private Work work;

    @ManyToOne(optional = false)
    @JoinColumn(name = "process_id", referencedColumnName = "id",
            updatable = false, insertable = false)
    private Process process;

    private Integer sequenceNumber;

    public WorkProcess() { }

    public WorkProcess(@NotNull Integer workId, @NotNull Integer processId,
                       @NotNull Integer sequenceNumber) {
        Optional.of(workId).ifPresent(this::setWorkId);
        Optional.of(processId).ifPresent(this::setProcessId);
        Optional.of(sequenceNumber).ifPresent(this::setSequenceNumber);
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

    public Work getWork() {
        return work;
    }

    public Process getProcess() {
        return process;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
