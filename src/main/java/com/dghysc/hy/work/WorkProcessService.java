package com.dghysc.hy.work;

import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.model.WorkProcessKey;
import com.dghysc.hy.work.repo.WorkProcessRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManagerFactory;
/**
 * Work Process Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class WorkProcessService {
    private final WorkProcessRepository workProcessRepository;

    private final SessionFactory sessionFactory;

    @Autowired
    public WorkProcessService(WorkProcessRepository workProcessRepository,
                              EntityManagerFactory factory) {
        this.workProcessRepository = workProcessRepository;
        if(factory.unwrap(SessionFactory.class) == null){
            throw new NullPointerException("factory is not a hibernate factory");
        }
        this.sessionFactory = factory.unwrap(SessionFactory.class);
    }

    /**
     * Add WorkProcess Service
     * @param workProcess the work process will be add.
     * @return the work process have been add.
     */
    WorkProcess add(WorkProcess workProcess) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        workProcess = (WorkProcess) session.merge(workProcess);

        tx.commit();

        session.close();

        return workProcess;
    }

    /**
     * Check Work Process By Key Service
     * @param workProcessKey the work process key.
     * @return if the work process exist return true else return false.
     */
    boolean checkByKey(WorkProcessKey workProcessKey) {
        return workProcessRepository.existsById(workProcessKey);
    }
}
