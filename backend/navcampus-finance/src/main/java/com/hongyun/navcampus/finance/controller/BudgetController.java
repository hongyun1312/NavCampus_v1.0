package com.hongyun.navcampus.finance.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.finance.converter.VoConverter;
import com.hongyun.navcampus.finance.entity.Budget;
import com.hongyun.navcampus.finance.service.BudgetService;
import com.hongyun.navcampus.finance.vo.BudgetVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "预算管理", description = "预算创建与按周期查询")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @PostMapping
    @Operation(summary = "创建预算")
    public R<BudgetVO> create(@RequestBody Budget b) {
        return R.ok(VoConverter.toBudgetVO(budgetService.create(b)));
    }

    @GetMapping
    @Operation(summary = "查询某周期的预算列表")
    public R<List<BudgetVO>> list(@RequestParam String period) {
        return R.ok(VoConverter.toBudgetVOList(budgetService.list(period)));
    }
}
