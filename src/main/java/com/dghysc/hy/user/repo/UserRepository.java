package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.User;
import com.dghysc.hy.user.model.UserKey;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, UserKey> {
    User findUserById(Integer id);

    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findUsersByName(String name);

}
