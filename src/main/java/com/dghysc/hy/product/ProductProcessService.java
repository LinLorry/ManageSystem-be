package com.dghysc.hy.product;

import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.product.model.ProductProcessId;
import com.dghysc.hy.product.rep.ProductProcessRepository;
import com.dghysc.hy.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Product Process Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class ProductProcessService {

    private final ProductProcessRepository productProcessRepository;

    public ProductProcessService(ProductProcessRepository productProcessRepository) {
        this.productProcessRepository = productProcessRepository;
    }

    public ProductProcess loadById(@NotNull Long productId, @NotNull Integer processId) {
        return productProcessRepository.findById(new ProductProcessId(productId, processId))
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Load All Self Finish Product Processes
     * @param pageNumber the page number.
     * @param pageSize the page size
     * @return the product processes.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER', 'WORKER')")
    public Page<ProductProcess> loadAllSelfFinish(int pageNumber, int pageSize) {
        return productProcessRepository.findAllByFinisher(SecurityUtil.getUser(),
                PageRequest.of(
                        pageNumber, pageSize,
                        Sort.by("finishTime").descending()
                )
        );
    }

    /**
     * Load All Product Processes By Finish Time Scope
     * @param after load finish time after this.
     * @param before load finish time before this.
     * @return the product processes.
     * @throws NullPointerException if {@literal after} or {@literal before} is {@literal null}.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
    public List<ProductProcess> loadAllByFinishTimeAfterAndFinishTimeBefore(
            @NotNull Timestamp after, @NotNull Timestamp before) {
        return productProcessRepository.findAllByFinishTimeAfterAndFinishTimeBefore(
                Optional.of(after).get(), Optional.of(before).get()
        );
    }
}
