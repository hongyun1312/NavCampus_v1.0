package com.hongyun.accounting.controller;

import com.hongyun.accounting.entity.Budget;
import com.hongyun.accounting.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预算控制器。
 * 提供预算创建与按周期查询接口。
 */
@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    /**
     * 创建预算。
     */
    @PostMapping
    public Budget create(@RequestBody Budget b) {
        return budgetService.create(b);
    }

    /**
     * 查询某周期的预算列表。
     */
    @GetMapping
    public List<Budget> list(@RequestParam String period) {
        return budgetService.list(period);
    }
}
