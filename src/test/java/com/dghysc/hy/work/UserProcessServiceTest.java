package com.dghysc.hy.work;

import com.dghysc.hy.exception.UserNoFoundException;
import com.dghysc.hy.exception.UserNotWorkerException;
import com.dghysc.hy.product.model.ProductProcess;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.util.TestUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.repo.ProcessRepository;
import com.dghysc.hy.work.repo.UserProcessRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserProcessServiceTest {

    @Autowired
    public TestUtil testUtil;

    @Autowired
    public ProcessRepository processRepository;

    @Autowired
    public UserProcessRepository userProcessRepository;

    @Autowired
    public UserProcessService userProcessService;

    @Before
    public void setUp() {
        testUtil.setAuthorities("ROLE_WORKER");
        User user = testUtil.getUser();

        if (processRepository.count() == 0) {
            processRepository.save(new Process(testUtil.nextString(), user));
        }
    }

    @Test
    public void updateAll() throws UserNoFoundException, UserNotWorkerException {

        Long id = testUtil.getUser().getId();
        Set<Integer> tmp = new HashSet<>();

        while (tmp.size() != processRepository.count() && tmp.size() < 3) {
            tmp.add(testUtil.nextId(Process.class));
        }

        List<Integer> processIds = new ArrayList<>(tmp);

        List<Process> processes = userProcessService.updateAll(id, processIds);

        System.out.println(processes);

        assertEquals(tmp.size(), processes.size());
    }

    @Test
    public void loadByUserId() {
        List<Process> processes = userProcessService
                .loadByUserId(testUtil.nextId(User.class));

        for (Process process : processes) {
            System.out.println(process.getId());
            System.out.println(process);
        }
    }

    @Test
    public void loadAllTodayFinish() {
        testUtil.setAuthorities("ROLE_WORKER_MANAGER");

        List<ProductProcess> productProcesses = userProcessService.loadAllTodayFinish();

        assertNotNull(productProcesses);
    }
}