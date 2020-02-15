package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.ParentMenu;
import com.dghysc.hy.util.SecurityUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * Menu Controller
 * @author lorry
 * @author lin864464995@163.com
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
        menuService.refreshMenuMap();
    }

    @GetMapping
    public JSONObject getMenu() {
        JSONObject response = new JSONObject();

        response.put("data", menuService.getMenus(SecurityUtil.getAuthorities()));
        response.put("status", 1);
        response.put("message", "获取菜单成功");

        return response;
    }

    @PostMapping("/parent")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject createOrUpdateParent(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer id = request.getInteger("id");
        String name = request.getString("name");
        String icon = request.getString("icon");
        Integer location = request.getInteger("location");

        try {
            ParentMenu parentMenu;

            if (id == null) {
                parentMenu = menuService.addParent(name, icon, location);
                response.put("message", "创建父菜单成功");
            } else {
                parentMenu = menuService.updateParent(id, name, icon, location);
                response.put("message", "更新父菜单成功");
            }
            menuService.refreshMenuMap();

            response.put("status", 1);
            response.put("data", parentMenu);

        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为： " + id + "的父菜单不存在");
        } catch (NullPointerException e) {
            response.put("status", 0);
            if (name == null) {
                response.put("message", "缺少父菜单名字");
            } else if (icon == null) {
                response.put("message", "缺少父菜单图标信息");
            } else if (location == null) {
                response.put("message", "缺少父菜单位置信息");
            }
        }

        return response;
    }

    @GetMapping("/parent")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject getParent(@RequestParam(required = false) Integer id) {
        JSONObject response = new JSONObject();

        if (id == null) {
            response.put("status", 1);
            response.put("message", "获取所有父菜单成功");
            response.put("data", menuService.loadAllParentMenus());
        } else {
            ParentMenu parentMenu = menuService.loadParentMenuById(id);
            if (parentMenu != null) {
                response.put("status", 1);
                response.put("message", "获取父菜单成功");
                response.put("data", parentMenu);
            } else {
                response.put("status", 0);
                response.put("message", "Id为：" + id + "的父菜单不存在");
            }
        }

        return response;
    }

    @DeleteMapping("/parent")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject deleteParent(@RequestParam Integer id) {
        JSONObject response = new JSONObject();

        try {
            menuService.removeParentById(id);
            menuService.refreshMenuMap();

            response.put("status", 1);
            response.put("message", "删除父菜单成功");

        } catch (EmptyResultDataAccessException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的父菜单不存在");
        }
        return response;
    }

    @PostMapping("/parent/location")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject updateParentLocation(@RequestBody List<JSONObject> request) {
        JSONObject response = new JSONObject();
        Map<Integer, Integer> map = new HashMap<>();

        request.forEach(data ->
                map.put(data.getInteger("id"), data.getInteger("location"))
        );

        try {
            response.put("data", menuService.updateParentsLocation(map));
            response.put("status", 1);
            response.put("message", "更新父菜单位置成功");
            menuService.refreshMenuMap();
        } catch (NullPointerException e) {
            response.put("status", 0);
            response.put("message", "更新父菜单位置失败");
        }
        return response;
    }

    @PostMapping("/child")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject createOrUpdateChild(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer id = request.getInteger("id");
        String name = request.getString("name");
        String url = request.getString("url");
        Integer location = request.getInteger("location");
        Integer parentId = request.getInteger("parentId");
        JSONArray roles = request.getJSONArray("roles");
        List<Integer> roleIds = roles == null ? null :
                roles.toJavaList(Integer.TYPE);

        try {
            ChildMenu childMenu;

            if (id == null) {
                childMenu = menuService.addChild(name, url, location, parentId, roleIds);
                response.put("message", "创建子菜单成功");
            } else {
                childMenu = menuService.updateChild(id, name, url, location, parentId, roleIds);
                response.put("message", "更新子菜单成功");
            }
            menuService.refreshMenuMap();

            response.put("status", 1);
            response.put("data", childMenu);

        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为： " + id + "的子菜单不存在");
        } catch (NullPointerException e) {
            response.put("status", 0);
            if (name == null) {
                response.put("message", "缺少名字");
            } else if (url == null) {
                response.put("message", "缺少Url");
            } else if (location == null) {
                response.put("message", "缺少位置");
            } else if (parentId == null) {
                response.put("message", "缺少父菜单");
            }
        }

        return response;
    }

    @GetMapping("/child")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject getChild(@RequestParam(required = false) Integer id) {
        JSONObject response = new JSONObject();

        if (id == null) {
            response.put("status", 1);
            response.put("message", "获取所有子菜单成功！");
            response.put("data", menuService.loadAllChildMenus());
        } else {
            ChildMenu childMenu = menuService.loadChildMenuById(id);
            if (childMenu != null) {
                response.put("status", 1);
                response.put("message", "获取子菜单成功！");
                response.put("data", childMenu);
            } else {
                response.put("status", 0);
                response.put("message", "Id为：" + id + "的子菜单不存在");
            }
        }

        return response;
    }

    @DeleteMapping("/child")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject deleteChild(@RequestParam Integer id) {
        JSONObject response = new JSONObject();

        try {
            menuService.removeChildById(id);
            menuService.refreshMenuMap();

            response.put("status", 1);
            response.put("message", "删除子菜单成功");

        } catch (EmptyResultDataAccessException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的子菜单不存在");
        }
        return response;
    }

    @PostMapping("/child/location")
    @PreAuthorize("hasRole('ADMIN')")
    public JSONObject updateChildLocation(@RequestBody List<JSONObject> request) {
        JSONObject response = new JSONObject();
        Map<Integer, Integer> map = new HashMap<>();

        request.forEach(data ->
                map.put(data.getInteger("id"), data.getInteger("location"))
        );

        try {
            response.put("data", menuService.updateChildrenLocation(map));
            response.put("status", 1);
            response.put("message", "更新子菜单位置成功");
            menuService.refreshMenuMap();
        } catch (NullPointerException e) {
            response.put("status", 0);
            response.put("message", "更新子菜单位置失败");
        }
        return response;
    }
}
