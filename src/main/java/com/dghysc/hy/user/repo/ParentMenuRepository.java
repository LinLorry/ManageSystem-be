package com.dghysc.hy.user.repo;

import com.dghysc.hy.user.model.ParentMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The Parent Menu Repository
 * @author lorry
 * @author lin864464995@163.com
 * @see org.springframework.data.jpa.repository.JpaRepository
 */
@Repository
public interface ParentMenuRepository extends JpaRepository<ParentMenu, Integer> {

    List<ParentMenu> findAllByOrderByLocationAsc();

}
