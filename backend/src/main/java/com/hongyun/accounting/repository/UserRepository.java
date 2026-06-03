package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * 用户仓库。
 * 提供用户名/邮箱存在校验与按用户名查询。
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
