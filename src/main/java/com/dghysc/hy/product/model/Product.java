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
import java.util.*;

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

    @Column(nullable = false, columnDefinition = "char(8)")
    private String serial;              // Serial号

    @Column(length = 32)
    private String IGT;                 // IGT号

    @Column(length = 32)
    private String ERP;                 // ERP号

    @Column(length = 32)
    private String central;             // central号

    @Column(length = 64)
    private String area;                // 地区

    @Column(length = 32)
    private String design;              // 花色

    private Timestamp beginTime;        // 下单时间

    private Timestamp demandTime;       // 需求时间

    private Timestamp endTime;          // 发货时间

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Work work;

    @OneToMany(mappedBy = "product", cascade = CascadeType.DETACH)
    @JsonIgnore
    private Set<ProductProcess> productProcesses = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private Timestamp createTime;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private User createUser;

    @Column(nullable = false)
    private Timestamp updateTime;

    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private User updateUser;

    @Column(nullable = false, columnDefinition = "Boolean default false")
    private Boolean complete = false;   // 订单是否完成标识

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

    public String getIGT() {
        return IGT;
    }

    public void setIGT(String IGT) {
        this.IGT = IGT;
    }

    public String getERP() {
        return ERP;
    }

    public void setERP(String ERP) {
        this.ERP = ERP;
    }

    public String getCentral() {
        return central;
    }

    public void setCentral(String central) {
        this.central = central;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getDesign() {
        return design;
    }

    public void setDesign(String design) {
        this.design = design;
    }

    public Timestamp getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Timestamp beginTime) {
        this.beginTime = beginTime;
    }

    public Timestamp getDemandTime() {
        return demandTime;
    }

    public void setDemandTime(Timestamp demandTime) {
        this.demandTime = demandTime;
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

    public Boolean isComplete() {
        return complete;
    }

    public void setComplete() {
        this.complete = true;
    }

    public void setUnComplete() {
        this.complete = false;
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
