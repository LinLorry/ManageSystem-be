package com.dghysc.hy.work;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.SpecificationUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;

/**
 * Process Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class ProcessService {

    private final ProcessRepository processRepository;

    public ProcessService(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    /**
     * Add A Process
     *
     * @param name    the process name.
     * @param comment the process comment.
     * @return the process.
     * @throws DataIntegrityViolationException if the have process name is {@code name}.
     * @throws NullPointerException            if {@code name} is {@literal null}.
     */
    Process add(@NotNull String name, @Nullable String comment) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        Process process = new Process();

        Optional.of(name).ifPresent(process::setName);
        Optional.ofNullable(comment).ifPresent(process::setComment);

        process.setCreateTime(now);
            process.setUpdateTime(now);

        process.setCreateUser(creator);
        process.setUpdateUser(creator);

        return processRepository.save(process);
    }

    /**
     * Update Process
     *
     * @param id      the process id.
     * @param name    update process name.
     * @param comment update process comment.
     * @return the updated process.
     * @throws DataIntegrityViolationException if the have process name is {@code name}.
     * @throws EntityNotFoundException         if process id is {@code id} not exist.
     * @throws IllegalArgumentException        if {@code id} is {@literal null}.
     */
    Process update(@NotNull Integer id, @Nullable String name, @Nullable String comment) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        Process process = processRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Optional.ofNullable(name).ifPresent(process::setName);
        Optional.ofNullable(comment).ifPresent(process::setComment);

        process.setUpdateTime(now);

        process.setUpdateUser(creator);

        return processRepository.save(process);
    }

    /**
     * Load Process By Id, Name, Comment
     * @param likeMap {
     *     "the process field": value will be equal by "%value%"
     * }
     * @param pageNumber page number.
     * @param pageSize page size.
     * @return the page of query result.
     */
    Page<Process> load(Map<String, Object> likeMap,
                    Integer pageNumber, Integer pageSize) {
        SpecificationUtil specificationUtil = new SpecificationUtil();
        specificationUtil.addLikeMap(likeMap);

        Specification<Process> specification = specificationUtil.getSpecification();
        return processRepository.findAll(specification, PageRequest.of(pageNumber, pageSize));
    }

    /**
     * Load Process By Id
     *
     * @param id the process id.
     * @return the process.
     * @throws EntityNotFoundException  if process id is {@code id} not exist.
     * @throws IllegalArgumentException if {@code id} is {@literal null}.
     */
    public Process loadById(Integer id) {
        return processRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Remove Process By Id
     * @param id the process id.
     * @throws org.springframework.dao.EmptyResultDataAccessException
     *      if the process didn't exists throw this exception.
     */
    void removeById(Integer id) throws EmptyResultDataAccessException {
        processRepository.deleteById(id);
    }
}
