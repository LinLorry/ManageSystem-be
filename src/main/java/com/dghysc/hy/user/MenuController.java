package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.ParentMenu;
import org.springframework.dao.EmptyResultDataAccessException;
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
    }

    @PostMapping("/parent")
    public JSONObject createOrUpdateParent(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer id = request.getInteger("id");
        String name = request.getString("name");
        Integer location = request.getInteger("location");

        try {
            ParentMenu parentMenu;

            if (id == null) {
                parentMenu = menuService.addParent(name, location);
                response.put("message", "创建父菜单成功");
            } else {
                parentMenu = menuService.updateParent(id, name, location);
                response.put("message", "更新父菜单成功");
            }

            response.put("status", 1);
            response.put("data", parentMenu);

        } catch (EntityNotFoundException e) {
            response.put("status", 0);
            response.put("message", "Id为： " + id + "的父菜单不存在");
        } catch (NullPointerException e) {
            response.put("status", 0);
            if (name == null) {
                response.put("message", "缺少父菜单名字");
            } else if (location == null) {
                response.put("message", "缺少父菜单位置信息");
            }
        }

        return response;
    }

    @GetMapping("/parent")
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
                response.put("data", menuService.loadParentMenuById(id));
            } else {
                response.put("status", 0);
                response.put("message", "Id为：" + id + "的父菜单不存在");
            }
        }

        return response;
    }

    @DeleteMapping("/parent")
    public JSONObject deleteParent(@RequestParam Integer id) {
        JSONObject response = new JSONObject();

        try {
            menuService.removeParentById(id);

            response.put("status", 1);
            response.put("message", "删除父菜单成功");

        } catch (EmptyResultDataAccessException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的父菜单不存在");
        }
        return response;
    }

    @PostMapping("/child")
    public JSONObject createOrUpdateChild(@RequestBody JSONObject request) {
        JSONObject response = new JSONObject();

        Integer id = request.getInteger("id");
        String name = request.getString("name");
        String url = request.getString("url");
        Integer location = request.getInteger("location");
        Integer parentId = request.getInteger("parentId");
        List<Integer> roleIds = request.getJSONArray("roles").toJavaList(Integer.TYPE);

        try {
            ChildMenu childMenu;

            if (id == null) {
                childMenu = menuService.addChild(name, url, location, parentId, roleIds);
                response.put("message", "创建子菜单成功");
            } else {
                childMenu = menuService.updateChild(id, name, url, location, parentId, roleIds);
                response.put("message", "更新子菜单成功");
            }

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
    public JSONObject deleteChild(@RequestParam Integer id) {
        JSONObject response = new JSONObject();

        try {
            menuService.removeChildById(id);

            response.put("status", 1);
            response.put("message", "删除子菜单成功");

        } catch (EmptyResultDataAccessException e) {
            response.put("status", 0);
            response.put("message", "Id为" + id + "的子菜单不存在");
        }
        return response;
    }
}
