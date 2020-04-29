package com.dghysc.hy.wechat.repo;

import com.dghysc.hy.wechat.model.ScheduleMessageUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Schedule Message User Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
@Repository
public interface ScheduleMessageUserRepository
        extends CrudRepository<ScheduleMessageUser, String> { }
