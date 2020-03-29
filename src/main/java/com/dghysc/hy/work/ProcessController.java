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
import java.util.Optional;

/**
 * Process Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/process")
@PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    /**
     * Create Or Update Process Api
     * @param request {
     *     "id": the process id: int,
     *     "name": the process name: str,
     *     "comment": the process comment: str
     * }
     * @return if create or update success return {
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
     * Get Process Or Processs Api.
     * @param id the process id.
     * @param name the name process contains.
     * @param comment the comment process contains.
     * @param pageNumber the page number.
     * @param pageSize the page size.
     * @return if id is null return {
     *     "status": 1,
     *     "message": "获取工序成功",
     *     "data": {
     *         "size": page size: int
     *         "total": page total number: int,
     *         "processes": processes: array
     *     }
     * } else if id is not null return {
     *     "status": 1,
     *     "message": "获取工序成功",
     *     "data": process data: object
     * }
     */
    @GetMapping
    public JSONObject get(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String comment,
            @RequestParam(defaultValue = "0") Integer pageNumber,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        JSONObject response = new JSONObject();

        if (id == null) {
            Map<String, Object> likeMap = new HashMap<>();

            Optional.ofNullable(name).ifPresent(value -> likeMap.put("name", value));
            Optional.ofNullable(comment).ifPresent(value -> likeMap.put("comment", value));

            JSONObject data = new JSONObject();
            Page<Process> page = processService.load(likeMap, pageNumber, pageSize);
            data.put("size", page.getSize());
            data.put("total", page.getTotalPages());
            data.put("processes", page.getContent());

            response.put("data", data);
        } else {
            try {
                response.put("data", processService.loadById(id));
            } catch (EntityNotFoundException e) {
                response.put("status", 1);
                response.put("message", "Id为" + id + "的工序不存在");
                return response;
            }
        }

        response.put("status", 1);
        response.put("message", "获取工序成功");

        return response;
    }
}
