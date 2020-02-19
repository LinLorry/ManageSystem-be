package com.dghysc.hy.work;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.SecurityUtil;
import com.dghysc.hy.util.SpecificationUtil;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.WorkRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

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


    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    /**
     * Add Or Update Work
     * @param work the work will be add.
     * @return the work have be add.
     */
    Work addOrUpdate(Work work) {
        return workRepository.save(work);
    }

    /**
     * Add A Work
     *
     * @param name    the work name.
     * @param comment the work comment.
     * @return the work.
     * @throws DataIntegrityViolationException if the have work name is {@code name}.
     * @throws NullPointerException            if {@code name} is {@literal null}.
     */
    Work add(@NotNull String name, @Nullable String comment) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        Work work = new Work();

        Optional.of(name).ifPresent(work::setName);
        Optional.ofNullable(comment).ifPresent(work::setComment);

        work.setCreateTime(now);
        work.setUpdateTime(now);

        work.setCreateUser(creator);
        work.setUpdateUser(creator);

        return workRepository.save(work);
    }

    /**
     * Load Work By Id, Name, Comment
     * @param equalMap {
     *     "the work field": value have to equal
     * }
     * @param likeMap {
     *     "the work field": value will be equal by "%value%"
     * }
     * @param pageNumber page number.
     * @return the page of query result.
     */
    Page<Work> load(Map<String, Object> equalMap,
                    Map<String, Object> likeMap, Integer pageNumber) {
        SpecificationUtil specificationUtil = new SpecificationUtil();
        specificationUtil.addEqualMap(equalMap);
        specificationUtil.addLikeMap(likeMap);

        Specification<Work> specification = specificationUtil.getSpecification();
        return workRepository.findAll(specification, PageRequest.of(pageNumber, 20));
    }

    /**
     * Load Work By Id
     * @param id the work id.
     * @return the work.
     * @throws NoSuchElementException if work not exist throw this exception.
     */
    public Work loadById(Integer id) throws NoSuchElementException {
        Optional<Work> optionalWork = workRepository.findById(id);
        if (optionalWork.isPresent()) {
            return optionalWork.get();
        }
        throw new NoSuchElementException("There's no Work with id " + id);
    }

    /**
     * Check Work By Name
     * @param name the work name.
     * @return if name is exist return true else return false.
     */
    boolean checkByName(String name) {
        return workRepository.existsByName(name);
    }

    /**
     * Remove Work By Id
     * @param id the work id.
     * @throws org.springframework.dao.EmptyResultDataAccessException
     *      if the work didn't exists throw this exception.
     */
    void removeById(Integer id) throws EmptyResultDataAccessException {
        workRepository.deleteById(id);
    }
}
