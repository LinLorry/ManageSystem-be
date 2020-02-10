package com.dghysc.hy.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dghysc.hy.user.model.*;
import com.dghysc.hy.user.repo.ChildMenuRepository;
import com.dghysc.hy.user.repo.ParentMenuRepository;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.util.SecurityUtil;
import org.springframework.data.util.Pair;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.*;

/**
 * Menu Service
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
public class MenuService {

    private final RoleRepository roleRepository;

    private final ParentMenuRepository parentMenuRepository;

    private final ChildMenuRepository childMenuRepository;

    private static Map<ChildMenu, JSONObject> childData = new HashMap<>();

    private static Map<String, Map<ParentMenu,
            Pair<JSONObject, Set<ChildMenu>>>> menus = new HashMap<>();

    public MenuService(
            RoleRepository roleRepository,
            ParentMenuRepository parentMenuRepository,
            ChildMenuRepository childMenuRepository) {
        this.roleRepository = roleRepository;
        this.parentMenuRepository = parentMenuRepository;
        this.childMenuRepository = childMenuRepository;
    }

    /**
     * Add Parent Menu Service
     *
     * @param name     the name of the parent menu.
     * @param icon     the icon of the parent menu.
     * @param location the location of the parent menu.
     * @return the parent menu.
     */
    ParentMenu addParent(@NotNull String name, @NotNull String icon, @NotNull Integer location) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        ParentMenu parentMenu = new ParentMenu();
        Optional.of(name).ifPresent(parentMenu::setName);
        Optional.of(icon).ifPresent(parentMenu::setIcon);
        Optional.of(location).ifPresent(parentMenu::setLocation);

        parentMenu.setCreateTime(now);
        parentMenu.setCreateUser(creator);
        parentMenu.setUpdateTime(now);
        parentMenu.setUpdateUser(creator);

        return parentMenuRepository.save(parentMenu);
    }

    /**
     * Update Parent Menu Service
     *
     * @param id       the parent menu id.
     * @param name     the parent menu name.
     * @param icon     the parent menu icon.
     * @param location the parent menu location.
     * @return the updated parent menu.
     */
    ParentMenu updateParent(
            @NotNull Integer id, @Nullable String name,
            @Nullable String icon, @Nullable Integer location
    ) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        if (id == null) throw new NullPointerException();

        ParentMenu parentMenu = parentMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        Optional.ofNullable(name).ifPresent(parentMenu::setName);
        Optional.ofNullable(icon).ifPresent(parentMenu::setIcon);
        Optional.ofNullable(location).ifPresent(parentMenu::setLocation);

        parentMenu.setUpdateTime(now);
        parentMenu.setUpdateUser(creator);

        return parentMenuRepository.save(parentMenu);
    }

    /**
     * Update Parent Menus Location Service
     *
     * @param data a map of parent menu id and it location.
     * @return the update results.
     */
    List<ParentMenu> updateParentsLocation(@NotNull Map<Integer, Integer> data) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = SecurityUtil.getUser();
        data.forEach((id, location) -> {
            if (id == null || location == null) {
                throw new NullPointerException();
            }
        });

        List<ParentMenu> parentMenus = parentMenuRepository.findAllById(data.keySet());

        parentMenus.forEach(parentMenu -> {
            parentMenu.setUpdateUser(user);
            parentMenu.setUpdateTime(now);
            parentMenu.setLocation(data.get(parentMenu.getId()));
        });

        parentMenuRepository.saveAll(parentMenus);

        return parentMenus;
    }

    /**
     * Remove Parent Menu By Id Service
     *
     * @param id the parent menu id.
     */
    void removeParentById(@NotNull Integer id) {
        Optional.of(id).ifPresent(parentMenuRepository::deleteById);
    }

    /**
     * Load Parent Menu By Id Service
     *
     * @param id the parent menu id.
     * @return the parent menu.
     * @throws EntityNotFoundException if not parent menu id is {@code id}.
     */
    ParentMenu loadParentMenuById(Integer id) {
        return parentMenuRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    /**
     * Load All Parent Menus Service
     *
     * @return the list of all parent menus.
     */
    List<ParentMenu> loadAllParentMenus() {
        return parentMenuRepository.findAll();
    }

    /**
     * Add Child Menu Service
     *
     * @param name     the name of child menu.
     * @param url      the url of child menu.
     * @param location the location of child menu.
     * @param parentId the parent menu id of child menu.
     * @param roleIds  the id of roles can get child menu.
     * @return the child menu.
     */
    public ChildMenu addChild(
            @NotNull String name, @NotNull String url,
            @NotNull Integer location, @NotNull Integer parentId,
            @Nullable Iterable<Integer> roleIds
    ) {
        if (name == null || url == null || parentId == null || location == null) {
            throw new NullPointerException();
        }

        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = SecurityUtil.getUser();

        ChildMenu childMenu = new ChildMenu();
        childMenu.setCreateTime(now);
        childMenu.setCreateUser(user);
        childMenu.setUpdateTime(now);
        childMenu.setUpdateUser(user);

        return addOrUpdate(childMenu, name, url, location, parentId, roleIds);
    }

    /**
     * Update Child Menu Service
     *
     * @param id       the child menu id.
     * @param name     the child menu name.
     * @param url      the child menu url.
     * @param location the child menu location.
     * @param parentId the child menu parent menu id.
     * @param roleIds  the id of roles can get child menu.
     * @return the child menu.
     */
    @Transactional
    public ChildMenu updateChild(
            @NotNull Integer id, @Nullable String name,
            @Nullable String url, @Nullable Integer location,
            @Nullable Integer parentId, @Nullable Iterable<Integer> roleIds
    ) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = SecurityUtil.getUser();

        if (id == null) throw new NullPointerException();

        ChildMenu childMenu = childMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        childMenu.setUpdateTime(now);
        childMenu.setUpdateUser(user);

        return addOrUpdate(childMenu, name, url, location, parentId, roleIds);
    }

    private ChildMenu addOrUpdate(
            ChildMenu childMenu, String name,
            String url, Integer location,
            Integer parentId, Iterable<Integer> roleIds) {

        Optional.ofNullable(name).ifPresent(childMenu::setName);
        Optional.ofNullable(url).ifPresent(childMenu::setUrl);
        Optional.ofNullable(location).ifPresent(childMenu::setLocation);

        Optional.ofNullable(parentId).ifPresent(id -> childMenu
                .setParent(parentMenuRepository.findById(id)
                        .orElseThrow(EntityNotFoundException::new)));

        Optional.ofNullable(roleIds).ifPresent(ids -> {
            List<Role> roleList = roleRepository.findAllById(roleIds);
            Set<Role> roles = childMenu.getRoles();

            roles.removeIf(role -> !roleList.contains(role));
            roles.addAll(roleList);
        });

        return childMenuRepository.save(childMenu);
    }

    /**
     * Update Child Menus Location Service.
     *
     * @param data a map of child menu id and it location.
     * @return the update results.
     */
    List<ChildMenu> updateChildrenLocation(@NotNull Map<Integer, Integer> data) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = SecurityUtil.getUser();
        data.forEach((id, location) -> {
            if (id == null || location == null) {
                throw new NullPointerException();
            }
        });

        List<ChildMenu> childMenus = childMenuRepository.findAllById(data.keySet());

        childMenus.forEach(childMenu -> {
            childMenu.setUpdateUser(user);
            childMenu.setUpdateTime(now);
            childMenu.setLocation(data.get(childMenu.getId()));
        });

        childMenuRepository.saveAll(childMenus);

        return childMenus;
    }

    /**
     * Remove Child Menu By Id Service
     *
     * @param id the child menu id.
     */
    void removeChildById(@NotNull Integer id) {
        Optional.of(id).ifPresent(childMenuRepository::deleteById);
    }

    /**
     * Load Child Menu By Id Service
     *
     * @param id the child menu id.
     * @return the child menu.
     */
    ChildMenu loadChildMenuById(@NotNull Integer id) {
        return childMenuRepository.findById(id).orElse(null);
    }

    /**
     * Load All Child Menus Service
     *
     * @return the list of all child menus.
     */
    List<ChildMenu> loadAllChildMenus() {
        return childMenuRepository.findAll();
    }

    /**
     * Get Menus Service
     *
     * @param roles according those get menus.
     * @return the menus can be access under {@code roles}.
     */
    public Collection<JSONObject> getMenus(Collection<? extends GrantedAuthority> roles) {
        Map<ParentMenu, Set<ChildMenu>> parentTmp = new HashMap<>();

        Map<ParentMenu, JSONObject> parentMenuJSONObjectMap = new HashMap<>();

        roles.forEach(role -> {
            Map<ParentMenu, Pair<JSONObject, Set<ChildMenu>>> map = menus.get(role.getAuthority());
            map.forEach((key, value) -> {
                parentMenuJSONObjectMap.putIfAbsent(key, (JSONObject) value.getFirst().clone());
                parentTmp.putIfAbsent(key, new HashSet<>());
                parentTmp.get(key).addAll(value.getSecond());
            });
        });

        parentTmp.forEach((key, value) -> {
            JSONArray children = new JSONArray();

            value.forEach(childMenu ->
                    children.add(childData.get(childMenu))
            );
            parentMenuJSONObjectMap.get(key).put("children", children);
        });

        return parentMenuJSONObjectMap.values();
    }

    /**
     * Refresh Static Menu Map Service
     */
    @Transactional(readOnly = true)
    public void refreshMenuMap() {
        menus.clear();
        childData.clear();

        roleRepository.findAll().forEach(role -> {
            Map<ParentMenu, Pair<JSONObject, Set<ChildMenu>>> parentTmp = new HashMap<>();

            role.getMenus().forEach(childMenu -> {
                childData.computeIfAbsent(childMenu, key -> {
                    JSONObject tmp = new JSONObject();
                    tmp.put("name", key.getName());
                    tmp.put("url", key.getUrl());
                    tmp.put("location", key.getLocation());

                    return tmp;
                });

                parentTmp.computeIfAbsent(childMenu.getParent(), parentMenu -> {
                    JSONObject tmp = new JSONObject();
                    tmp.put("name", parentMenu.getName());
                    tmp.put("icon", parentMenu.getIcon());
                    tmp.put("location", parentMenu.getLocation());

                    return Pair.of(tmp, new HashSet<>());
                });

                parentTmp.get(childMenu.getParent()).getSecond().add(childMenu);
            });

            menus.put(role.getAuthority(), parentTmp);
        });
    }
}
