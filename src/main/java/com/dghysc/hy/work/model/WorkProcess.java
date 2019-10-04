package com.dghysc.hy.work.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

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

    public WorkProcess() {
    }

    public WorkProcess(Work work, Process process) {
        this.work = work;
        this.process = process;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof  WorkProcess)) return false;
        WorkProcess that = (WorkProcess) o;
        return Objects.equals(this.work.getId(), that.work.getId()) &&
                Objects.equals(this.process.getId(), that.process.getId());
    }
}
