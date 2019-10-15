package com.dghysc.hy.work;

import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.WorkRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class WorkService {
    private final WorkRepository workRepository;

    public WorkService(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    Integer addWork(String name, String comment) {
        Work work = new Work();

        work.setName(name);
        work.setComment(comment);
        work.setUpdateDate(new Date(System.currentTimeMillis()));

        workRepository.save(work);
        return work.getId();
    }

    void updateWork(Integer id, String name, String comment)
            throws NoSuchElementException {
        Optional<Work> optionalWork = workRepository.findById(id);
        if (!optionalWork.isPresent()) {
            throw new NoSuchElementException("There's no Process with id " + id);
        }

        Work work = optionalWork.get();
        work.setName(name);
        work.setComment(comment);
        work.setUpdateDate(new Date(System.currentTimeMillis()));

        workRepository.save(work);
    }

    List<Work> getWorks(Integer pageNumber) {
        return workRepository.findAll(PageRequest.of(pageNumber, 20)).getContent();
    }

    Work loadWork(Integer id) throws NoSuchElementException {
        Optional<Work> optionalWork = workRepository.findById(id);
        if (optionalWork.isPresent()) {
            return optionalWork.get();
        }
        throw new NoSuchElementException("There's no Process with id " + id);
    }

    boolean checkWorkByName(String name) {
        return workRepository.existsByName(name);
    }

}
