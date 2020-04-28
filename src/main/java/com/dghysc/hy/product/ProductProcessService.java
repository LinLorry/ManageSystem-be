package com.dghysc.hy.product;

import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.product.rep.ProductProcessRepository;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.ZoneIdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

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

    /**
     * Load All Today Finish Product Processes Service
     * @return the list of today finish product processes.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
    public List<ProductProcess> loadAllTodayFinish() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneIdUtil.CST);
        ZonedDateTime today = localDateTime
                .toLocalDate()
                .atStartOfDay(ZoneIdUtil.CST);

        Timestamp todayTimestamp = Timestamp.from(today.toInstant());
        Timestamp tomorrowTimestamp = Timestamp.from(today.plusDays(1).toInstant());

        return productProcessRepository.findAllByFinishTimeAfterAndFinishTimeBefore(
                todayTimestamp, tomorrowTimestamp
        );
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
}
