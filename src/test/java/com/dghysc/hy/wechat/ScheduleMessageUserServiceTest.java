package com.dghysc.hy.wechat;

import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.wechat.model.ScheduleMessageUser;
import com.dghysc.hy.wechat.model.WechatUser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ScheduleMessageUserServiceTest {

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ScheduleMessageUserService scheduleMessageUserService;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_ADMIN");
    }

    @Test
    public void add() {
        scheduleMessageUserService.add(testUtil.nextId(WechatUser.class));
    }

    @Test
    public void remove() {
        scheduleMessageUserService.remove(testUtil.nextId(WechatUser.class));
    }

    @Test
    public void loadAll() {
        Iterable<ScheduleMessageUser> scheduleMessageUsers = scheduleMessageUserService
                .loadAll();
        assertNotNull(scheduleMessageUsers);

        for (ScheduleMessageUser scheduleMessageUser : scheduleMessageUsers) {
            System.out.println(scheduleMessageUser.getId());
            System.out.println(scheduleMessageUser.getWechatUser().getName());
        }
    }
}