package com.hongyun.accounting.repository;

import com.hongyun.accounting.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * 账户仓库。
 * 支持按用户查询其账户列表。
 */
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserId(Long userId);
}
