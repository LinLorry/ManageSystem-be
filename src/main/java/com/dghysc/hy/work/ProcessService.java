package com.dghysc.hy.work;

import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProcessService {
    private final ProcessRepository processRepository;

    public ProcessService(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    boolean addProcess(String name, String comment) {
        Process process = new Process();
        process.setName(name);
        process.setComment(comment);

        processRepository.save(process);
        return true;
    }

    boolean updateProcess(Integer id, String name, String comment) {
        Optional<Process> optionalProcess = processRepository.findById(id);
        if (!optionalProcess.isPresent()) {
            return false;
        }

        Process process = optionalProcess.get();
        process.setName(name);
        process.setComment(comment);
        process.setUpdateTime(new Date(new java.util.Date().getTime()));

        processRepository.save(process);

        return true;
    }

    List<Process> getProcesses() {
        List<Process> processes = new ArrayList<>();
        for (Process process : processRepository.findAll()) {
            processes.add(process);
        }

        return processes;
    }
}
