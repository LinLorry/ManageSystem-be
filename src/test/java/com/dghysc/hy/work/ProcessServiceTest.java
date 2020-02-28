package com.dghysc.hy.work;

import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ProcessServiceTest {


    @Autowired
    public TestUtil testUtil;

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    public ProcessService processService;

    @Before
    public void initTest() {
        testUtil.setAuthorities("ROLE_ADMIN");

        if (processRepository.count() == 0) {
            add();
        }
    }

    @Test
    public void add() {
        String name = testUtil.nextString();
        String comment = testUtil.nextString();

        Process process = processService.add(name, comment);

        assertEquals(name, process.getName());
        assertEquals(comment, process.getComment());

        boolean flag = false;

        try {
            processService.add(null, null);
        } catch (NullPointerException e) {
            flag = true;
        }

        assertTrue(flag);
    }
}