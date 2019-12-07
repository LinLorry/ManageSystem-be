package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.model.WorkProcessKey;
import com.dghysc.hy.work.repo.WorkProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Work Process Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class WorkProcessService {
    private final WorkProcessRepository workProcessRepository;

    private EntityManager entityManager;

    @Autowired
    public WorkProcessService(WorkProcessRepository workProcessRepository) {
        this.workProcessRepository = workProcessRepository;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Add WorkProcess Service
     * @param workProcess the work process will be add.
     */
    void addOrUpdate(WorkProcess workProcess) {
        entityManager.persist(workProcess);
    }

    /**
     * Load Work Process Service
     * @param json {
     *     "sequenceNumber": work process's sequence number equal this value,
     *     "createTimeBefore": work process's create time before this value,
     *     "updateTimeBefore": work process's update time before this value,
     *     "createTimeAfter": work process's create time after this value,
     *     "createTimeAfter": work process's update time after this value,
     *     "work": {
     *         "id": work process's work id equal this value,
     *         "name": work process's work name contains this value,
     *         "comment": work process's work comment contains this value,
     *         "createTimeBefore": work process's work create time before this value,
     *         "updateTimeBefore": work process's work update time before this value,
     *         "createTimeAfter": work process's work create time after this value,
     *         "createTimeAfter": work process's work update time after this value
     *     },
     *     "process": {
     *         "id": work process's process id equal this value,
     *         "name": work process's process name contains this value,
     *         "comment": work process's process comment contains this value,
     *         "createTimeBefore": work process's process create time before this value,
     *         "updateTimeBefore": work process's process update time before this value,
     *         "createTimeAfter": work process's process create time after this value,
     *         "createTimeAfter": work process's process update time after this value
     *     }
     * }
     * @return the load result.
     */
    Page<WorkProcess> load(JSONObject json) {

        Integer pageNumber = json.getInteger("pageNumber");
        if (pageNumber == null) {
            pageNumber = 0;
        }

        Specification<WorkProcess> specification = ((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>(createPredicates(criteriaBuilder, json, root));
            Integer sequenceNumber = json.getInteger("sequenceNumber");

            if (sequenceNumber != null)
                predicates.add(criteriaBuilder.equal(root.get("sequenceNumber"), sequenceNumber));


            JSONObject work = json.getJSONObject("work");
            JSONObject process = json.getJSONObject("process");

            if (work != null)
                predicates.addAll(createPredicates(criteriaBuilder, work, root.get("work")));

            if (process != null)
                predicates.addAll(createPredicates(criteriaBuilder, process, root.get("process")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[]{}));
        });
        return workProcessRepository.findAll(specification, PageRequest.of(pageNumber, 20));
    }

    /**
     * Create Predicates By Data
     * @param criteriaBuilder the criteria builder.
     * @param json {
     *     "id": id equal this value,
     *     "name": name contains this value,
     *     "comment": comment contains this value,
     *     "createTimeBefore": create time before this value,
     *     "updateTimeBefore": update time before this value,
     *     "createTimeAfter": create time after this value,
     *     "createTimeAfter": update time after this value
     * }
     * @param path the path.
     * @return the predicates.
     */
    private static List<Predicate> createPredicates(CriteriaBuilder criteriaBuilder, JSONObject json, Path<?> path) {
        List<Predicate> predicates = new ArrayList<>();

        Integer id = json.getInteger("id");
        String name = json.getString("name");
        String comment = json.getString("comment");
        Timestamp createTimeBefore = json.getTimestamp("createTimeBefore");
        Timestamp updateTimeBefore = json.getTimestamp("updateTimeBefore");
        Timestamp createTimeAfter = json.getTimestamp("createTimeAfter");
        Timestamp updateTimeAfter = json.getTimestamp("updateTimeAfter");

        if (id != null)
            predicates.add(criteriaBuilder.equal(path.get("id"), id));

        if (name != null && name.length() != 0)
            predicates.add(criteriaBuilder.like(path.get("name"), "%" + name+ "%"));

        if (comment != null && comment.length() != 0)
            predicates.add(criteriaBuilder.like(path.get("comment"), "%" + comment + "%"));

        if (createTimeBefore != null)
            predicates.add(criteriaBuilder.lessThan(path.get("createTime"), createTimeBefore));

        if (updateTimeBefore != null)
            predicates.add(criteriaBuilder.lessThan(path.get("createTime"), updateTimeBefore));

        if (createTimeAfter != null)
            predicates.add(criteriaBuilder.greaterThan(path.get("createTime"), createTimeBefore));

        if (updateTimeAfter != null)
            predicates.add(criteriaBuilder.greaterThan(path.get("createTime"), updateTimeAfter));

        return predicates;
    }

    WorkProcess loadByKey(WorkProcessKey key) throws NoSuchElementException {
        return workProcessRepository.findById(key).orElseThrow(NoSuchElementException::new);
    }

    /**
     * Check Work Process By Key Service
     * @param workProcessKey the work process key.
     * @return if the work process exist return true else return false.
     */
    boolean checkByKey(WorkProcessKey workProcessKey) {
        return workProcessRepository.existsById(workProcessKey);
    }

    /**
     * Remove Work Process
     * @param workProcess the work process will be remove.
     */
    void remove(WorkProcess workProcess) {
        entityManager.remove(workProcess);
    }
}
