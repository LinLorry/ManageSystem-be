package com.dghysc.hy.wechat;

import com.dghysc.hy.wechat.model.ScheduleMessageUser;
import com.dghysc.hy.wechat.repo.ScheduleMessageUserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

/**
 * Schedule Message User Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
@PreAuthorize("hasRole('ADMIN')")
public class ScheduleMessageUserService {

    private final ScheduleMessageUserRepository scheduleMessageUserRepository;

    public ScheduleMessageUserService(ScheduleMessageUserRepository scheduleMessageUserRepository) {
        this.scheduleMessageUserRepository = scheduleMessageUserRepository;
    }

    /**
     * Add User In Schedule Message User Set.
     * @param id the user wechat id, must not be {@literal null}.
     * @throws NullPointerException in case the given {@literal id} is {@literal null}
     */
    public void add(@NotNull String id) {
        ScheduleMessageUser user = new ScheduleMessageUser(id);
        scheduleMessageUserRepository.save(user);
    }

    /**
     * Remove User From Schedule Message User Set.
     * @param id the user wechat id, must not be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal id} is {@literal null}
     */
    public void remove(@NotNull String id) {
        scheduleMessageUserRepository.deleteById(id);
    }

    /**
     * Load All Schedule Message User
     * @return the schedule message users.
     */
    public Iterable<ScheduleMessageUser> loadAll() {
        return scheduleMessageUserRepository.findAll();
    }
}
