package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

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
        JSONObject result = new JSONObject();

        if (processService.addProcess(
                request.getString("name"),
                request.getString("comment")
        )) {
            result.put("status", 1);
            result.put("message", "Add Process Success");
        } else {
            result.put("status", 0);
            result.put("message", "Add Process Failed");
        }

        return result;
    }

    @ResponseBody
    @RequestMapping("/update")
    public JSONObject update(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();

        if (processService.updateProcess(
                request.getInteger("id"),
                request.getString("name"),
                request.getString("comment")
        )) {
            result.put("status", 1);
            result.put("message", "Update Process Success");
        } else {
            result.put("status", 0);
            result.put("message", "Update Process Failed");
        }

        return result;
    }

    @ResponseBody
    @GetMapping("/getProcesses")
    public JSONObject getProcesses(@RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject result = new JSONObject();

        result.put("data", processService.getProcesses(pageNumber));
        result.put("status", 1);
        result.put("message", "Get Works Success");

        return result;
    }

    @ResponseBody
    @GetMapping("/getProcess")
    public JSONObject getProcess(@RequestParam Integer id) {
        JSONObject result = new JSONObject();

        try {
            result.put("data", processService.loadProcess(id));
            result.put("status", 1);
            result.put("message", "Get Works Success");
        } catch (NoSuchElementException e) {
            logger.error(e);
            result.put("status", 0);
            result.put("message", "No such Process.");
        }

        return result;
    }
}
