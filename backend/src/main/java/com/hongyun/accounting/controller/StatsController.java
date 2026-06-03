package com.hongyun.accounting.controller;

import com.hongyun.accounting.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 统计控制器。
 * 提供月度统计聚合接口，用于前端可视化。
 */
@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatsController {
    @Autowired
    private StatsService statsService;

    /**
     * 聚合统计。
     */
    @GetMapping("/aggregate")
    public Object aggregate(@RequestParam String period) {
        return statsService.aggregate(period);
    }
}
