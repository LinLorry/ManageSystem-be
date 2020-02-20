package com.dghysc.hy.user;

import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.util.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class RoleServiceTest {

    private Integer roleId;

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

        if (roleRepository.count() < 2) {
            add();
        }

        do {
            roleId = testUtil.nextId(Role.class);
        } while (roleId != 1);
    }

    @Test
    public void add() {
        String roleStr = testUtil.nextString();
        String name = testUtil.nextString();
        User user = testUtil.getUser();

        Role role = roleService.add(roleStr, name);
        assertNotNull(role.getId());
        assertEquals(roleStr, role.getRole());
        assertEquals(name, role.getName());
        assertEquals(user.getId(), role.getCreateUser().getId());
        assertTrue(System.currentTimeMillis() >= role.getCreateTime().getTime());
    }

    @Test
    public void update() {
        Integer id = roleId;
        String roleStr = testUtil.nextString();
        String name = testUtil.nextString();
        List<Long> userId = new ArrayList<>();
        List<Integer> menuId = new ArrayList<>();

        userId.add(testUtil.nextId(User.class));
        menuId.add(testUtil.nextId(ChildMenu.class));

        User user = testUtil.getUser();

        Role role = roleService.update(id, roleStr, name, userId, menuId);
        assertEquals(id, role.getId());
        assertEquals(roleStr, role.getRole());
        assertEquals(name, role.getName());
        assertEquals(user.getId(), role.getUpdateUser().getId());
        assertTrue(System.currentTimeMillis() >= role.getUpdateTime().getTime());
        assertTrue(System.currentTimeMillis() >= role.getCreateTime().getTime());
    }

    @Test
    public void loadById() {
        Integer id = roleId;
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
    public void delete() {
        Integer id = roleId;
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
