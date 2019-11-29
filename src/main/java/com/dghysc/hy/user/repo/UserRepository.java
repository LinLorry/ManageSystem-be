package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, BigInteger> {
    Optional<User> findById(BigInteger id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
