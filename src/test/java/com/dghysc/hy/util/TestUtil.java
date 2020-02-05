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
import org.springframework.security.core.GrantedAuthority;
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

    private Iterator<User> userIterator;

    private final RandomString randomString = new RandomString();

    private final Map<Class, CrudRepository> map;

    private final UserRepository userRepository;

    public TestUtil(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ParentMenuRepository parentMenuRepository,
            ChildMenuRepository childMenuRepository,
            ProcessRepository processRepository,
            WorkRepository workRepository,
            ProductRepository productRepository
    ) {

        this.map = new HashMap<>();
        this.userRepository = userRepository;

        map.put(User.class, userRepository);
        map.put(Role.class, roleRepository);
        map.put(ParentMenu.class, parentMenuRepository);
        map.put(ChildMenu.class, childMenuRepository);

        map.put(Process.class, processRepository);
        map.put(Work.class, workRepository);

        map.put(Product.class, productRepository);
    }

    public HttpHeaders getTokenHeader() {
        if (user == null) setAuthorities();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization",
                authenticationName + " " +
                        tokenUtil.generateToken(user)
        );

        return headers;
    }

    public <T, E> T nextId(Class<E> aClass) {
        CrudRepository<E, T> repository = Optional
                .ofNullable(map.get(aClass))
                .orElseThrow(NoRepositoryException::new);

        long count = repository.count();

        if (count == 0) {
            throw new NoElementException();
        }

        long randomNumber = Math.abs(nextLong()) % (count + 1);

        Iterator<E> iterator = repository.findAll().iterator();

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
    public void setAuthorities(Long userId, String... authorities) {
        user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        List<GrantedAuthority> authorityList = new ArrayList<>();

        for (String authority : authorities) {
            authorityList.add((GrantedAuthority) () -> authority);
        }

        authorityList.addAll(user.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, authorityList));
    }

    public void setAuthorities(String... authorities) {
        if (userRepository.count() == 0) {
            throw new EntityNotFoundException();
        } else if (userIterator == null || !userIterator.hasNext()) {
            userIterator = userRepository.findAll().iterator();
        }

        user = userIterator.next();

        List<GrantedAuthority> authorityList = new ArrayList<>();

        for (String authority : authorities) {
            authorityList.add((GrantedAuthority) () -> authority);
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        user, null, authorityList));
    }

    public User getUser() {
        return user;
    }

    public String nextString() {
        return randomString.nextString();
    }
}
