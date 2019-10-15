package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/work")
public class WorkController {
    private final Log logger = LogFactory.getLog(this.getClass());

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    @ResponseBody
    @PostMapping("/add")
    public JSONObject add(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();

        if (workService.addWork(
                request.getString("name"),
                request.getString("comment")
        )) {
            result.put("status", 1);
            result.put("message", "Add Work Success");
        } else {
            result.put("status", 0);
            result.put("message", "Add Work failed");
        }

        return result;
    }

    @ResponseBody
    @PostMapping("/update")
    public JSONObject update(@RequestBody JSONObject request) {
        JSONObject result = new JSONObject();

        if (workService.updateWork(
                request.getInteger("id"),
                request.getString("name"),
                request.getString("comment")
        )) {
            result.put("status", 1);
            result.put("message", "Update Work Success");
        } else {
            result.put("status", 0);
            result.put("message", "Update Work Failed");
        }

        return result;
    }

    @ResponseBody
    @GetMapping("/getWorks")
    public JSONObject getWorks(@RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject result = new JSONObject();

        result.put("data", workService.getWorks(pageNumber));
        result.put("status", 1);
        result.put("message", "Get Works Success");

        return result;
    }

    @ResponseBody
    @GetMapping("/getWork")
    public JSONObject getWork(@RequestParam Integer id) {
        JSONObject result = new JSONObject();

        try {
            result.put("data", workService.loadWork(id));
            result.put("status", 1);
            result.put("message", "Get Works Success");
        } catch (NoSuchElementException e) {
            logger.error(e);
            result.put("status", 0);
            result.put("message", "No such Work.");
        }

        return result;
    }
}
