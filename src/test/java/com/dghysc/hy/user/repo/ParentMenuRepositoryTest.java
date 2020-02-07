package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.ParentMenu;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ParentMenuRepositoryTest {

    private Integer id;

    private User user;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ParentMenuRepository parentMenuRepository;

    @Before
    public void initTest() {
        user = userRepository.findById(testUtil.nextId(User.class))
                .orElseThrow(EntityNotFoundException::new);

        if (parentMenuRepository.count() == 0) {
            save();
        }
        id = testUtil.nextId(ParentMenu.class);
    }

    @Test
    public void save() {
        String name = testUtil.nextString();
        String url = testUtil.nextString();
        Integer location = testUtil.nextInt();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = this.user;

        ParentMenu parentMenu = new ParentMenu();

        parentMenu.setName(name);
        parentMenu.setUrl(url);
        parentMenu.setLocation(location);
        parentMenu.setCreateTime(now);
        parentMenu.setCreateUser(user);
        parentMenu.setUpdateTime(now);
        parentMenu.setUpdateUser(user);

        parentMenuRepository.saveAndFlush(parentMenu);

        assertNotNull(parentMenu.getId());
        System.out.println(parentMenu.getCreateTime());
        System.out.println(now);
        assertEquals(now, parentMenu.getCreateTime());
        assertEquals(user.getId(), parentMenu.getCreateUser().getId());
    }

    @Test
    public void update() {
        Integer id = this.id;
        String name = testUtil.nextString();
        String url = testUtil.nextString();
        Integer location = testUtil.nextInt();
        Timestamp now = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        User user = this.user;

        ParentMenu parentMenu = parentMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        parentMenu.setName(name);
        parentMenu.setUrl(url);
        parentMenu.setLocation(location);
        parentMenu.setUpdateUser(user);
        parentMenu.setUpdateTime(now);

        parentMenuRepository.saveAndFlush(parentMenu);

        parentMenu = parentMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(parentMenu.getName(), name);
        assertEquals(now, parentMenu.getUpdateTime());
        assertTrue(System.currentTimeMillis() >= parentMenu.getCreateTime().getTime());
        assertEquals(user.getId(), parentMenu.getUpdateUser().getId());
    }

    @Test
    public void delete() {
        Integer id = this.id;
        ParentMenu parentMenu = parentMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        parentMenuRepository.delete(parentMenu);

        assertFalse(parentMenuRepository.existsById(id));
    }
}