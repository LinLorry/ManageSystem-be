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

    boolean addWork(String name, String comment) {
        Work work = new Work();

        work.setName(name);
        work.setComment(comment);

        workRepository.save(work);

        return true;
    }

    boolean updateWork(Integer id, String name, String comment) {
        Optional<Work> optionalWork = workRepository.findById(id);
        if (!optionalWork.isPresent()) {
            return false;
        }

        Work work = optionalWork.get();
        work.setName(name);
        work.setComment(comment);
        work.setUpdateDate(new Date(new java.util.Date().getTime()));

        workRepository.save(work);
        return true;
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

}
