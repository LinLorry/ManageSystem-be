package com.dghysc.hy.user;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.util.TestUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTest {

    @Autowired
    public TestUtil testUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

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