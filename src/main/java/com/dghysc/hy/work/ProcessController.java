package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.work.model.Process;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
     * Add Or Update Process Api
     * @param request {
     *     "id": the process id: int,
     *     "name": the process name: str,
     *     "comment": the process comment: str
     * }
     * @return if add or update success return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": process data: object
     * } else return {
     *     "status": 0,
     *     "message": error message: str
     * }
     */
    @PostMapping
    public JSONObject createOrUpdate(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer id = request.getInteger("id");
        String name = request.getString("name");
        String comment = request.getString("comment");

        try {
            if (id == null) {
                response.put("data", processService.add(name, comment));
                response.put("message", "创建工序成功");
            } else {
                response.put("data", processService.update(id, name, comment));
                response.put("message", "更新工序成功");
            }
            response.put("status", 1);
        } catch (NullPointerException e) {
            response.put("status", 0);
            response.put("message", "");
        } catch (DataIntegrityViolationException e) {
            response.put("status", 0);
            response.put("message", "名称为" + name + "的工序已存在");
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的工序不存在");
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
     *     "data": {
     *         "total": page total number: Integer,
     *         "processes": [
     *             {
     *                 "id": process id: Integer,
     *                 "name": process name: String,
     *                 "comment": process comment: String,
     *                 "createTime": process create time: Timestamp,
     *                 "updateTime": process update time: Timestamp
     *             },
     *             ...
     *         ]
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

        JSONObject data = new JSONObject();
        Page<Process> page = processService.load(equalMap, likeMap, pageNumber);
        data.put("total", page.getTotalPages());
        data.put("processes", page.getContent());

        response.put("data", data);
        response.put("status", 1);
        response.put("message", "Get process success.");

        return response;
    }

    /**
     * Delete Process Api.
     * @param request {
     *     "id": the process id: Long
     * }
     * @return {
     *     "status": 1,
     *     "message": "Delete process success."
     * }
     */
    @ResponseBody
    @DeleteMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject delete(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer id = Objects.requireNonNull(request.getInteger("id"));
        processService.removeById(id);

        response.put("status", 1);
        response.put("message", "Delete process success.");

        return response;
    }
}
