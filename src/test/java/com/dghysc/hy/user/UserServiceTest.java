package com.dghysc.hy.user;

import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    public TestUtil testUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @Test
    public void add() {
        String username = testUtil.nextString();
        String password = "test";
        String name = testUtil.nextString();

        User user = userService.add(username, password, name);
        assertEquals(username, user.getUsername());
        assertEquals(name, user.getName());
    }

    @Test
    public void update() {
        Long id;
        do {
            id = testUtil.nextId(User.class);
        } while (id == 1);
        String username = testUtil.nextString();
        String name = testUtil.nextString();
        Set<Integer> tmp = new HashSet<>();
        while (tmp.size() != roleRepository.count() && tmp.size() < 3) {
            tmp.add(testUtil.nextId(Role.class));
        }

        User user = userService.update(id, username, name, new ArrayList<>(tmp));

        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(name, user.getName());
    }

    @Test
    public void disableAndEnable() {
        Long id = testUtil.nextId(User.class);

        userService.disable(id);

        assertTrue(userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new).getDisable()
        );

        userService.enable(id);

        assertFalse(userRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new).getDisable()
        );
    }
}