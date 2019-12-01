package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * Process Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/process")
public class ProcessController {
    private final Log logger = LogFactory.getLog(this.getClass());

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @ResponseBody
    @RequestMapping("/add")
    public JSONObject add(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        try {
            response.put("processId", processService.addProcess(
                    request.getString("name"),
                    request.getString("comment")
            ));
            response.put("status", 1);
            response.put("message", "Add Process Success");
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);
            if (processService.checkProcessByName(request.getString("name"))) {
                response.put("message", "Process Name Exist.");
            } else {
                response.put("message", "Add Process Failed.");
            }
        }

        return response;
    }

    @ResponseBody
    @RequestMapping("/update")
    public JSONObject update(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        try {
            processService.updateProcess(
                    request.getInteger("id"),
                    request.getString("name"),
                    request.getString("comment")
            );
            response.put("status", 1);
            response.put("message", "Update Process Success");
        } catch (NoSuchElementException e) {
            logger.error(e);
            response.put("status", 0);
            response.put("message", "No such process");
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);

            if (processService.checkProcessByName(request.getString("name"))) {
                response.put("message", "Process Name Exist.");
            } else {
                response.put("message", "Update Process Failed");
            }
        }
        return response;
    }

    @ResponseBody
    @GetMapping("/getProcesses")
    public JSONObject getProcesses(@RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject response = new JSONObject();

        response.put("data", processService.getProcesses(pageNumber));
        response.put("status", 1);
        response.put("message", "Get Works Success");

        return response;
    }

    @ResponseBody
    @GetMapping("/getProcess")
    public JSONObject getProcess(@RequestParam Integer id) {
        JSONObject response = new JSONObject();

        try {
            response.put("data", processService.loadProcess(id));
            response.put("status", 1);
            response.put("message", "Get Process Success");
        } catch (NoSuchElementException e) {
            logger.error(e);
            response.put("status", 0);
            response.put("message", "No such Process.");
        }

        return response;
    }
}
