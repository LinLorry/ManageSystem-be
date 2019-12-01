package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.User;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * User Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.repository.CrudRepository
 */
public interface UserRepository extends CrudRepository<User, BigInteger> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
