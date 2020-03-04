package com.dghysc.hy.product.model;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.EntityUtil;
import com.dghysc.hy.work.model.Work;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The Complete Product Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
public class CompleteProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String serial;

    @Column(updatable = false)
    private Timestamp endTime;

    @OneToMany(mappedBy = "product")
    @JsonIgnore
    private Set<ProductProcess> productProcesses = new HashSet<>();

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Work work;

    @Column(nullable = false, updatable = false)
    private Timestamp createTime;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private User createUser;

    @Column(nullable = false, updatable = false)
    private Timestamp updateTime;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private User updateUser;

    public CompleteProduct() { }

    public CompleteProduct(@NotNull Product product, @NotNull User finisher) {
        this(product, finisher, new Timestamp(System.currentTimeMillis()));
    }

    public CompleteProduct(@NotNull Product product, @NotNull User finisher, @NotNull Timestamp time) {
        Optional.of(product).ifPresent(p -> {
            Optional.of(p.getId()).ifPresent(this::setId);
            Optional.of(p.getSerial()).ifPresent(this::setSerial);
            this.endTime = product.getEndTime();
            Optional.of(p.getWork()).ifPresent(this::setWork);
            Optional.of(p.getCreateTime()).ifPresent(this::setCreateTime);
            Optional.of(p.getCreateUser()).ifPresent(this::setCreateUser);
        });

        Optional.of(finisher).ifPresent(this::setUpdateUser);
        Optional.of(time).ifPresent(this::setUpdateTime);
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    private void setSerial(String serial) {
        this.serial = serial;
    }

    public Timestamp getEndTime() {
        return endTime;
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

    private void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public User getCreateUser() {
        return createUser;
    }

    private void setCreateUser(User createUser) {
        this.createUser = createUser;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    private void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public User getUpdateUser() {
        return updateUser;
    }

    private void setUpdateUser(User updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getWorkId() {
        return work.getId();
    }

    public String getWorkName() {
        return work.getName();
    }

    @JsonAnyGetter
    public Map<String, Object> historyInfo() {
        return EntityUtil.getCreateAndUpdateInfo(createUser, updateUser);
    }
}
