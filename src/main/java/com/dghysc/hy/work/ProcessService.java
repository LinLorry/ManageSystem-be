package com.dghysc.hy.work;

import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Process Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class ProcessService {
    private final ProcessRepository processRepository;

    private static Specification<Process> equalId(long id) {
        return (process, cq, cb) -> cb.equal(process.get("id"), id);
    }

    private static Specification<Process> containsName(String name) {
        return (process, cq, cb) -> cb.like(process.get("name"), "%" + name + "%");
    }

    private static Specification<Process> containsComment(String comment) {
        return (process, cq, cb) -> cb.like(process.get("comment"), "%" + comment + "%");
    }

    public ProcessService(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    /**
     * Add Process
     * @param process the process will be add.
     * @return the process have be add.
     */
    Process add(Process process) {
        return processRepository.save(process);
    }

    /**
     * Update Process
     * @param process the process will be update.
     * @return the process have be update.
     */
    Process update(Process process) {
        return processRepository.save(process);
    }

    /**
     * Load Process By Id, Name, Comment
     * @param id the process id.
     * @param name the name process contains.
     * @param comment the comment process contains.
     * @param pageNumber page number.
     * @return the list of query result.
     */
    List<Process> load(Integer id, String name, String comment, Integer pageNumber) {

        ArrayList<Specification<Process>> specifications = new ArrayList<>();

        if (id != null) {
            specifications.add(equalId(id));
        }

        if (name != null && name.length() != 0) {
            specifications.add(containsName(name));
        }

        if (comment != null && comment.length() != 0) {
            specifications.add(containsComment(comment));
        }

        if (specifications.size() == 0) {
            return processRepository.findAll(PageRequest.of(pageNumber, 20)).getContent();
        }

        Specification<Process> s = specifications.get(0);
        for (int i = 1; i < specifications.size(); ++i) {
            s.and(specifications.get(i));
        }
        return processRepository.findAll(s, PageRequest.of(pageNumber, 20)).getContent();
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
