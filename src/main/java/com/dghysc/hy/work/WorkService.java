package com.dghysc.hy.work;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.SpecificationUtil;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.repo.ProcessRepository;
import com.dghysc.hy.work.repo.WorkRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;

/**
 * Work Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class WorkService {

    private final WorkRepository workRepository;

    private final ProcessRepository processRepository;

    public WorkService(WorkRepository workRepository, ProcessRepository processRepository) {
        this.workRepository = workRepository;
        this.processRepository = processRepository;
    }

    /**
     * Add A Work
     *
     * @param name    the work name.
     * @param comment the work comment.
     * @param processIds the processes id, this sequence is the work processes sequence.
     * @return the work.
     * @throws DataIntegrityViolationException if the have work name is {@code name}.
     * @throws NullPointerException            if {@code name} is {@literal null}.
     * @throws EntityNotFoundException         if not process id in {@code processIds}.
     */
    @Transactional
    public Work add(@NotNull String name, @Nullable String comment,
                    @NotNull List<Integer> processIds) {
        User creator = SecurityUtil.getUser();

        Work work = new Work(name, creator);

        Optional.ofNullable(comment).ifPresent(work::setComment);

        work = workRepository.save(work);

        if (processIds.size() != processRepository.countByIdIn(processIds)) {
            throw new EntityNotFoundException();
        }

        Set<WorkProcess> workProcesses = work.getWorkProcesses();
        final Integer workId = work.getId();

        for (int i = 0; i < processIds.size(); i++) {
            workProcesses.add(new WorkProcess(workId, processIds.get(i), i));
        }

        return workRepository.save(work);
    }

    /**
     * Update Work
     *
     * @param id      the work id.
     * @param name    update work name.
     * @param comment update work comment.
     * @return the updated work.
     * @throws DataIntegrityViolationException if the have work name is {@code name}.
     * @throws EntityNotFoundException         if work id is {@code id} not exist.
     * @throws IllegalArgumentException        if {@code id} is {@literal null}.
     */
    Work update(@NotNull Integer id, @Nullable String name, @Nullable String comment) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        Work work = workRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Optional.ofNullable(name).ifPresent(work::setName);
        Optional.ofNullable(comment).ifPresent(work::setComment);

        work.setUpdateTime(now);
        work.setUpdateUser(creator);

        return workRepository.save(work);
    }

    /**
     * Load Work By Id, Name, Comment
     * @param likeMap {
     *     "the work field": value will be equal by "%value%"
     * }
     * @param pageNumber page number.
     * @param pageSize page size.
     * @return the page of query result.
     */
    Page<Work> load(Map<String, Object> likeMap,
                    Integer pageNumber, Integer pageSize) {
        SpecificationUtil specificationUtil = new SpecificationUtil();
        specificationUtil.addLikeMap(likeMap);

        Specification<Work> specification = specificationUtil.getSpecification();
        return workRepository.findAll(specification, PageRequest.of(pageNumber, pageSize));
    }

    /**
     * Load Work By Id
     * @param id the work id.
     * @return the work.
     * @throws EntityNotFoundException if work id is {@code id} not exist.
     */
    public Work loadById(Integer id) {
        return workRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Load Work With Processes By Id
     * @param id the work id.
     * @return the work with processes.
     */
    @Transactional
    public Work loadWithProcessesById(@NotNull Integer id) {
        Work work = workRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        work.setProcessesReturn();

        return work;
    }
}
