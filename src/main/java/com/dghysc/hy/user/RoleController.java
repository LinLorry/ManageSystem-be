package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.Role;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * Role Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/role")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public JSONObject createOrUpdate(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer id = request.getInteger("id");
        String roleStr = request.getString("role");
        String name = request.getString("name");

        try {
            Role role;

            if (id == null) {
                role = roleService.add(roleStr, name);
                response.put("message", "Create role success.");
            } else {
                List<Long> userIds = request.getJSONArray("users")
                        .toJavaList(Long.TYPE);
                List<Integer> menuIds = request.getJSONArray("menus")
                        .toJavaList(Integer.TYPE);
                role = roleService.update(id, roleStr, name, userIds, menuIds);
                response.put("message", "Update role success");
            }

            response.put("status", 1);
            response.put("data", role);

        } catch (NullPointerException e) {
            response.put("status", 0);
            if (roleStr == null) {
                response.put("message", "Role is null.");
            } else if (name == null) {
                response.put("message", "Name is null.");
            }
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "The role which id is " + id + " not exist.");
        }

        return response;
    }

    @GetMapping
    public JSONObject get(@RequestParam(required = false) Integer id) {
        JSONObject response = new JSONObject();

        if (id == null) {
            response.put("status", 1);
            response.put("message", "Get all roles success.");
            response.put("data", roleService.loadAll());
        } else {
            try {
                response.put("data", roleService.loadById(id));
                response.put("status", 1);
                response.put("message", "Get role success.");
            } catch (EntityNotFoundException e) {
                response.put("status", 0);
                response.put("message", "The role which id is " + id + " not exist.");
            }
        }

        return response;
    }

    @DeleteMapping
    public JSONObject delete(@RequestParam Integer id) {
        JSONObject response = new JSONObject();

        try {
            if (roleService.delete(id)) {
                response.put("status", 1);
                response.put("message", "Delete role success.");
            } else {
                response.put("status", 0);
                response.put("message", "Delete role fail.");
            }
        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "The role which id is " + id + " not exist.");
        }

        return response;
    }
}
