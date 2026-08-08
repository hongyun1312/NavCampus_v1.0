package com.hongyun.navcampus.campus.controller;

import com.hongyun.navcampus.common.core.R;
import com.hongyun.navcampus.campus.service.NavigationService;
import com.hongyun.navcampus.campus.vo.PathResultVO;
import com.hongyun.navcampus.campus.vo.RoadNetworkVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.Map;

/**
 * 导航控制器。
 * 提供路网数据查询、A*最短路径规划、最近节点查找等接口。
 */
@RestController
@RequestMapping("/api/navigation")
@Tag(name = "校园导航", description = "路网数据查询与A*路径规划")
public class NavigationController {

    @Autowired
    private NavigationService navigationService;

    @GetMapping("/network")
    @Operation(summary = "获取完整路网数据", description = "返回所有路网节点与边，用于前端可视化")
    public R<RoadNetworkVO> getRoadNetwork() {
        return R.ok(navigationService.getRoadNetwork());
    }

    @GetMapping("/path")
    @Operation(summary = "A*最短路径规划",
            description = "基于A*算法计算两点间最短路径，返回路径节点、总距离、预计步行时间、转向导航指令")
    public R<PathResultVO> findPath(
            @RequestParam Long startNodeId,
            @RequestParam Long endNodeId) {
        return R.ok(navigationService.findShortestPath(startNodeId, endNodeId));
    }

    @GetMapping("/nearest-node")
    @Operation(summary = "查找最近节点", description = "根据坐标查找距离最近的路径节点，用于从任意位置发起导航")
    public R<Map<String, Object>> findNearestNode(
            @RequestParam double x,
            @RequestParam double z) {
        Long nodeId = navigationService.findNearestNode(x, z);
        return R.ok(Map.of("nodeId", nodeId));
    }

    @PostMapping("/cache/refresh")
    @Operation(summary = "刷新路网缓存", description = "手动刷新内存中的路网数据缓存，数据变更后调用")
    public R<String> refreshCache() {
        navigationService.refreshCache();
        return R.ok("路网缓存已刷新", null);
    }
}
