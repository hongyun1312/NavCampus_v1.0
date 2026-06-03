package com.hongyun.accounting.controller;

import com.hongyun.accounting.entity.Account;
import com.hongyun.accounting.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账户控制器。
 * 提供账户的列表、创建、更新与删除接口。
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {
    @Autowired
    private AccountService accountService;

    /**
     * 查询当前用户的账户。
     */
    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    /**
     * 创建账户。
     */
    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.createAccount(account);
    }
    
    /**
     * 更新账户。
     */
    @PutMapping("/{id}")
    public Account updateAccount(@PathVariable Long id, @RequestBody Account account) {
        return accountService.updateAccount(id, account);
    }
    
    /**
     * 删除账户。
     */
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
    }
}
