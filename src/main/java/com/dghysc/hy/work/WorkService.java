package com.dghysc.hy.work;

import com.dghysc.hy.until.SpecificationUtil;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.WorkRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Work Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class WorkService {

    private final WorkRepository workRepository;

    private final SpecificationUtil<Work> specificationUtil;

    public WorkService(WorkRepository workRepository,
                       SpecificationUtil<Work> specificationUtil) {
        this.workRepository = workRepository;
        this.specificationUtil = specificationUtil;
    }

    /**
     * Add Work
     * @param work the work will be add.
     * @return the work have be add.
     */
    Work add(Work work) {
        return workRepository.save(work);
    }

    /**
     * Update Work
     * @param work the work will be update.
     * @return the work have be update.
     */
    Work update(Work work){
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
     * @return the list of query result.
     */
    List<Work> load(Map<String, Object> equalMap,
                    Map<String, Object> likeMap, Integer pageNumber) {
        Specification<Work> specification = specificationUtil.getSpecification(equalMap, likeMap);
        return workRepository.findAll(specification, PageRequest.of(pageNumber, 20)).getContent();
    }

    /**
     * Load Work By Id
     * @param id the work id.
     * @return the work.
     * @throws NoSuchElementException if work not exist throw this exception.
     */
    Work loadById(Integer id) throws NoSuchElementException {
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
}
