package com.hongyun.navcampus.finance.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.finance.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器。
 * 提供月度统计聚合接口，用于前端可视化。
 */
@RestController
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private StatsService statsService;

    /**
     * 聚合统计。
     */
    @GetMapping("/aggregate")
    public R<Object> aggregate(@RequestParam String period) {
        return R.ok(statsService.aggregate(period));
    }
}