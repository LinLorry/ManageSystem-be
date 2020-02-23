package com.dghysc.hy.util;

import com.dghysc.hy.exception.NoElementException;
import com.dghysc.hy.exception.NoRepositoryException;
import com.dghysc.hy.product.model.Product;
import com.dghysc.hy.product.rep.ProductRepository;
import com.dghysc.hy.user.model.ChildMenu;
import com.dghysc.hy.user.model.ParentMenu;
import com.dghysc.hy.user.model.Role;
import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.repo.ChildMenuRepository;
import com.dghysc.hy.user.repo.ParentMenuRepository;
import com.dghysc.hy.user.repo.RoleRepository;
import com.dghysc.hy.user.repo.UserRepository;
import com.dghysc.hy.wechat.model.WechatUser;
import com.dghysc.hy.wechat.repo.WechatUserRepository;
import com.dghysc.hy.work.model.Process;
import com.dghysc.hy.work.model.Work;
import com.dghysc.hy.work.repo.ProcessRepository;
import com.dghysc.hy.work.repo.WorkRepository;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Method;
import java.util.*;

@Component
public class TestUtil extends Random {

    @Value("${manage.authentication.name}")
    private String authenticationName;

    @Autowired
    private TokenUtil tokenUtil;

    private User user;

    private Role role;

    private Iterator<User> userIterator;

    private final RandomString randomString = new RandomString();

    private final Map<Class<?>, CrudRepository<?, ?>> map;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    public TestUtil(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ParentMenuRepository parentMenuRepository,
            WechatUserRepository wechatUserRepository,
            ChildMenuRepository childMenuRepository,
            ProcessRepository processRepository,
            WorkRepository workRepository,
            ProductRepository productRepository
    ) {

        this.map = new HashMap<>();
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;

        map.put(User.class, userRepository);
        map.put(Role.class, roleRepository);
        map.put(ParentMenu.class, parentMenuRepository);
        map.put(ChildMenu.class, childMenuRepository);

        map.put(WechatUser.class, wechatUserRepository);

        map.put(Process.class, processRepository);
        map.put(Work.class, workRepository);

        map.put(Product.class, productRepository);
    }

    public HttpHeaders getTokenHeader() {
        if (userIterator == null || !userIterator.hasNext()) {
            if (role != null) {
                userIterator = role.getUsers().iterator();
            } else {
                userIterator = userRepository.findAll().iterator();
            }
        }

        user = userIterator.next();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",
                authenticationName + " " +
                        tokenUtil.generateToken(user)
        );

        return headers;
    }

    public <T> T nextId(Class<?> aClass) {
        CrudRepository<?, ?> repository =Optional
                .ofNullable(map.get(aClass))
                .orElseThrow(NoRepositoryException::new);

        long count = repository.count();

        if (count == 0) {
            throw new NoElementException();
        }

        long randomNumber = Math.abs(nextLong()) % (count + 1);

        Iterator<?> iterator = repository.findAll().iterator();

        while (--randomNumber > 0) {
            iterator.next();
        }
        Object obj = iterator.next();
        try {
            Method method = obj.getClass().getMethod("getId");
            return (T) method.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException("This class: " + aClass.toString() +
                    " don't have getId method.");
        }
    }

    @Transactional(readOnly = true)
    public void setAuthorities() {
        if (userIterator == null || !userIterator.hasNext()) {
            Iterable<User> users = userRepository.findAll();

            users.forEach(User::getAuthorities);
            userIterator = users.iterator();
        }

        user = userIterator.next();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()));
    }

    @Transactional(readOnly = true)
    public void setAuthorities(String authority) {
        if (role == null || !role.getRole().equals(authority)) {
            role = roleRepository.findByRole(authority)
                    .orElseThrow(EntityNotFoundException::new);
            if (role.getUsers().size() == 0) {
                throw new EntityNotFoundException();
            }
            role.getUsers().forEach(User::getAuthorities);

            userIterator = role.getUsers().iterator();
        } else if (!userIterator.hasNext()) {
            userIterator = role.getUsers().iterator();
        }

        user = userIterator.next();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()));
    }

    public User getUser() {
        return user;
    }

    public String nextString() {
        return randomString.nextString();
    }
}
