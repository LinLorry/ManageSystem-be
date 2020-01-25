package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TestUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.sql.Timestamp;

import static org.junit.Assert.*;

@SpringBootTest()
@RunWith(SpringRunner.class)
public class RoleRepositoryTest {

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Before
    public void initTest() throws Exception {
        if (roleRepository.count() == 0) {
            save();
        }
    }

    @Test
    public void save() throws Exception {
        String roleStr = RandomString.make();
        String name = RandomString.make();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = userRepository.findById(testUtil.nextId(User.class))
                .orElseThrow(EntityNotFoundException::new);

        Role role = new Role();
        role.setRole(roleStr);
        role.setName(name);
        role.setCreateUser(user);
        role.setUpdateUser(user);
        role.setCreateTime(now);
        role.setUpdateTime(now);

        roleRepository.saveAndFlush(role);
        assertNotNull(role.getId());
        assertEquals(now, role.getCreateTime());
        assertEquals(user, role.getCreateUser());
    }

    @Test
    @Transactional
    public void update() throws Exception {
        Role role = roleRepository.getOne(testUtil.nextId(Role.class));
        String name = RandomString.make();
        String roleStr = RandomString.make();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = userRepository.findById(testUtil.nextId(User.class))
                .orElseThrow(EntityNotFoundException::new);

        role.setRole(roleStr);
        role.setName(name);
        role.setUpdateTime(now);
        role.setUpdateUser(user);
        role.delete();

        role = roleRepository.saveAndFlush(role);

        assertEquals(roleStr, role.getRole());
        assertEquals(name, role.getName());
        assertEquals(now, role.getUpdateTime());
        assertEquals(user, role.getUpdateUser());
        assertTrue(role.isDelete());
    }
}
