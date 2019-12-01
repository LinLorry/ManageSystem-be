package com.dghysc.hy.work;

import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    Integer addProcess(String name, String comment) {
        Process process = new Process();

        process.setName(name);
        process.setComment(comment);
        process.setUpdateTime(new Date(System.currentTimeMillis()));

        processRepository.save(process);
        return process.getId();
    }

    void updateProcess(Integer id, String name, String comment)
            throws NoSuchElementException {
        Optional<Process> optionalProcess = processRepository.findById(id);
        if (!optionalProcess.isPresent()) {
            throw new NoSuchElementException("There's no Process with id " + id);
        }

        Process process = optionalProcess.get();
        process.setName(name);
        process.setComment(comment);
        process.setUpdateTime(new Date(System.currentTimeMillis()));

        processRepository.save(process);
    }

    List<Process> getProcesses(Integer pageNumber) {
        return processRepository.findAll(PageRequest.of(pageNumber, 20)).getContent();
    }

    Process loadProcess(Integer id) throws NoSuchElementException {
        Optional<Process> optionalProcess = processRepository.findById(id);
        if (optionalProcess.isPresent()) {
            return optionalProcess.get();
        }
        throw new NoSuchElementException("There's no Process with id " + id);
    }

    boolean checkProcessByName(String name) {
        return processRepository.existsByName(name);
    }
}
