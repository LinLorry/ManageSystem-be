package com.dghysc.hy.util;

import com.dghysc.hy.user.MenuService;
import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.ParentMenu;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.ChildMenuRepository;
import com.dghysc.hy.user.repo.ParentMenuRepository;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.user.repo.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

/**
 * Admin Command Line Runner
 * @author lorry
 * @author lin864464995@163.com
 */
@Component
public class AdminCommandLineRunner implements CommandLineRunner {

    @Value("${manage.secret.password}")
    private String salt;

    public final UserRepository userRepository;

    public final RoleRepository roleRepository;

    public final ChildMenuRepository childMenuRepository;

    public final ParentMenuRepository parentMenuRepository;

    private final MenuService menuService;

    public AdminCommandLineRunner(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ChildMenuRepository childMenuRepository,
            ParentMenuRepository parentMenuRepository,
            MenuService menuService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.childMenuRepository = childMenuRepository;
        this.parentMenuRepository = parentMenuRepository;
        this.menuService = menuService;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            User admin = new User();
            Role role = new Role();
            ParentMenu adminMenu = new ParentMenu();
            ChildMenu roleManageMenu = new ChildMenu();
            ChildMenu menuManageMenu = new ChildMenu();

            admin.setUsername("admin");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String hash = encoder.encode(salt + "password" + salt);
            admin.setPassword(hash);
            admin = userRepository.save(admin);

            role.setRole("ROLE_ADMIN");
            role.setName("Admin");
            role.setCreateUser(admin);
            role.setCreateTime(now);
            role.setUpdateUser(admin);
            role.setUpdateTime(now);
            role.getUsers().add(admin);
            role = roleRepository.save(role);

            adminMenu.setName("系统管理");
            adminMenu.setIcon("el-icon-s-tools");
            adminMenu.setLocation(10);
            adminMenu.setCreateUser(admin);
            adminMenu.setCreateTime(now);
            adminMenu.setUpdateUser(admin);
            adminMenu.setUpdateTime(now);
            adminMenu = parentMenuRepository.save(adminMenu);

            roleManageMenu.setName("权限管理");
            roleManageMenu.setUrl("/admin/roleManage");
            roleManageMenu.setLocation(0);
            roleManageMenu.setParent(adminMenu);
            roleManageMenu.setCreateUser(admin);
            roleManageMenu.setCreateTime(now);
            roleManageMenu.setUpdateUser(admin);
            roleManageMenu.setUpdateTime(now);
            roleManageMenu.getRoles().add(role);
            childMenuRepository.save(roleManageMenu);

            menuManageMenu.setName("菜单管理");
            menuManageMenu.setUrl("/admin/menuManage");
            menuManageMenu.setLocation(1);
            menuManageMenu.setParent(adminMenu);
            menuManageMenu.setCreateUser(admin);
            menuManageMenu.setCreateTime(now);
            menuManageMenu.setUpdateUser(admin);
            menuManageMenu.setUpdateTime(now);
            menuManageMenu.getRoles().add(role);
            childMenuRepository.save(menuManageMenu);

            menuService.refreshMenuMap();
        }
    }
}
