package com.dghysc.hy.work.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.io.Serializable;

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

    public WorkProcess() { }

    public WorkProcess(Work work, Process process,
                       Integer sequenceNumber) {
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
}
