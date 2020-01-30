package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
