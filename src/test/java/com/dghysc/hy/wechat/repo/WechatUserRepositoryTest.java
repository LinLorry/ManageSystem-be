package com.dghysc.hy.wechat.repo;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.wechat.model.WechatUser;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WechatUserRepositoryTest {

    @Autowired
    private WechatUserRepository wechatUserRepository;

    @Autowired
    private TestUtil testUtil;

    @Before
    public void initTest() {
        if (wechatUserRepository.count() == 0) {
            save();
        }
    }

    @Test
    public void save() {
        WechatUser wechatUser = new WechatUser();
        String id = RandomString.make();

        wechatUser.setId(id);

        wechatUser = wechatUserRepository.save(wechatUser);

        assertEquals(wechatUser.getId(), id);
    }

    @Test
    public void update() throws Exception {
        String id = testUtil.nextId(WechatUser.class);
        String name = RandomString.make();
        Long userId = null;
        WechatUser wechatUser = wechatUserRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        wechatUser.setName(name);

        if (wechatUser.getUser() != null) {
            wechatUser.setUser(null);
        } else if (!wechatUserRepository.existsByUserId(userId)) {
            userId = testUtil.nextId(User.class);
            User user = new User();
            user.setId(userId);
            wechatUser.setUser(user);
        }

        wechatUser = wechatUserRepository.save(wechatUser);
        assertEquals(name, wechatUser.getName());
        if (wechatUser.getUser() != null) {
            assertEquals(userId, wechatUser.getUser().getId());
        }
    }
}
