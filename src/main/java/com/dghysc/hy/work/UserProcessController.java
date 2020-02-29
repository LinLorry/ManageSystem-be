package com.dghysc.hy.work;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.exception.UserNoFoundException;
import com.dghysc.hy.exception.UserNotWorkerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * User Process Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/userProcess")
@PreAuthorize("hasAnyRole('ADMIN', 'WORKER_MANAGER')")
public class UserProcessController {

    private final UserProcessService userProcessService;

    public UserProcessController(UserProcessService userProcessService) {
        this.userProcessService = userProcessService;
    }

    @GetMapping
    public JSONObject get(@RequestParam Long id) {
        JSONObject response = new JSONObject();

        response.put("status", 1);
        response.put("message", "获取用户工序成功");
        response.put("data", userProcessService.loadByUserId(id));

        return response;
    }

    @PostMapping
    public JSONObject post(@RequestBody JSONObject request)
            throws MissingServletRequestParameterException {
        JSONObject response = new JSONObject();
        Long id = Optional.of(request.getLong("id"))
                .orElseThrow(() -> new MissingServletRequestParameterException("id", "int"));
        JSONArray tmp = request.getJSONArray("processes");
        List<Integer> processIds = tmp == null ? new ArrayList<>() : tmp.toJavaList(Integer.TYPE);

        try {
            userProcessService.updateAll(id, processIds);

            response.put("data", userProcessService.loadByUserId(id));
            response.put("status", 1);
            response.put("message", "更新用户工序成功");
        } catch (UserNoFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的用户不存在");
        } catch (UserNotWorkerException e) {
            response.put("status", 0);
            response.put("message", "该用户不是生产人员");
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "工序不存在");
        }

        return response;
    }
}
