package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findUserById(Integer id);

    User findUserByUsername(String username);

    List<User> findUsersByName(String name);

}
