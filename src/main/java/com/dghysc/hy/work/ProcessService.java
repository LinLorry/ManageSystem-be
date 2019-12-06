package com.dghysc.hy.work;

import com.dghysc.hy.until.SpecificationUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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
     * Add Or Update Process
     * @param process the process will be add.
     * @return the process have be add.
     */
    Process addOrUpdate(Process process) {
        return processRepository.save(process);
    }

    /**
     * Load Process By Id, Name, Comment
     * @param equalMap {
     *     "the process field": value have to equal
     * }
     * @param likeMap {
     *     "the process field": value will be equal by "%value%"
     * }
     * @param pageNumber page number.
     * @return the page of query result.
     */
    Page<Process> load(Map<String, Object> equalMap,
                       Map<String, Object> likeMap, Integer pageNumber) {

        SpecificationUtil specificationUtil = new SpecificationUtil();
        specificationUtil.addEqualMap(equalMap);
        specificationUtil.addLikeMap(likeMap);

        Specification<Process> specification = specificationUtil.getSpecification();
        return processRepository.findAll(specification, PageRequest.of(pageNumber, 20));
    }

    /**
     * Load Process By Id
     * @param id the process id.
     * @return the process.
     * @throws NoSuchElementException if process not exist throw this exception.
     */
    Process loadById(Integer id) throws NoSuchElementException {
        return processRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    /**
     * Check Process By Name
     * @param name the process name.
     * @return if name is exist return true else return false.
     */
    boolean checkByName(String name) {
        return processRepository.existsByName(name);
    }
}
