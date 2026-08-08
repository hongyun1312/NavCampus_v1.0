package com.hongyun.navcampus.finance.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.finance.converter.VoConverter;
import com.hongyun.navcampus.finance.entity.Account;
import com.hongyun.navcampus.finance.service.AccountService;
import com.hongyun.navcampus.finance.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@Tag(name = "账户管理", description = "账户CRUD与余额查询")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping
    @Operation(summary = "查询当前用户全部账户")
    public R<List<AccountVO>> list() {
        return R.ok(VoConverter.toAccountVOList(accountService.getAllAccounts()));
    }

    @PostMapping
    @Operation(summary = "创建账户")
    public R<AccountVO> create(@RequestBody Account account) {
        return R.ok(VoConverter.toAccountVO(accountService.createAccount(account)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新账户")
    public R<AccountVO> update(@PathVariable Long id, @RequestBody Account account) {
        return R.ok(VoConverter.toAccountVO(accountService.updateAccount(id, account)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除账户")
    public R<Void> delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return R.ok();
    }
}
