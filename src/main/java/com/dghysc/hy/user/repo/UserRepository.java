package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

public interface UserRepository extends CrudRepository<User, BigInteger> {
    User findUserById(BigInteger id);

    User findUserByUsername(String username);

    boolean existsByUsername(String username);

    List<User> findUsersByName(String name);

}
