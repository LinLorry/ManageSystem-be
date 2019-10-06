package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface WorkRepository extends CrudRepository<Work, Integer> {
    Page<Work> findAll(Pageable pageable);

    Page<Work> findAllByNameContaining(Pageable pageable, String name);
}
