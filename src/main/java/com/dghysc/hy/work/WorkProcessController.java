package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.until.SecurityUtil;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.model.WorkProcess;
import com.dghysc.hy.work.model.WorkProcessKey;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

/**
 * Work Process Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@Controller
@RequestMapping("/api/workProcess")
public class WorkProcessController {

    private final WorkService workService;

    private final ProcessService processService;

    private final WorkProcessService workProcessService;

    public WorkProcessController(WorkService workService,
                                 ProcessService processService,
                                 WorkProcessService workProcessService) {
        this.workService = workService;
        this.processService = processService;
        this.workProcessService = workProcessService;
    }

    /**
     * Create Work Process Api.
     * @param request {
     *     "workId": the work id: Integer,
     *     "processId": the process id: Integer,
     *     "sequenceNumber": the sequence number: Integer
     * }
     * @return if create success return {
     *     "status": 1,
     *     "message": "Add process in work success.",
     *     "data": {
     *         "workId": the work id: Integer,
     *         "workName": the work name: String,
     *         "processId": the process id: Integer,
     *         "processName": the process name: String,
     *         "sequenceNumber": the sequence number,
     *         "createTime": create time: Timestamp,
     *         "updateTime": update time: Timestamp
     *     }
     * }
     */
    @ResponseBody
    @PostMapping("/create")
    @Transactional
    public JSONObject create(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer workId = request.getInteger("workId");
        Integer processId = request.getInteger("processId");
        Integer sequenceNumber = request.getInteger("sequenceNumber");

        try {
            Work work = workService.loadById(workId);
            Process process = processService.loadById(processId);

            if (workProcessService.checkByKey(new WorkProcessKey(work, process))) {
                response.put("status", 0);
                response.put("message", "This work process is exist.");
                return response;
            }

            WorkProcess workProcess = new WorkProcess(
                    work, process, sequenceNumber,
                    SecurityUtil.getUser(),
                    new Timestamp(System.currentTimeMillis())
            );
            workProcessService.addOrUpdate(workProcess);
            response.put("data", workProcess);
            response.put("status", 1);
            response.put("message", "Add process in work success.");
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "Process isn't exist or work isn't exist.");
        } catch (InvalidDataAccessApiUsageException e) {
            response.put("status", 0);
            response.put("message", "Need processId and workId.");
        }

        return response;
    }

    /**
     * Update Work Process Api.
     * @param request {
     *     "workId": the work id: Integer,
     *     "processId": the process id: Integer,
     *     "sequenceNumber": the sequence number: Integer
     * }
     * @return if update success return {
     *     "status": 1,
     *     "message": "Update process in work success.",
     *     "data": {
     *         "workId": the work id: Integer,
     *         "workName": the work name: String,
     *         "processId": the process id: Integer,
     *         "processName": the process name: String,
     *         "sequenceNumber": the sequence number,
     *         "createTime": create time: Timestamp,
     *         "updateTime": update time: Timestamp
     *     }
     * }
     */
    @ResponseBody
    @PostMapping("/update")
    @Transactional
    public JSONObject update(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer workId = request.getInteger("workId");
        Integer processId = request.getInteger("processId");
        WorkProcessKey key = new WorkProcessKey(workId, processId);
        Integer sequenceNumber = request.getInteger("sequenceNumber");

        try {
            WorkProcess workProcess = workProcessService.loadById(key);
            workProcess.setSequenceNumber(sequenceNumber);
            workProcess.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            workProcess.setUpdateUser(SecurityUtil.getUser());

            workProcessService.addOrUpdate(workProcess);

            response.put("status", 1);
            response.put("message", "Update process in work success.");
            response.put("data", workProcess);

        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "This work process isn't exist.");
        }

        return response;
    }

    /**
     * Find Work Process Api
     * @param request {
     *     "sequenceNumber": work process's sequence number equal this value,
     *     "createTimeBefore": work process's create time before this value,
     *     "updateTimeBefore": work process's update time before this value,
     *     "createTimeAfter": work process's create time after this value,
     *     "createTimeAfter": work process's update time after this value,
     *     "work": {
     *         "id": work process's work id equal this value,
     *         "name": work process's work name contains this value,
     *         "comment": work process's work comment contains this value,
     *         "createTimeBefore": work process's work create time before this value,
     *         "updateTimeBefore": work process's work update time before this value,
     *         "createTimeAfter": work process's work create time after this value,
     *         "createTimeAfter": work process's work update time after this value
     *     },
     *     "process": {
     *         "id": work process's process id equal this value,
     *         "name": work process's process name contains this value,
     *         "comment": work process's process comment contains this value,
     *         "createTimeBefore": work process's process create time before this value,
     *         "updateTimeBefore": work process's process update time before this value,
     *         "createTimeAfter": work process's process create time after this value,
     *         "createTimeAfter": work process's process update time after this value
     *     }
     * }
     * @return {
     *     "status": 1,
     *     "message": "Get work process success.",
     *     "data": [
     *         {
     *             "workId": the work id: Integer,
     *             "workName": the work name: String,
     *             "processId": the process id: Integer,
     *             "processName": the process name: Integer,
     *             "sequenceNumber": the work process sequence number: Integer,
     *             "createTime": the work process create time: Timestamp,
     *             "updateTime": the work process update time: Timestamp
     *         }
     *     ]
     * }
     */
    @ResponseBody
    @GetMapping("/find")
    public JSONObject find(@RequestBody(required = false) JSONObject request) {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "Get work process success.");
        response.put("data", workProcessService.load(request));

        return response;
    }
}
