package com.dghysc.hy.work.repo;

import com.dghysc.hy.work.model.UserProcess;
import com.dghysc.hy.work.model.UserProcessId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserProcessRepository extends JpaRepository<UserProcess, UserProcessId> {

    List<UserProcess> findAllByUserId(Long userId);

}
