package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.work.model.Work;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;

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
     * Add Or Update Work Api
     * @param request {
     *     "id": the work id: int,
     *     "name": the work name: str,
     *     "comment": the work comment: str
     * }
     * @return if add or update success return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": work data: object
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
                response.put("data", workService.add(name, comment));
                response.put("message", "创建生产流程成功");
            } else {
                response.put("data", workService.update(id, name, comment));
                response.put("message", "更新生产流程成功");
            }
            response.put("status", 1);
        } catch (NullPointerException e) {
            response.put("status", 0);
            response.put("message", "");
        } catch (DataIntegrityViolationException e) {
            response.put("status", 0);
            response.put("message", "名称为" + name + "的生产流程已存在");
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的生产流程不存在");
        }

        return response;
    }

    /**
     * Get Work Or Works Api.
     * @param id the work id.
     * @param name the name work contains.
     * @param comment the comment work contains.
     * @param pageNumber the page number.
     * @param pageSize the page size.
     * @return if id is null return {
     *     "status": 1,
     *     "message": "获取生产流程成功",
     *     "data": {
     *         "size": page size: int
     *         "total": page total number: int,
     *         "works": works: array
     *     }
     * } else if id is not null return {
     *     "status": 1,
     *     "message": "获取生产流程成功",
     *     "data": work data: object
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
            Page<Work> page = workService.load(likeMap, pageNumber, pageSize);
            data.put("size", page.getSize());
            data.put("total", page.getTotalPages());
            data.put("works", page.getContent());

            response.put("data", data);
        } else {
            response.put("data", workService.loadById(id));
        }

        response.put("status", 1);
        response.put("message", "获取生产流程成功");

        return response;
    }

    /**
     * Delete Work Api
     * @param id the work id.
     * @return if delete success return {
     *     "status": 1,
     *     "message": "删除生产流程成功"
     * } else return {
     *     "status": 1,
     *     "message": error message: str
     * }
     */
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject delete(@RequestParam Integer id) {
        JSONObject response = new JSONObject();

        try {
            workService.removeById(id);

            response.put("status", 1);
            response.put("message", "删除生产流程成功");
        } catch (EmptyResultDataAccessException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的生产流程不存在");
        }

        return response;
    }
}
