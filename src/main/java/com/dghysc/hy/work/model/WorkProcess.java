package com.dghysc.hy.work.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * The Work Process Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class WorkProcess implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn
    private Work work;

    @Id
    @ManyToOne
    @JoinColumn
    private Process process;

    private Integer sequenceNumber;

    public WorkProcess() {
    }

    public WorkProcess(Work work, Process process, Integer sequenceNumber) {
        this.work = work;
        this.process = process;
        this.sequenceNumber = sequenceNumber;
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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof  WorkProcess)) return false;
        WorkProcess that = (WorkProcess) o;
        return Objects.equals(this.work.getId(), that.work.getId()) &&
                Objects.equals(this.process.getId(), that.process.getId()) &&
                Objects.equals(this.sequenceNumber, that.sequenceNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(work.getId(), process.getId(), sequenceNumber);
    }
}
