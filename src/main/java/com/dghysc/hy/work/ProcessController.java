package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/process")
public class ProcessController {
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
    @GetMapping("/get")
    public JSONObject get(@RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject result = new JSONObject();

        result.put("data", processService.getProcesses(pageNumber));
        result.put("status", 1);
        result.put("message", "Get Works Success");

        return result;
    }
}
