package com.dghysc.hy.user;

import com.dghysc.hy.user.model.*;
import com.dghysc.hy.user.repo.ChildMenuRepository;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.util.SecurityUtil;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Role Server
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
@PreAuthorize("hasRole('ADMIN')")
public class RoleService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ChildMenuRepository childMenuRepository;

    public RoleService(UserRepository userRepository, RoleRepository roleRepository, ChildMenuRepository childMenuRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.childMenuRepository = childMenuRepository;
    }

    Role add(@NotNull String roleStr, @NotNull String name) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        Role role = new Role();

        Optional.of(roleStr).ifPresent(role::setRole);
        Optional.of(name).ifPresent(role::setName);

        role.setCreateTime(now);
        role.setUpdateTime(now);

        role.setCreateUser(creator);
        role.setUpdateUser(creator);

        return roleRepository.save(role);
    }

    @Transactional
    public Role update(@NotNull Integer id, @Nullable String roleStr,
                       @Nullable String name, @Nullable Iterable<Long> userId,
                       @Nullable Iterable<Integer> menuId) {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        Role role = roleRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        Optional.ofNullable(roleStr).ifPresent(role::setRole);
        Optional.ofNullable(name).ifPresent(role::setName);
        Optional.ofNullable(userId).ifPresent(ids -> {
            List<User> userList = userRepository.findAllById(ids);
            Set<User> users = role.getUsers();

            users.removeIf(user -> !userList.contains(user));
            users.addAll(userList);

            userRepository.flush();
        });

        Optional.ofNullable(menuId).ifPresent(ids -> {
            List<ChildMenu> childMenuList = childMenuRepository.findAllById(ids);
            Set<RoleMenu> roleMenus = role.getRoleMenuSet();

            roleMenus.removeIf(roleMenu -> !childMenuList.remove(roleMenu.getMenu()));
            childMenuList.forEach(childMenu -> roleMenus.add(new RoleMenu(role, childMenu)));
            childMenuRepository.flush();
        });

        role.setUpdateTime(now);
        role.setUpdateUser(creator);

        return roleRepository.save(role);
    }

    Role loadById(@NotNull Integer id) {
        return roleRepository.findByIdAndIsDeleteFalse(id).orElseThrow(EntityNotFoundException::new);
    }

    List<Role> loadAll() {
        return roleRepository.findAllByIsDeleteFalse();
    }

    @Transactional
    public boolean delete(@NotNull Integer id) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User deleter = SecurityUtil.getUser();

        Role role = roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (role.isDelete()) {
            return false;
        }

        role.setUpdateTime(now);
        role.setUpdateUser(deleter);

        role.delete();
        roleRepository.save(role);

        return true;
    }
}
