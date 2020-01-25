package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.ParentMenu;
import com.dghysc.hy.util.TestUtil;
import net.bytebuddy.utility.RandomString;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest()
public class ParentMenuRepositoryTest {

    private Integer id;

    @Autowired
    private TestUtil testUtil;

    @Autowired
    private ParentMenuRepository parentMenuRepository;

    @Autowired
    private ChildMenuRepository childMenuRepository;

    @Before
    public void initTest() throws Exception {
        if (parentMenuRepository.count() == 0) {
            save();
        }
        id = testUtil.nextId(ParentMenu.class);
    }

    @Test
    public void save() {
        ParentMenu parentMenu = new ParentMenu();

        parentMenu.setName(RandomString.make());
        parentMenuRepository.saveAndFlush(parentMenu);

        assertNotNull(parentMenu.getId());
    }

    @Test
    @Transactional
    public void update() {
        Integer id = this.id;
        String name = RandomString.make();

        ParentMenu parentMenu = parentMenuRepository.getOne(id);

        parentMenu.setName(name);

        parentMenuRepository.saveAndFlush(parentMenu);

        parentMenu = parentMenuRepository.getOne(id);
        assertEquals(parentMenu.getName(), name);
    }

    @Test
    @Transactional
    public void updateChild() throws Exception {
        Integer id = this.id;

        ParentMenu parentMenu = parentMenuRepository.getOne(id);

        Set<ChildMenu> childMenuSet = parentMenu.getChildMenuSet();
        Set<ChildMenu> tmp = new HashSet<>();
        final int beforeCount = childMenuSet.size();

        while (childMenuSet.size() < beforeCount + 3 &&
                childMenuSet.size() != childMenuRepository.count()) {
            ChildMenu childMenu = childMenuRepository.getOne(
                    testUtil.nextId(ChildMenu.class)
            );
            childMenu.setParent(parentMenu);

            childMenuSet.add(childMenu);
            tmp.add(childMenu);
        }

        parentMenuRepository.saveAndFlush(parentMenu);

        parentMenu = parentMenuRepository.getOne(id);
        assertTrue(parentMenu.getChildMenuSet().containsAll(tmp));

        this.id = id;
    }

    @Test
    public void delete() {
        Integer id = this.id;
        ParentMenu parentMenu = parentMenuRepository.getOne(id);

        parentMenuRepository.delete(parentMenu);

        assertFalse(parentMenuRepository.existsById(id));
    }
}