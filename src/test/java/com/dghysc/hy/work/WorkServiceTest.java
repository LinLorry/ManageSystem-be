package com.dghysc.hy.work;

import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.WorkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WorkServiceTest {

    @Autowired
    public TestUtil testUtil;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    public WorkService workService;

    @Before
    public void initTest() {
        testUtil.setAuthorities("ROLE_ADMIN");

        if (workRepository.count() == 0) {
            add();
        }
    }

    @Test
    public void add() {
        String name = testUtil.nextString();
        String comment = testUtil.nextString();

        Work work = workService.add(name, comment);

        assertEquals(name, work.getName());
        assertEquals(comment, work.getComment());

        boolean flag = false;

        try {
            workService.add(null, null);
        } catch (NullPointerException e) {
            flag = true;
        }

        assertTrue(flag);
    }

    @Test
    public void update() {
        Integer id = testUtil.nextId(Work.class);
        String name = testUtil.nextString();
        String comment = testUtil.nextString();

        Work work = workService.update(id, name, comment);
        assertEquals(id, work.getId());
        assertEquals(name, work.getName());
        assertEquals(comment, work.getComment());
    }
}