package com.dghysc.hy.work;

import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.ProcessRepository;
import com.dghysc.hy.work.repo.WorkRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class WorkServiceTest {

    @Autowired
    public TestUtil testUtil;

    @Autowired
    private WorkRepository workRepository;

    @Autowired
    private ProcessRepository processRepository;

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


        Set<Integer> tmp = new HashSet<>();

        while (tmp.size() != processRepository.count() && tmp.size() < 3) {
            tmp.add(testUtil.nextId(Process.class));
        }

        List<Integer> processIds = new ArrayList<>(tmp);

        Work work = workService.add(name, comment, processIds);

        assertEquals(name, work.getName());
        assertEquals(comment, work.getComment());

        boolean flag = false;

        try {
            workService.add(null, null, processIds);
        } catch (NullPointerException e) {
            flag = true;
        }

        assertTrue(flag);
    }

    @Test
    public void loadWithProcessesById() {
        Integer id = testUtil.nextId(Work.class);

        Work work = workService.loadWithProcessesById(id);

        System.out.println(work.getProcesses());
    }
}