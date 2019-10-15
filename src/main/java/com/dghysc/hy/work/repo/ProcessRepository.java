package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.Process;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProcessRepository extends CrudRepository<Process, Integer> {
    Page<Process> findAll(Pageable pageable);

    Page<Process> findProcessesByNameContains(Pageable pageable, String name);

    boolean existsByName(String name);
}
