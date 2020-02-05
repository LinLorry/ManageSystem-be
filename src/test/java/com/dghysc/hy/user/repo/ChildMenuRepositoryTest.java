package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.*;
import com.dghysc.hy.util.TestUtil;
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

@SpringBootTest
@RunWith(SpringRunner.class)
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
    public void initTest() {
        user = userRepository.findById(testUtil.nextId(User.class))
                .orElseThrow(EntityNotFoundException::new);
        if (parentMenuRepository.count() == 0) {
            ParentMenu parentMenu = new ParentMenu();
            parentMenu.setName(testUtil.nextString());

            parentMenuRepository.save(parentMenu);
        }

        if (childMenuRepository.count() == 0) {
            save();
        }

        id = testUtil.nextId(ChildMenu.class);
    }

    private void beforeRole() {
        ChildMenu childMenu = childMenuRepository.getOne(id);
        if (childMenu.getRoles().size() == 0) {
            addRole();
        }
    }

    @Test
    public void save() {
        String name = testUtil.nextString();
        String url = "/" + testUtil.nextString();
        Integer location = testUtil.nextInt();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = this.user;
        ChildMenu childMenu = new ChildMenu();

        childMenu.setName(name);
        childMenu.setUrl(url);
        childMenu.setLocation(location);
        childMenu.setCreateTime(now);
        childMenu.setCreateUser(user);
        childMenu.setUpdateTime(now);
        childMenu.setUpdateUser(user);

        parentMenuRepository.findById(
                testUtil.nextId(ParentMenu.class)
        ).ifPresent(childMenu::setParent);

        Set<Role> roles = childMenu.getRoles();

        while (roles.size() < 3 &&
                roles.size() != roleRepository.count()) {
            roles.add(roleRepository.getOne(testUtil.nextId(Role.class)));
        }

        childMenuRepository.saveAndFlush(childMenu);

        assertNotNull(childMenu.getId());
        assertEquals(user.getId(), childMenu.getCreateUser().getId());
        assertEquals(now, childMenu.getCreateTime());
    }

    @Test
    public void update() {
        Integer id = this.id;
        String name = testUtil.nextString();
        String url = "/" + testUtil.nextString();
        Integer location = testUtil.nextInt();
        Timestamp now = new Timestamp(System.currentTimeMillis() / 1000 * 1000);
        User user = this.user;

        ChildMenu childMenu = childMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        childMenu.setName(name);
        childMenu.setUrl(url);
        childMenu.setLocation(location);
        childMenu.setUpdateTime(now);
        childMenu.setUpdateUser(user);

        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(name, childMenu.getName());
        assertEquals(url, childMenu.getUrl());
        assertEquals(now, childMenu.getUpdateTime());
        assertEquals(user.getId(), childMenu.getUpdateUser().getId());
    }

    @Test
    public void updateParent() {
        Integer childId = this.id;
        Integer parentId = testUtil.nextId(ParentMenu.class);

        ChildMenu childMenu = childMenuRepository.findById(childId)
                .orElseThrow(EntityNotFoundException::new);
        ParentMenu parentMenu = parentMenuRepository.findById(parentId)
                .orElseThrow(EntityNotFoundException::new);

        childMenu.setParent(parentMenu);

        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.findById(childId)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(childMenu.getParent().getId(), parentMenu.getId());
    }

    @Transactional
    public void addRole() {
        Integer id = this.id;

        ChildMenu childMenu = childMenuRepository.getOne(id);

        Set<Role> roles = childMenu.getRoles();
        Set<Role> tmp = new HashSet<>();

        final int beforeCount = roles.size();

        while (roles.size() < beforeCount + 3 &&
                roles.size() != roleRepository.count()) {
            Role role = roleRepository.getOne(testUtil.nextId(Role.class));
            roles.add(role);
            tmp.add(role);
        }

        roleRepository.flush();
        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.getOne(id);
        assertTrue(childMenu.getRoles().containsAll(tmp));

        this.id = id;
    }

    @Test
    @Transactional
    public void removeRole() {
        beforeRole();
        Integer id = this.id;
        ChildMenu childMenu = childMenuRepository.getOne(id);

        Set<Role> roles = childMenu.getRoles();
        Role role = roles.iterator().next();
        roles.remove(role);

        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.getOne(id);
        assertFalse(childMenu.getRoles().contains(role));
    }

    @Test
    @Transactional
    public void updateRole() {
        beforeRole();
        Integer id = this.id;

        ChildMenu childMenu = childMenuRepository.getOne(id);

        Set<Role> roles = childMenu.getRoles();

        Role oldRole = roles.iterator().next();
        Role newRole;

        do {
            newRole = roleRepository.getOne(testUtil.nextId(Role.class));
        } while (newRole.equals(oldRole) &&
                roles.size() != roleRepository.count());

        roles.add(newRole);
        roles.remove(oldRole);

        childMenuRepository.saveAndFlush(childMenu);

        childMenu = childMenuRepository.getOne(id);

        if (!newRole.equals(oldRole)) {
            assertTrue(childMenu.getRoles().contains(newRole));
        }
        assertFalse(childMenu.getRoles().contains(oldRole));
    }

    @Test
    public void delete() {
        Integer id = this.id;

        ChildMenu childMenu = childMenuRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

        System.out.println(childMenu.getUpdateUser().getId());

        childMenuRepository.delete(childMenu);

        assertFalse(childMenuRepository.existsById(id));
    }
}