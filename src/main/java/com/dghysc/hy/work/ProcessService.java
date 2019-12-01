package com.dghysc.hy.work;

import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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

    public ProcessService(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    Process add(Process process) {
        return processRepository.save(process);
    }

    Process update(Process process) {
        return processRepository.save(process);
    }

    List<Process> loadAll(Integer pageNumber) {
        return processRepository.findAll(PageRequest.of(pageNumber, 20)).getContent();
    }

    Process loadById(Integer id) throws NoSuchElementException {
        return processRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    boolean checkByName(String name) {
        return processRepository.existsByName(name);
    }
}
