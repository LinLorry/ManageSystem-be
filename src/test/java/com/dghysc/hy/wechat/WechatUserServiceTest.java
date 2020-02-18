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
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

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
    public void disable() {
        String id = testUtil.nextId(WechatUser.class);

        WechatUser wechatUser = wechatUserService.disable(id);

        assertNull(wechatUser.getUser());
    }

    @Test
    public void loadByName() {
        String id = testUtil.nextId(WechatUser.class);
        WechatUser wechatUser = wechatUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Page<WechatUser> wechatUsers = wechatUserService.loadAllByName(
                wechatUser.getName(), Math.abs(testUtil.nextInt()), 5);
        System.out.println(wechatUsers.getContent());
        assertEquals(wechatUserRepository.count(), wechatUsers.getTotalElements());
    }
}