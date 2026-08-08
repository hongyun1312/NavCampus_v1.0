package com.hongyun.navcampus.finance.service;

import com.hongyun.navcampus.finance.entity.Account;
import com.hongyun.navcampus.system.entity.User;
import com.hongyun.navcampus.finance.mapper.AccountMapper;
import com.hongyun.navcampus.system.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账户服务。
 * 提供账户 CRUD 与当前登录用户的账户访问。
 */
@Service
public class AccountService {
    @Autowired
    private AccountMapper accountMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 获取当前登录用户。
     */
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userMapper.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    /**
     * 查询当前用户的全部账户。
     */
    public List<Account> getAllAccounts() {
        return accountMapper.findByUserId(getCurrentUser().getId());
    }

    /**
     * 创建账户。
     */
    public Account createAccount(Account account) {
        account.setUser(getCurrentUser());
        return accountMapper.save(account);
    }
    
    /**
     * 更新账户（校验归属）。
     */
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountMapper.findById(id).orElseThrow();
        // Check ownership
        if (!account.getUserId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }
        account.setName(accountDetails.getName());
        account.setType(accountDetails.getType());
        account.setBalance(accountDetails.getBalance());
        account.setIcon(accountDetails.getIcon());
        return accountMapper.save(account);
    }
    
    /**
     * 删除账户（校验归属）。
     */
    public void deleteAccount(Long id) {
        Account account = accountMapper.findById(id).orElseThrow();
        if (!account.getUserId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }
        accountMapper.delete(account);
    }
}
