package com.dghysc.hy.product.model;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.work.model.Process;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.Optional;

/**
 * The Product Process Model
 * @author lorry
 * @author lin864464995@163.com
 */
@Entity
@IdClass(ProductProcessId.class)
@Table(name = "product_process")
public class ProductProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "product_id", updatable = false)
    @JsonIgnore
    private Long productId;

    @Id
    @Column(name = "process_id", updatable = false)
    private Integer processId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false,
            insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",  nullable = false,
            insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private CompleteProduct completeProduct;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "process_id", nullable = false,
            insertable = false, updatable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Process process;

    @ManyToOne(optional = false)
    @JoinColumn(name = "finisher_id", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private User finisher;

    @Column(nullable = false)
    private Timestamp finishTime;

    public ProductProcess() { }

    public ProductProcess(
            @NotNull Long productId, @NotNull Integer processId,
            @NotNull User finisher
    ) {
        this(productId, processId, finisher, new Timestamp(System.currentTimeMillis()));
    }

    public ProductProcess(
            @NotNull Long productId, @NotNull Integer processId,
            @NotNull User finisher, @NotNull Timestamp time
    ) {
        Optional.of(productId).ifPresent(this::setProductId);
        Optional.of(processId).ifPresent(this::setProcessId);
        Optional.of(finisher).ifPresent(this::setFinisher);
        Optional.of(time).ifPresent(this::setFinishTime);
    }

    public Long getProductId() {
        return productId;
    }

    private void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getProcessId() {
        return processId;
    }

    private void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public Product getProduct() {
        return product;
    }

    public CompleteProduct getCompleteProduct() {
        return completeProduct;
    }

    public Process getProcess() {
        return process;
    }

    private void setProcess(Process process) {
        this.process = process;
    }

    public User getFinisher() {
        return finisher;
    }

    public void setFinisher(User finisher) {
        this.finisher = finisher;
    }

    public Timestamp getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Timestamp finishTime) {
        this.finishTime = finishTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductProcess that = (ProductProcess) o;
        return productId.equals(that.productId) &&
                processId.equals(that.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, processId);
    }
}
