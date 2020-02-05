package com.dghysc.hy.wechat;

import com.dghysc.hy.exception.DuplicateUserException;
import com.dghysc.hy.exception.WechatUserExistException;
import com.dghysc.hy.user.model.User;
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
            add();
        }
    }

    @Test
    public void add() {
        String id = RandomString.make();
        String name = RandomString.make();
        boolean nameSet = testUtil.nextBoolean();
        WechatUser wechatUser;

        if (nameSet) {
            wechatUser = wechatUserService.add(id, name);
        } else {
            wechatUser = wechatUserService.add(id, null);
        }

        assertEquals(id, wechatUser.getId());
        if (nameSet) {
            assertEquals(name, wechatUser.getName());
        } else {
            assertNull(wechatUser.getName());
        }
    }

    @Test
    public void addUserByWechatUser() throws Exception {
        String id = testUtil.nextId(WechatUser.class);
        WechatUser wechatUser;

        try {
            wechatUser = wechatUserService.addUserByWechatUser(id);
        } catch (WechatUserExistException e) {
            wechatUser = wechatUserService.loadById(id);
        }

        assertNotNull(wechatUser.getUser());
    }

    @Test
    public void update() throws Exception {
        String id = testUtil.nextId(WechatUser.class);
        String name = RandomString.make();

        WechatUser wechatUser = wechatUserService.update(id, name);
        assertEquals(name, wechatUser.getName());
    }

    @Test
    public void updateUser() throws Exception {
        String id = testUtil.nextId(WechatUser.class);
        Long userId = testUtil.nextId(User.class);

        try {
            WechatUser wechatUser = wechatUserService.updateUser(id, userId);
            assertEquals(userId, wechatUser.getUser().getId());
        } catch (DuplicateUserException e) {
            WechatUser wechatUser = wechatUserService.updateUser(id, null);
            assertNull(wechatUser.getUser());
        }
    }
}