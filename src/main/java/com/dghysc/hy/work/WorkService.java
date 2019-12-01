package com.dghysc.hy.work;

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

    private static Specification<Work> equalId(long id) {
        return (work, cq, cb) -> cb.equal(work.get("id"), id);
    }

    private static Specification<Work> containsName(String name) {
        return (work, cq, cb) -> cb.like(work.get("name"), "%" + name + "%");
    }

    private static Specification<Work> containsComment(String comment) {
        return (work, cq, cb) -> cb.like(work.get("comment"), "%" + comment + "%");
    }

    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
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
     * @param id the work id.
     * @param name the name work contains.
     * @param comment the comment work contains.
     * @param pageNumber page number.
     * @return the list of query result.
     */
    List<Work> load(Integer id, String name,
                    String comment, Integer pageNumber) {
        ArrayList<Specification<Work>> specifications = new ArrayList<>();

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
            return workRepository.findAll(PageRequest.of(pageNumber, 20)).getContent();
        }

        Specification<Work> s = specifications.get(0);
        for (int i = 1; i < specifications.size(); ++i) {
            s.and(specifications.get(i));
        }
        return workRepository.findAll(s, PageRequest.of(pageNumber, 20)).getContent();
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
