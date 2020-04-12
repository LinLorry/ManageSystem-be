package com.dghysc.hy.product;

import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.product.model.ProductProcessId;
import com.dghysc.hy.product.rep.ProductProcessRepository;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.SpecificationUtil;
import com.dghysc.hy.util.ZoneIdUtil;
import com.dghysc.hy.work.model.UserProcess;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.repo.UserProcessRepository;
import com.dghysc.hy.work.repo.WorkRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Product Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class ProductService {

    private final UserProcessRepository userProcessRepository;

    private final WorkRepository workRepository;

    private final ProductRepository productRepository;

    private final ProductProcessRepository productProcessRepository;

    public ProductService(
            UserProcessRepository userProcessRepository,
            WorkRepository workRepository,
            ProductRepository productRepository,
            ProductProcessRepository productProcessRepository
    ) {
        this.userProcessRepository = userProcessRepository;
        this.workRepository = workRepository;
        this.productRepository = productRepository;
        this.productProcessRepository = productProcessRepository;
    }

    /**
     * Add Product Service
     * @param serial the serial.
     * @param endTime the endTime.
     * @param workId the work id.
     * @return the product.
     * @throws NullPointerException if {@code serial}  or {@code workId} is null
     * @throws EntityNotFoundException if work id is {@code workId} not exist
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    Product add(
            @NotNull String serial, @Nullable String IGT,
            @Nullable String ERP, @Nullable String central,
            @Nullable String area, @Nullable String design,
            @Nullable Timestamp beginTime, @Nullable Timestamp demandTime,
            @Nullable Timestamp endTime, @NotNull Integer workId
    ) {
        Work work = workRepository.findById(Optional.of(workId).get())
                .orElseThrow(EntityNotFoundException::new);
        Product product = new Product(serial, work, SecurityUtil.getUser());

        product.setIGT(IGT);
        product.setERP(ERP);
        product.setCentral(central);
        product.setArea(area);
        product.setDesign(design);
        product.setBeginTime(beginTime);
        product.setDemandTime(demandTime);
        product.setEndTime(endTime);

        return productRepository.save(product);
    }

    /**
     * Update Product Service.
     * @param id the product id.
     * @param serial the product serial.
     * @param endTime the product endTime.
     * @return the product.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    Product update(
            @NotNull Long id, @Nullable String serial,
            @Nullable String IGT, @Nullable String ERP,
            @Nullable String central, @Nullable String area,
            @Nullable String design, @Nullable Timestamp beginTime,
            @Nullable Timestamp demandTime, @Nullable Timestamp endTime
    ) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User updater = SecurityUtil.getUser();

        Product product = productRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        Optional.ofNullable(serial).ifPresent(product::setSerial);
        Optional.ofNullable(IGT).ifPresent(product::setIGT);
        Optional.ofNullable(ERP).ifPresent(product::setERP);
        Optional.ofNullable(central).ifPresent(product::setCentral);
        Optional.ofNullable(area).ifPresent(product::setArea);
        Optional.ofNullable(design).ifPresent(product::setDesign);
        Optional.ofNullable(beginTime).ifPresent(product::setBeginTime);
        Optional.ofNullable(demandTime).ifPresent(product::setDemandTime);

        Optional.ofNullable(endTime).ifPresent(product::setEndTime);
        product.setUpdateUser(updater);
        product.setUpdateTime(now);

        return productRepository.save(product);
    }

    /**
     * User Complete Product Process Service
     * @param productId the product productId
     * @return complete result:
     *      if product's all process have been complete, return false.
     *      if execute complete user can't complete this process return false.
     *      if complete success return true.
     * @throws EntityNotFoundException the product not exist.
     * @throws NullPointerException the {@code productId} is {@literal null}
     */
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER', 'WORKER')")
    public boolean completeProcess(@NotNull Long productId, @NotNull Integer processId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(EntityNotFoundException::new);

        boolean exist = false;
        for (WorkProcess workProcess : product.getWork().getWorkProcesses()) {
            if (workProcess.getProcessId().equals(processId)) {
                exist = true;
                break;
            }
        }

        if (!exist) return false;

        if (productProcessRepository.findById(
                new ProductProcessId(productId, processId)
        ).isPresent()) {
            return false;
        }

        boolean canDo = false;

        for (GrantedAuthority authority : SecurityUtil.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                canDo = true;
            } else if ("ROLE_PRODUCT_MANAGER".equals(authority.getAuthority())) {
                canDo = true;
            }
        }

        if (!canDo) {
            List<UserProcess> userProcesses = userProcessRepository.findAllByUserId(SecurityUtil.getUserId());

            for (UserProcess userProcess : userProcesses) {
                if (processId.equals(userProcess.getProcessId())) {
                    canDo = true;
                    break;
                }
            }
        }
        if (!canDo) return false;

        ProductProcess productProcess = new ProductProcess(productId, processId, SecurityUtil.getUser());

        productProcessRepository.save(productProcess);

        return true;
    }

    /**
     * Un Complete Product Process Service.
     * @param productId the product id.
     * @param processId the process id.
     * @throws EmptyResultDataAccessException if product process not exists.
     */
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public void unCompleteProcess(@NotNull Long productId, @NotNull Integer processId) {
        productProcessRepository.deleteById(new ProductProcessId(productId, processId));
    }

    /**
     * Complete Product
     * @param id the product id.
     * @return the complete product.
     */
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public boolean complete(@NotNull Long id) {
        Product product = productRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        if (product.isComplete()) {
            return true;
        } else if (product.getProductProcesses().size() !=
                product.getWork().getWorkProcesses().size()) {
            return false;
        }

        product.setComplete();
        productRepository.save(product);

        return true;
    }

    /**
     * Load Product By Its Field
     * @param likeMap {
     *     "the product field": value will be equal by "%value%"
     * }
     * @param dateGreaterMap {
     *     "the product date field": value will be greater by "%value%"
     * }
     * @param dateLesserMap {
     *     "the product date field": value will be lesser by "%value%"
     * }
     * @param complete load complete product or else.
     * @param pageNumber page number.
     * @param pageSize page size.
     * @return the page of query result.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    Page<Product> load(
            @NotNull Map<String, Object> likeMap, @NotNull Map<String, Date> dateGreaterMap,
            @NotNull Map<String, Date> dateLesserMap, boolean complete,
            int pageNumber, int pageSize
    ) {

        SpecificationUtil specificationUtil = new SpecificationUtil();

        specificationUtil.addLikeMap(likeMap);
        specificationUtil.addGreaterDateMap(dateGreaterMap);
        specificationUtil.addLessDateMap(dateLesserMap);
        specificationUtil.addEqualMap("complete", complete);

        Specification<Product> specification = specificationUtil.getSpecification();

        return productRepository.findAll(specification, PageRequest.of(pageNumber, pageSize));
    }

    /**
     * Load Product By Id Service
     * @param id the product id.
     * @return the product.
     * @throws EntityNotFoundException if the product isn't exist throw this exception.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER', 'WORKER_MANAGER')")
    Product loadById(@NotNull Long id) {
        return productRepository.findById(Optional.of(id).get()).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Load Product With Work And Processes By Id
     * @param id the product id.
     * @return the product with its work and processes.
     * @throws EntityNotFoundException if the product isn't exist throw this exception.
     */
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER', 'WORKER_MANAGER', 'WORKER')")
    public Product loadWithProcessesById(@NotNull Long id) {
        Product product = productRepository.findById(Optional.of(id).get())
                .orElseThrow(EntityNotFoundException::new);

        product.getProductProcesses().size();
        product.getWork().getWorkProcesses().size();

        return product;
    }

    /**
     * Count Not Start Product Service
     * @return the number of han't started products.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public int countNotStart() {
        return productRepository.countAllNotStart();
    }

    /**
     * Count Start Product Service
     * @return the number of have started products.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public int countStart() {
        return productRepository.countALLStart();
    }

    /**
     * Count Can Complete Product Service
     * @return the number of can complete products.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public int countCanComplete() {
        return productRepository.countAllCanComplete();
    }

    /**
     * Count Create Product During Month Service
     * @return the number of product which created in this month.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUCT_MANAGER')")
    public int countCreateProductDuringTheMonth() {
        LocalDateTime localDateTime = LocalDateTime.now(ZoneIdUtil.CST);

        Timestamp first = Timestamp.from(
                localDateTime.with(TemporalAdjusters.firstDayOfMonth())
                        .atZone(ZoneIdUtil.CST).toInstant()
        );
        Timestamp last = Timestamp.from(
                localDateTime.with(TemporalAdjusters.lastDayOfMonth())
                        .atZone(ZoneIdUtil.CST).toInstant()
        );

        return productRepository
                .countAllByCreateTimeAfterAndCreateTimeBefore(first, last);
    }
}
