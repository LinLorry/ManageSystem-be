package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.until.SecurityUtil;
import com.dghysc.hy.work.model.Work;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Work Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/work")
public class WorkController {

    private final WorkService workService;

    public WorkController(WorkService workService) {
        this.workService = workService;
    }

    /**
     * Create Work Api
     * @param request {
     *     "name": work name: String[must],
     *     "comment": work comment: String
     * }
     * @return create work success return {
     *     "status": 1,
     *     "message": "Create work success."
     *     "data": {
     *         "id": work id: Integer,
     *         "name": work name: String,
     *         "comment": work comment: String,
     *         "createTime": work create time: Timestamp,
     *         "updateTime": work update time: Timestamp
     *     }
     * }
     */
    @ResponseBody
    @PostMapping("/create")
    public JSONObject create(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        String name = request.getString("name");
        String comment = request.getString("comment");

        Work work = new Work(name,
                SecurityUtil.getUser(), new Timestamp(System.currentTimeMillis()));
        work.setComment(comment);
        try {
            response.put("data", workService.addOrUpdate(work));
            response.put("status", 1);
            response.put("message", "Create work success.");
        } catch (DataIntegrityViolationException e) {
            if (!workService.checkByName(name)) {
                throw e;
            }

            response.put("status", 0);
            response.put("message", "Work name exist.");
        }

        return response;
    }

    /**
     * Update Work Api
     * @param request {
     *     "id": work id: Integer
     *     "name": work name: String,
     *     "comment": work comment: String
     * }
     * @return if update success return {
     *     "status": 1,
     *     "message": "Update work success."
     *     "data": {
     *         "id": work id: Integer,
     *         "name": work name: String,
     *         "comment": work comment: String,
     *         "createTime": work create time: Timestamp,
     *         "updateTime": work update time: Timestamp
     *     }
     * }
     */
    @ResponseBody
    @PostMapping("/update")
    @Transactional
    public JSONObject update(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();
        String name = request.getString("name");
        String comment = request.getString("comment");

        try {
            Work work = workService.loadById(request.getInteger("id"));

            work.setName(name);
            work.setComment(comment);
            work.setUpdateUser(SecurityUtil.getUser());
            work.setUpdateTime(new Timestamp(System.currentTimeMillis()));

            response.put("data", workService.addOrUpdate(work));
            response.put("status", 1);
            response.put("message", "Update work success.");
        } catch (NoSuchElementException e) {
            response.put("status", 0);
            response.put("message", "No such work.");
        } catch (DataIntegrityViolationException e) {
            if (!workService.checkByName(name)) {
                throw e;
            }
            response.put("status", 0);
            response.put("message", "Work name exist.");
        }

        return response;
    }

    /**
     * Find Work Api
     * @param id the work id.
     * @param name the name work contains.
     * @param comment the comment work contains.
     * @param pageNumber the page number.
     * @return {
     *     "status": 1,
     *     "message": "Get work success.",
     *     "data": [
     *         {
     *             "id": work id: Integer,
     *             "name": work name: String,
     *             "comment": work comment: String,
     *             "createTime": work create time: Timestamp,
     *             "updateTime": work update time: Timestamp
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

        Map<String, Object> equalMap = new HashMap<>();
        Map<String, Object> likeMap = new HashMap<>();

        if (id != null) {
            equalMap.put("id", id);
        }

        if (name != null) {
            likeMap.put("name", name);
        }

        if (comment != null) {
            likeMap.put("comment", comment);
        }

        response.put("data", workService.load(equalMap, likeMap, pageNumber));
        response.put("status", 1);
        response.put("message", "Get work success.");

        return response;
    }
}
