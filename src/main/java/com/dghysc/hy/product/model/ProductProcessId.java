package com.dghysc.hy.product.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * The Product Process Id
 * @author lorry
 * @author lin864464995@163.com
 */
@Embeddable
public class ProductProcessId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "product_id", updatable = false)
    private Long productId;

    @Column(name = "process_id", updatable = false)
    private Integer processId;

    public ProductProcessId() { }

    public ProductProcessId(@NotNull Long productId, @NotNull Integer processId) {
        Optional.of(productId).ifPresent(this::setProductId);
        Optional.of(processId).ifPresent(this::setProcessId);
    }

    public Long getProductId() {
        return productId;
    }

    private void setProductId(Long productId) {
        this.productId = productId;
    }

    private Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductProcessId that = (ProductProcessId) o;
        return productId.equals(that.productId) &&
                processId.equals(that.processId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, processId);
    }
}
