package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.work.model.Work;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
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
        JSONObject response = new JSONObject();

        String name = request.getString("name");
        String comment = request.getString("comment");

        Work work = new Work();
        work.setName(name);
        work.setComment(comment);
        work.setUpdateDate(new Date(System.currentTimeMillis()));

        try {
            response.put("workId", workService.addWork(work));
            response.put("status", 1);
            response.put("message", "Add Work Success");
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);
            if (workService.checkWorkByName(request.getString("name"))) {
                response.put("message", "Work Name Exist.");
            } else {
                response.put("message", "Add Work Failed.");
            }
        }

        return response;
    }

    @ResponseBody
    @PostMapping("/update")
    public JSONObject update(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        try {
            workService.updateWork(
                    request.getInteger("id"),
                    request.getString("name"),
                    request.getString("comment")
            );
            response.put("status", 1);
            response.put("message", "Update Work Success");
        } catch (NoSuchElementException e) {
            logger.error(e);
            response.put("status", 0);
            response.put("message", "No such work");
        } catch (Exception e) {
            logger.error(e);
            response.put("status", 0);

            if (workService.checkWorkByName(request.getString("name"))) {
                response.put("message", "Work Name Exist.");
            } else {
                response.put("message", "Update Work Failed");
            }
        }

        return response;
    }

    @ResponseBody
    @GetMapping("/getWorks")
    public JSONObject getWorks(@RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject response = new JSONObject();

        response.put("data", workService.getWorks(pageNumber));
        response.put("status", 1);
        response.put("message", "Get Works Success");

        return response;
    }

    @ResponseBody
    @GetMapping("/getWork")
    public JSONObject getWork(@RequestParam Integer id) {
        JSONObject response = new JSONObject();

        try {
            response.put("data", workService.loadWork(id));
            response.put("status", 1);
            response.put("message", "Get Works Success");
        } catch (NoSuchElementException e) {
            logger.error(e);
            response.put("status", 0);
            response.put("message", "No such Work.");
        }

        return response;
    }
}
