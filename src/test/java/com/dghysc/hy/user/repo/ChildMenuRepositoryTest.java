package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.*;
import com.dghysc.hy.util.TestUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ChildMenuRepositoryTest {

    private Integer id;

    private User user;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ParentMenuRepository parentMenuRepository;

    @Autowired
    private ChildMenuRepository childMenuRepository;

    @Before
    @Transactional
    public void initTest() throws Exception {
        user = userRepository.findById(testUtil.nextId(User.class))
                .orElseThrow(EntityNotFoundException::new);
        if (parentMenuRepository.count() == 0) {
            ParentMenu parentMenu = new ParentMenu();
            parentMenu.setName(RandomString.make());

            parentMenuRepository.save(parentMenu);
        }

        if (childMenuRepository.count() == 0) {
            save();
        }

        id = testUtil.nextId(ChildMenu.class);
    }

    private void beforeRole() throws Exception {
        ChildMenu childMenu = childMenuRepository.getOne(id);
        if (childMenu.getRoleMenuSet().size() == 0) {
            addRole();
        }
    }

    @Test
    @Transactional
    public void save() throws Exception {
        String name = RandomString.make();
        String url = "/" + RandomString.make();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = this.user;
        ChildMenu childMenu = new ChildMenu();

        childMenu.setName(name);
        childMenu.setUrl(url);
        childMenu.setCreateTime(now);
        childMenu.setCreateUser(user);
        childMenu.setUpdateTime(now);
        childMenu.setUpdateUser(user);

        parentMenuRepository.findById(
                testUtil.nextId(ParentMenu.class)
        ).ifPresent(childMenu::setParent);

        Set<RoleMenu> roleMenuSet = childMenu.getRoleMenuSet();

        while (roleMenuSet.size() < 3 &&
                roleMenuSet.size() != roleRepository.count()) {
            RoleMenu roleMenu = new RoleMenu(
                    roleRepository.getOne(testUtil.nextId(Role.class)),
                    childMenu
            );

            roleMenuSet.add(roleMenu);
        }

        childMenuRepository.saveAndFlush(childMenu);

        assertNotNull(childMenu.getId());
        assertEquals(user.getId(), childMenu.getCreateUser().getId());
        assertEquals(now, childMenu.getCreateTime());
    }

    @Test
    @Transactional
    public void update() {
        Integer id = this.id;
        String name = RandomString.make();
        String url = "/" + RandomString.make();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = this.user;

        ChildMenu childMenu = childMenuRepository.getOne(id);

        childMenu.setName(name);
        childMenu.setUrl(url);
        childMenu.setUpdateTime(now);
        childMenu.setUpdateUser(user);

        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.getOne(id);

        assertEquals(name, childMenu.getName());
        assertEquals(url, childMenu.getUrl());
        assertEquals(now, childMenu.getUpdateTime());
        assertEquals(user.getId(), childMenu.getUpdateUser().getId());
    }

    @Test
    @Transactional
    public void updateParent() throws Exception {
        Integer childId = this.id;
        Integer parentId = testUtil.nextId(ParentMenu.class);

        ChildMenu childMenu = childMenuRepository.getOne(childId);
        ParentMenu parentMenu = parentMenuRepository.getOne(parentId);

        childMenu.setParent(parentMenu);

        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.getOne(childId);

        assertEquals(childMenu.getParent().getId(), parentMenu.getId());
    }

    @Transactional
    public void addRole() throws Exception {
        Integer id = this.id;

        ChildMenu childMenu = childMenuRepository.getOne(id);

        Set<RoleMenu> roleMenuSet = childMenu.getRoleMenuSet();
        Set<RoleMenu> tmp = new HashSet<>();
        final int beforeCount = roleMenuSet.size();

        while (roleMenuSet.size() < beforeCount + 3 &&
                roleMenuSet.size() != roleRepository.count()) {
            RoleMenu roleMenu = new RoleMenu(
                    roleRepository.getOne(testUtil.nextId(Role.class)),
                    childMenu
            );

            roleMenuSet.add(roleMenu);
            tmp.add(roleMenu);
        }

        roleRepository.flush();
        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.getOne(id);
        assertTrue(childMenu.getRoleMenuSet().containsAll(tmp));

        this.id = id;
    }

    @Test
    @Transactional
    public void removeRole() throws Exception {
        beforeRole();
        Integer id = this.id;
        ChildMenu childMenu = childMenuRepository.getOne(id);

        Set<RoleMenu> roleMenuSet = childMenu.getRoleMenuSet();

        RoleMenu roleMenu = roleMenuSet.iterator().next();

        roleMenuSet.remove(roleMenu);

        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.getOne(id);
        assertFalse(childMenu.getRoleMenuSet().contains(roleMenu));
    }

    @Test
    @Transactional
    public void updateRole() throws Exception {
        beforeRole();
        Integer id = this.id;

        ChildMenu childMenu = childMenuRepository.getOne(id);

        Set<RoleMenu> roleMenuSet = childMenu.getRoleMenuSet();

        RoleMenu oldRoleMenu = roleMenuSet.iterator().next();
        RoleMenu newRoleMenu = new RoleMenu(null, childMenu);

        do {
            roleRepository.findById(
                    testUtil.nextId(Role.class)
            ).ifPresent(newRoleMenu::setRole);
        }
        while (roleMenuSet.contains(newRoleMenu) &&
                roleMenuSet.size() != roleRepository.count());

        roleMenuSet.add(newRoleMenu);
        roleMenuSet.remove(oldRoleMenu);

        roleRepository.flush();
        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.getOne(id);

        if (!newRoleMenu.equals(oldRoleMenu)) {
            assertTrue(childMenu.getRoleMenuSet().contains(newRoleMenu));
        }
        assertFalse(childMenu.getRoleMenuSet().contains(oldRoleMenu));
    }

    @Test
    public void delete() {
        Integer id = this.id;

        ChildMenu childMenu = childMenuRepository.getOne(id);

        childMenuRepository.delete(childMenu);

        assertFalse(childMenuRepository.existsById(id));
    }
}