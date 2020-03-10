package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.UserService;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

/**
 * Worker Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/worker")
@PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
public class WorkerController {

    private final UserService userService;

    public WorkerController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get Worker Api
     * @param pageNumber the page number: int
     * @param pageSize the page size: int
     * @return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": {
     *         "size": the page size: int,
     *         "total": the page total number: int,
     *         "workers": [ user info: object... ]
     *     }
     * }
     */
    @GetMapping
    public JSONObject getWorkers(@RequestParam(defaultValue = "0") int pageNumber,
                                 @RequestParam(defaultValue = "20") int pageSize) {
        JSONObject response = new JSONObject();
        JSONObject data = new JSONObject();

        Page<User> users = userService.loadALLWorkers(pageNumber, pageSize);

        data.put("size", users.getSize());
        data.put("total", users.getTotalPages());
        data.put("workers", users.getContent());

        response.put("data", data);
        response.put("status", 1);
        response.put("message", "获取工人列表成功");

        return response;
    }

    /**
     * Update Worker Name Api
     * @param request {
     *      "id": the worker id: int,
     *      "name": the worker name: int
     * }
     * @return {
     *     "status": 1,
     *     "message": message: str,
     *     "data": user info: object
     * }
     * @throws MissingServletRequestParameterException {@code id} or {@code name} is {@literal null}
     */
    @PostMapping
    public JSONObject updateWorker(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();

        Long id = Optional.ofNullable(request.getLong("id"))
                .orElseThrow(() -> new MissingServletRequestParameterException("id", "int"));
        String name = Optional.ofNullable(request.getString("name"))
                .orElseThrow(() -> new MissingServletRequestParameterException("name", "str"));

        try {
            User user = userService.loadWithRolesById(id);

            boolean isWorker = false;
            for (Role role : user.getAuthorities()) {
                if ("ROLE_WORKER".equals(role.getRole())) {
                    isWorker = true;
                    break;
                }
            }

            if (!isWorker) throw new EntityNotFoundException();

            response.put("data", userService.update(id, null, name, null));
            response.put("status", 1);
            response.put("message", "更新员工信息成功");
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为：" + id + "的员工不存在");
        }

        return response;
    }
}
