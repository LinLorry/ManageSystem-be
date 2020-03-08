package com.dghysc.hy.wechat;

import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.wechat.model.WechatUser;
import com.dghysc.hy.wechat.repo.WechatUserRepository;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WechatUserServiceTest {

    @Autowired
    public WechatUserRepository wechatUserRepository;

    @Autowired
    public WechatUserService wechatUserService;

    @Autowired
    public TestUtil testUtil;

    @Before
    public void initTest() {
        if (wechatUserRepository.count() == 0) {
            WechatUser wechatUser = new WechatUser();
            wechatUser.setId(RandomString.make());
            wechatUserRepository.save(wechatUser);
        }
    }

    @Test
    public void update() throws Exception {
        String id = testUtil.nextId(WechatUser.class);
        String name = RandomString.make();

        WechatUser wechatUser = wechatUserService.update(id, name);
        assertEquals(name, wechatUser.getName());
    }

    @Test
    public void addUser() {
        String id = testUtil.nextId(WechatUser.class);
        WechatUser wechatUser;

        wechatUser = wechatUserService.addUser(id);

        assertNotNull(wechatUser.getUser());
    }
}