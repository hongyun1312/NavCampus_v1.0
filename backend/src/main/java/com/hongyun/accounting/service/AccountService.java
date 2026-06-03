package com.hongyun.accounting.service;

import com.hongyun.accounting.entity.Account;
import com.hongyun.accounting.entity.User;
import com.hongyun.accounting.repository.AccountRepository;
import com.hongyun.accounting.repository.UserRepository;
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
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 获取当前登录用户。
     */
    private User getCurrentUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    }

    /**
     * 查询当前用户的全部账户。
     */
    public List<Account> getAllAccounts() {
        return accountRepository.findByUserId(getCurrentUser().getId());
    }

    /**
     * 创建账户。
     */
    public Account createAccount(Account account) {
        account.setUser(getCurrentUser());
        return accountRepository.save(account);
    }
    
    /**
     * 更新账户（校验归属）。
     */
    public Account updateAccount(Long id, Account accountDetails) {
        Account account = accountRepository.findById(id).orElseThrow();
        // Check ownership
        if (!account.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }
        account.setName(accountDetails.getName());
        account.setType(accountDetails.getType());
        account.setBalance(accountDetails.getBalance());
        account.setIcon(accountDetails.getIcon());
        return accountRepository.save(account);
    }
    
    /**
     * 删除账户（校验归属）。
     */
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id).orElseThrow();
        if (!account.getUser().getId().equals(getCurrentUser().getId())) {
            throw new RuntimeException("Unauthorized");
        }
        accountRepository.delete(account);
    }
}
