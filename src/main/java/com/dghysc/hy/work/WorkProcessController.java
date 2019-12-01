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
            response.put("data", workProcessService.add(workProcess));
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
}
