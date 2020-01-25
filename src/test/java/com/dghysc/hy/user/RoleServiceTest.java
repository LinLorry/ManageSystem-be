package com.dghysc.hy.user;

import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.util.TestUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class RoleServiceTest {

    @Autowired
    public TestUtil testUtil;

    @Autowired
    public RoleService roleService;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    public UserRepository userRepository;

    @Before
    public void initTest() {
        testUtil.setAuthorities("ROLE_ADMIN");
        if (roleRepository.count() == 0) {
            add();
        }
    }

    @Test
    public void add() {
        String roleStr = RandomString.make();
        String name = RandomString.make();

        Role role = roleService.add(roleStr, name);
        assertNotNull(role.getId());
        assertEquals(roleStr, role.getRole());
        assertEquals(name, role.getName());
        assertEquals(testUtil.getUser().getId(), role.getCreateUser().getId());
        assertTrue(System.currentTimeMillis() >= role.getCreateTime().getTime());
    }

    @Test
    public void update() throws Exception {
        Integer id = testUtil.nextId(Role.class);
        String roleStr = RandomString.make();
        String name = RandomString.make();

        Role role = roleService.update(id, roleStr, name);
        assertEquals(id, role.getId());
        assertEquals(roleStr, role.getRole());
        assertEquals(name, role.getName());
        assertEquals(testUtil.getUser().getId(), role.getUpdateUser().getId());
        assertTrue(System.currentTimeMillis() >= role.getUpdateTime().getTime());
        assertTrue(System.currentTimeMillis() > role.getCreateTime().getTime());
    }

    @Test
    public void loadById() throws Exception {
        Integer id = testUtil.nextId(Role.class);
        Role role = roleRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        try {
            Role tmp = roleService.loadById(id);

            assertEquals(id, tmp.getId());
        } catch (EntityNotFoundException e) {
            assertTrue(role.isDelete());
        }
    }

    @Test
    public void loadAll() {
        List<Role> roles = roleService.loadAll();

        roles.forEach(role -> assertFalse(role.isDelete()));
    }

    @Test
    public void delete() throws Exception {
        Integer id = testUtil.nextId(Role.class);
        System.out.println(id);
        Role role = roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        if (role.isDelete()) {
            assertFalse(roleService.delete(id));
        } else {
            assertTrue(roleService.delete(id));
        }

        role = roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        assertTrue(role.isDelete());
    }
}
