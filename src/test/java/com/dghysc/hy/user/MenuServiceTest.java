package com.dghysc.hy.user;

import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.ParentMenu;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.ChildMenuRepository;
import com.dghysc.hy.user.repo.ParentMenuRepository;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.util.TestUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MenuServiceTest {

    private int parentId;

    private int childId;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private MenuService menuService;

    @Autowired
    private ChildMenuRepository childMenuRepository;

    @Autowired
    private ParentMenuRepository parentMenuRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Before
    public  void initTest() throws Exception {
        User user = new User();
        user.setId(testUtil.nextId(User.class));

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, null
                )
        );

        if (parentMenuRepository.count() == 0) {
            addParent();
        }

        parentId = testUtil.nextId(ParentMenu.class);

        if (childMenuRepository.count() == 0) {
            addChild();
        }

        childId = testUtil.nextId(ChildMenu.class);
    }

    @Test
    public void addParent() {
        String name = RandomString.make();
        Integer location = testUtil.nextInt();
        ParentMenu parentMenu = menuService.addParent(name, location);

        assertNotNull(parentMenu.getId());
        assertEquals(name, parentMenu.getName());

        boolean mark = false;

        try {
            menuService.addParent(null, null);
        } catch (NullPointerException e) {
            mark = true;
        }

        assertTrue(mark);
    }

    @Test
    public void updateParent() {
        Integer id = parentId;
        String name = RandomString.make();
        Integer location = testUtil.nextInt();

        ParentMenu parentMenu = menuService.updateParent(id, name, location);

        assertEquals(parentMenu.getName(), name);

        boolean mark = false;

        try {
            menuService.updateParent(null, name, location);
        } catch (NullPointerException e) {
            mark = true;
        }

        assertTrue(mark);
    }

    @Test
    public void updateParentsLocation() {
        List<ParentMenu> parentMenus = parentMenuRepository.findAll();
        Map<Integer, Integer> map = new HashMap<>();

        parentMenus.forEach(parentMenu -> map.put(parentMenu.getId(), testUtil.nextInt()));

        List<ParentMenu> results = menuService.updateParentsLocation(map);

        results.forEach(parentMenu -> assertEquals(
                map.get(parentMenu.getId()), parentMenu.getLocation()
        ));
    }

    @Test
    public void removeParentById() {
        Integer id = parentId;
        menuService.removeParentById(id);
        assertFalse(parentMenuRepository.existsById(id));
    }

    @Test
    public void loadParentMenuById() {
        Integer id = parentId;

        ParentMenu parentMenu = menuService.loadParentMenuById(id);
        assertNotNull(parentMenu);
        assertEquals(id, parentMenu.getId());
    }

    @Test
    public void loadAllParentMenus() {
        assertEquals(parentMenuRepository.count(), menuService.loadAllParentMenus().size());
    }

    @Test
    public void addChild() throws Exception {
        String name = RandomString.make();
        String url = "/" + RandomString.make();
        Integer location = testUtil.nextInt();
        Integer parentId = this.parentId;

        ChildMenu childMenu = menuService.addChild(name, url, location, parentId, null);

        assertNotNull(childMenu.getId());
        assertEquals(childMenu.getName(), name);
        assertEquals(childMenu.getUrl(), url);
        assertEquals(childMenu.getParent().getId(), parentId);

        final long roleCount = roleRepository.count();

        if (roleCount > 0) {
            name = RandomString.make();
            url = "/" + RandomString.make();
            Set<Integer> roleIdSet = new HashSet<>();
            final long num = Math.abs(testUtil.nextLong() % roleCount) + 1;
            for (int i = 0; i < num; ++i) {
                roleIdSet.add(testUtil.nextId(Role.class));
            }

            childMenu = menuService.addChild(name, url, location, parentId, roleIdSet);
            assertEquals(roleIdSet.size(), childMenu.getRoles().size());
        }

        boolean mark = false;

        try {
            menuService.addChild(null, url, location, parentId, null);
        } catch (NullPointerException e) {
            mark = true;
        }

        assertTrue(mark);
        mark = false;

        try {
            menuService.addChild(name, null, location, parentId, null);
        } catch (NullPointerException e) {
            mark = true;
        }

        assertTrue(mark);
        mark = false;

        try {
            menuService.addChild(name, url, null, parentId, null);
        } catch (NullPointerException e) {
            mark = true;
        }

        assertTrue(mark);
        mark = false;

        try {
            menuService.addChild(name, url, location, null, null);
        } catch (NullPointerException e) {
            mark = true;
        }

        assertTrue(mark);
    }

    @Test
    public void updateChildField() {
        Integer id = childId;
        String name = RandomString.make();
        Integer location = testUtil.nextInt();
        String url = "/" + RandomString.make();

        ChildMenu childMenu = menuService.updateChild(id, name, url, location, null, null);

        assertEquals(id, childMenu.getId());
        assertEquals(name, childMenu.getName());
        assertEquals(url, childMenu.getUrl());
    }

    @Test
    public void updateChildParent() {
        Integer id = childId;
        Integer parentId = this.parentId;

        ChildMenu childMenu = menuService.updateChild(id, null, null, null, parentId, null);

        assertEquals(id, childMenu.getId());
        assertEquals(parentId, childMenu.getParent().getId());
    }

    @Test
    public void updateChildRoles() throws Exception {
        Integer id = childId;

        final long roleCount = roleRepository.count();

        if (roleCount > 0) {
            Set<Integer> roleIdSet = new HashSet<>();
            final long num = Math.abs(testUtil.nextLong() % roleCount) + 1L;
            for (int i = 0; i < num; ++i) {
                roleIdSet.add(testUtil.nextId(Role.class));
            }

            ChildMenu childMenu = menuService.updateChild(id, null, null, null, null, roleIdSet);

            assertEquals(roleIdSet.size(), childMenu.getRoles().size());
            childMenu.getRoles().forEach(
                    role -> assertTrue(roleIdSet.contains(
                            role.getId())));
        }
    }

    @Test
    public void updateChildrenLocation() {
        List<ChildMenu> childMenus = childMenuRepository.findAll();
        Map<Integer, Integer> map = new HashMap<>();

        childMenus.forEach(childMenu -> map.put(childMenu.getId(), testUtil.nextInt()));

        List<ChildMenu> results = menuService.updateChildrenLocation(map);

        results.forEach(childMenu -> assertEquals(
                map.get(childMenu.getId()), childMenu.getLocation()
        ));
    }

    @Test
    public void removeChildById() {
        Integer id = childId;
        menuService.removeChildById(id);
        assertFalse(childMenuRepository.existsById(id));
    }

    @Test
    public void loadChildMenuById() {
        Integer id = childId;

        ChildMenu childMenu = menuService.loadChildMenuById(id);
        assertNotNull(childMenu);
        assertEquals(id, childMenu.getId());
    }

    @Test
    public void loadAllChildMenus() {
        assertEquals(childMenuRepository.count(), menuService.loadAllChildMenus().size());
    }
}