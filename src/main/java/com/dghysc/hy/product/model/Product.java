package com.dghysc.hy.product.model;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.work.model.Work;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * The Product Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serial;

    private Timestamp endTime;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    private Work work;

    @OneToMany(mappedBy = "product", cascade = CascadeType.DETACH)
    @JsonIgnore
    private Set<ProductProcess> productProcesses = new HashSet<>();

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

    public Product() { }

    public Product(
            @NotNull String serial, @NotNull Work work,
            @NotNull User creator
    ) {
        this(serial, work, creator, new Timestamp(System.currentTimeMillis()));
    }

    public Product(
            @NotNull String serial, @NotNull Work work,
            @NotNull User creator, @NotNull Timestamp time
    ) {
        Optional.of(serial).ifPresent(this::setSerial);
        Optional.of(work).ifPresent(this::setWork);
        Optional.of(creator).ifPresent(user -> {
            this.createUser = user;
            this.updateUser = user;
        });
        Optional.of(time).ifPresent(t -> {
            this.createTime = t;
            this.updateTime = t;
        });
    }

    public Long getId() {
        return id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Set<ProductProcess> getProductProcesses() {
        return productProcesses;
    }

    public Work getWork() {
        return work;
    }

    private void setWork(Work work) {
        this.work = work;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public User getCreateUser() {
        return createUser;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }
}
