package com.dghysc.hy.user;

import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.ParentMenu;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.RoleMenu;
import com.dghysc.hy.user.repo.ChildMenuRepository;
import com.dghysc.hy.user.repo.ParentMenuRepository;
import com.dghysc.hy.user.repo.RoleRepository;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
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

    ParentMenu addParent(@NotNull String name) {
        if (name == null) {
            throw new NullPointerException();
        }
        ParentMenu parentMenu = new ParentMenu();
        parentMenu.setName(name);

        return parentMenuRepository.save(parentMenu);
    }

    ParentMenu updateParent(@NotNull Integer id, @NotNull String name) {
        if (id == null || name == null) {
            throw new NullPointerException();
        }

        ParentMenu parentMenu = parentMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        parentMenu.setName(name);

        return parentMenuRepository.save(parentMenu);
    }

    void removeParentById(Integer id) {
        parentMenuRepository.deleteById(id);
    }

    ParentMenu loadParentMenuById(Integer id) {
        return parentMenuRepository.findById(id).orElse(null);
    }

    List<ParentMenu> loadAllParentMenus() {
        return parentMenuRepository.findAll();
    }

    @Transactional
    public ChildMenu addChild(@NotNull String name, @NotNull String url,
                       @NotNull Integer parentId, Iterable<Integer> roleIds) {
        if (name == null || url == null || parentId == null) {
            throw new NullPointerException();
        }

        ChildMenu childMenu = new ChildMenu();

        return addOrUpdate(childMenu, name, url, parentId, roleIds);
    }

    @Transactional
    public ChildMenu updateChild(
            Integer id, @Nullable String name,
            @Nullable String url, @Nullable Integer parentId,
            @Nullable Iterable<Integer> roleIds) {
        ChildMenu childMenu = childMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        return addOrUpdate(childMenu, name, url, parentId, roleIds);
    }

    private ChildMenu addOrUpdate(
            ChildMenu childMenu, String name,
            String url, Integer parentId,
            Iterable<Integer> roleIds) {

        Optional.ofNullable(name).ifPresent(childMenu::setName);
        Optional.ofNullable(url).ifPresent(childMenu::setUrl);

        Optional.ofNullable(parentId).ifPresent(id -> childMenu
                .setParent(parentMenuRepository.findById(id)
                        .orElseThrow(EntityNotFoundException::new)));

        Optional.ofNullable(roleIds).ifPresent(ids -> {
            List<Role> roleList = roleRepository.findAllById(roleIds);
            Set<RoleMenu> roleMenus = childMenu.getRoleMenuSet();

            roleMenus.removeIf(roleMenu -> !roleList.remove(roleMenu.getRole()));
            roleList.forEach(role -> roleMenus.add(new RoleMenu(role, childMenu)));
            roleRepository.flush();
        });

        return childMenuRepository.save(childMenu);
    }

    void removeChildById(Integer id) {
        childMenuRepository.deleteById(id);
    }

    ChildMenu loadChildMenuById(Integer id) {
        return childMenuRepository.findById(id).orElse(null);
    }

    List<ChildMenu> loadAllChildMenus() {
        return childMenuRepository.findAll();
    }
}
