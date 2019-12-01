package com.dghysc.hy.work.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WorkProcessKey implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @ManyToOne
    @JoinColumn
    private Work work;

    @Id
    @ManyToOne
    @JoinColumn
    private Process process;

    public WorkProcessKey() {
    }

    public WorkProcessKey(Work work, Process process) {
        this.work = work;
        this.process = process;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        else if (!(obj instanceof WorkProcessKey)) return false;
        WorkProcessKey tmp = (WorkProcessKey) obj;
        return tmp.work.equals(work) && tmp.process.equals(process);
    }

    @Override
    public int hashCode() {
        return Objects.hash(work.getId(), process.getId());
    }
}
