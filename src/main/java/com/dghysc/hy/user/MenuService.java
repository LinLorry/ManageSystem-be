package com.dghysc.hy.user;

import com.dghysc.hy.user.model.*;
import com.dghysc.hy.user.repo.ChildMenuRepository;
import com.dghysc.hy.user.repo.ParentMenuRepository;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.util.SecurityUtil;
import org.springframework.lang.Nullable;
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

    public MenuService(
            RoleRepository roleRepository,
            ParentMenuRepository parentMenuRepository,
            ChildMenuRepository childMenuRepository) {
        this.roleRepository = roleRepository;
        this.parentMenuRepository = parentMenuRepository;
        this.childMenuRepository = childMenuRepository;
    }

    ParentMenu addParent(@NotNull String name, @NotNull Integer location) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        ParentMenu parentMenu = new ParentMenu();
        Optional.of(name).ifPresent(parentMenu::setName);
        Optional.of(location).ifPresent(parentMenu::setLocation);

        parentMenu.setCreateTime(now);
        parentMenu.setCreateUser(creator);
        parentMenu.setUpdateTime(now);
        parentMenu.setUpdateUser(creator);

        return parentMenuRepository.save(parentMenu);
    }

    ParentMenu updateParent(@NotNull Integer id, @Nullable String name, @Nullable Integer location) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        if (id == null) throw new NullPointerException();

        ParentMenu parentMenu = parentMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        Optional.ofNullable(name).ifPresent(parentMenu::setName);
        Optional.ofNullable(location).ifPresent(parentMenu::setLocation);

        parentMenu.setUpdateTime(now);
        parentMenu.setUpdateUser(creator);

        return parentMenuRepository.save(parentMenu);
    }

    List<ParentMenu> updateParentsLocation(Map<Integer, Integer> data) {
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

    void removeParentById(Integer id) {
        parentMenuRepository.deleteById(id);
    }

    ParentMenu loadParentMenuById(Integer id) {
        return parentMenuRepository.findById(id).orElse(null);
    }

    List<ParentMenu> loadAllParentMenus() {
        return parentMenuRepository.findAllByOrderByLocationAsc();
    }

    @Transactional
    public ChildMenu addChild(@NotNull String name, @NotNull String url,
                              @NotNull Integer location, @NotNull Integer parentId,
                              @Nullable Iterable<Integer> roleIds) {
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

    @Transactional
    public ChildMenu updateChild(
            Integer id, @Nullable String name,
            @Nullable String url, @Nullable Integer location,
            @Nullable Integer parentId, @Nullable Iterable<Integer> roleIds) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = SecurityUtil.getUser();

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

    @Transactional
    void removeChildById(Integer id) {
        childMenuRepository.deleteById(id);
    }

    ChildMenu loadChildMenuById(Integer id) {
        return childMenuRepository.findById(id).orElse(null);
    }

    List<ChildMenu> loadAllChildMenus() {
        return childMenuRepository.findAllByOrderByLocationAsc();
    }
}
