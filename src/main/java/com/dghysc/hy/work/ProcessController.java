package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.until.SecurityUtil;
import com.dghysc.hy.work.model.Process;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.NoSuchElementException;

/**
 * Process Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/process")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    /**
     * Add Process Api
     * @param request {
     *     "name": process name: String[must],
     *     "comment": process comment: String
     * }
     * @return add process success return {
     *     "status": 1,
     *     "message": "Add process success."
     *     "data": {
     *         "id": process id: Integer,
     *         "name": process name: String,
     *         "comment": process comment: String,
     *         "createTime": process create time: Timestamp,
     *         "updateTime": process update time: Timestamp
     *     }
     * }
     */
    @ResponseBody
    @RequestMapping("/add")
    public JSONObject add(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        String name = request.getString("name");
        String comment = request.getString("comment");

        Process process = new Process(name,
                SecurityUtil.getUser(), new Timestamp(System.currentTimeMillis()));
        process.setComment(comment);

        try {
            response.put("data", processService.add(process));
            response.put("status", 1);
            response.put("message", "Add process success.");
        } catch (DataIntegrityViolationException e) {
            if (!processService.checkByName(name)) {
                throw e;
            }

            response.put("status", 0);
            response.put("message", "Process name exist.");
        }

        return response;
    }

    /**
     * Update Process Api
     * @param request {
     *     "id": process id: Integer
     *     "name": process name: String,
     *     "comment": process comment: String
     * }
     * @return if update success return {
     *     "status": 1,
     *     "message": "Update process success."
     *     "data": {
     *         "id": process id: Integer,
     *         "name": process name: String,
     *         "comment": process comment: String,
     *         "createTime": process create time: Timestamp,
     *         "updateTime": process update time: Timestamp
     *     }
     * }
     */
    @ResponseBody
    @RequestMapping("/update")
    public JSONObject update(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        String name = request.getString("name");
        String comment = request.getString("comment");

        try {
            Process process = processService.loadById(request.getInteger("id"));

            process.setName(name);
            process.setComment(comment);
            process.setUpdateUser(SecurityUtil.getUser());
            process.setUpdateTime(new Timestamp(System.currentTimeMillis()));

            response.put("data", processService.update(process));
            response.put("status", 1);
            response.put("message", "Update process success.");
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "No such process.");
        } catch (DataIntegrityViolationException e) {
            if (!processService.checkByName(name)) {
                throw e;
            }
            response.put("status", 0);
            response.put("message", "Process name exist.");
        }

        return response;
    }

    /**
     * Find Process Api
     * @param id the process id.
     * @param name the name process contains.
     * @param comment the comment process contains.
     * @param pageNumber the page number.
     * @return {
     *     "status": 1,
     *     "message": "Get process success.",
     *     "data": [
     *         {
     *             "id": process id: Integer,
     *             "name": process name: String,
     *             "comment": process comment: String,
     *             "createTime": process create time: Timestamp,
     *             "updateTime": process update time: Timestamp
     *         },
     *         ...
     *     ]
     * }
     */
    @ResponseBody
    @GetMapping("/find")
    public JSONObject find(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String comment,
            @RequestParam(defaultValue = "0") Integer pageNumber) {
        JSONObject response = new JSONObject();

        response.put("data", processService.load(id, name, comment, pageNumber));
        response.put("status", 1);
        response.put("message", "Get process success.");

        return response;
    }
}
