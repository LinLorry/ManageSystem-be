package com.dghysc.hy.work;

import com.dghysc.hy.exception.UserNoFoundException;
import com.dghysc.hy.exception.UserNotWorkerException;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.model.UserProcess;
import com.dghysc.hy.work.repo.UserProcessRepository;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.work.repo.ProcessRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * User Process Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class UserProcessService {

    private final UserRepository userRepository;

    private final ProcessRepository processRepository;

    private final UserProcessRepository userProcessRepository;

    public UserProcessService(
            UserRepository userRepository,
            ProcessRepository processRepository,
            UserProcessRepository userProcessRepository) {
        this.userRepository = userRepository;
        this.processRepository = processRepository;
        this.userProcessRepository = userProcessRepository;
    }

    /**
     * Update User Process Service
     * @param userId the user id.
     * @param processIds the process ids.
     * @return the list of processes.
     * @throws UserNoFoundException if user not find.
     * @throws UserNotWorkerException if user is not a worker.
     */
    @Transactional
    public List<Process> updateAll(@NotNull Long userId, @NotNull Collection<Integer> processIds)
            throws UserNoFoundException, UserNotWorkerException {
        User user = userRepository.findById(Optional.of(userId).get())
                .orElseThrow(UserNoFoundException::new);

        boolean flag = true;

        for (final Role role : user.getAuthorities()) {
            if ("ROLE_WORKER".equals(role.getRole())) {
                flag = false;
                break;
            }
        }

        if (flag) throw new UserNotWorkerException();

        if (processIds.size() != processRepository.countByIdIn(processIds)) {
            throw new EntityNotFoundException();
        }

        List<UserProcess> userProcesses = userProcessRepository.findAllByUserId(userId);

        List<Integer> existUserProcessesIds = new ArrayList<>();
        List<UserProcess> removeUserProcesses = new ArrayList<>();

        userProcesses.forEach(userProcess -> {
            if (!processIds.contains(userProcess.getProcessId())) {
                removeUserProcesses.add(userProcess);
            } else {
                existUserProcessesIds.add(userProcess.getProcessId());
            }
        });

        userProcessRepository.deleteAll(removeUserProcesses);
        userProcesses.removeAll(removeUserProcesses);
        processIds.removeAll(existUserProcessesIds);

        List<UserProcess> newUserProcesses = new ArrayList<>(processIds.size());

        processIds.forEach(processId ->
                newUserProcesses.add(new UserProcess(userId, processId))
        );

        userProcessRepository.saveAll(newUserProcesses);

        return toProcessList(userProcessRepository
                .findAllByUserId(Optional.of(userId).get()));
    }

    /**
     * Load All Processes By User Id
     * @param userId the user id.
     * @return the processes.
     */
    public List<Process> loadByUserId(@NotNull Long userId) {
        return toProcessList(userProcessRepository
                .findAllByUserId(Optional.of(userId).get()));
    }

    private static List<Process> toProcessList(@NotNull List<UserProcess> userProcesses) {
        List<Process> processes = new ArrayList<>(userProcesses.size());

        userProcesses.forEach(userProcess -> processes.add(userProcess.getProcess()));

        return processes;
    }
}
