package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The Role Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
}
