package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * User Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT user FROM Role role inner join role.users user where role.role = :role and user.disable = false")
    Page<User> loadAllByRoleAndEnable(String role, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(user) > 0 THEN true ELSE false END " +
            "FROM Role role " +
            "INNER JOIN role.users user " +
            "WHERE role.role = :role and user.id = :userId")
    boolean existsByRoleAndUserId(String role, Long userId);
}
