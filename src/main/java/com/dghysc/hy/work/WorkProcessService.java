package com.dghysc.hy.work;

import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.repo.ProcessRepository;
import com.dghysc.hy.work.repo.WorkRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
import java.util.Map;
import java.util.Optional;

@Service
public class WorkProcessService {
    private final WorkRepository workRepository;

    private final ProcessRepository processRepository;

    private final SessionFactory sessionFactory;

    @Autowired
    public WorkProcessService(WorkRepository workRepository,
                              ProcessRepository processRepository,
                              EntityManagerFactory factory) {
        this.workRepository = workRepository;
        this.processRepository = processRepository;
        if(factory.unwrap(SessionFactory.class) == null){
            throw new NullPointerException("factory is not a hibernate factory");
        }
        this.sessionFactory = factory.unwrap(SessionFactory.class);
    }

    boolean addProcessesInWork(Integer workId, Map<Integer, WorkProcess> map) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        Iterable<Process> processIterable = processRepository.findAllById(map.keySet());

        Optional<Work> workOptional = workRepository.findById(workId);
        Work work;

        if (! workOptional.isPresent()) {
            return false;
        }
        work = workOptional.get();
        for (Process process : processIterable) {
            WorkProcess workProcess = map.get(process.getId());
            workProcess.setWork(work);
            workProcess.setProcess(process);
            session.merge(workProcess);
        }

        tx.commit();

        session.close();

        return true;
    }
}
