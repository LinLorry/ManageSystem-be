package com.dghysc.hy.user;

import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.util.SecurityUtil;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Role Server
 * @author lorry
 * @author lin864464995@163.com
 */
@Service
@PreAuthorize("hasRole('ADMIN')")
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
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

    Role update(@NotNull Integer id, @Nullable String roleStr,
                @Nullable String name) {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        User creator = SecurityUtil.getUser();

        Role role = roleRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        Optional.ofNullable(roleStr).ifPresent(role::setRole);
        Optional.ofNullable(name).ifPresent(role::setName);

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

    boolean delete(@NotNull Integer id) {
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
