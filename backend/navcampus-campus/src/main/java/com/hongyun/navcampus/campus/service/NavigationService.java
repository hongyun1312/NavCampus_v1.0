package com.hongyun.navcampus.campus.service;

import com.hongyun.navcampus.campus.entity.CampusRoadEdge;
import com.hongyun.navcampus.campus.entity.CampusRoadNode;
import com.hongyun.navcampus.campus.mapper.CampusRoadEdgeMapper;
import com.hongyun.navcampus.campus.mapper.CampusRoadNodeMapper;
import com.hongyun.navcampus.campus.vo.NavigationStepVO;
import com.hongyun.navcampus.campus.vo.PathResultVO;
import com.hongyun.navcampus.campus.vo.RoadNetworkVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 导航服务。
 * <p>
 * 工程化 A* 路径规划实现，包含以下增强：
 * <ul>
 *   <li>路网数据内存缓存，避免每次请求全量查询数据库</li>
 *   <li>双精度浮点距离计算，无精度丢失</li>
 *   <li>边权重综合距离与通行难度（difficulty 字段）</li>
 *   <li>路径简化：移除共线冗余节点</li>
 *   <li>转向导航指令：自动计算左转/右转/直行等方向</li>
 *   <li>预计步行时间：按 1.4m/s 平均步速估算</li>
 *   <li>最近节点查找：支持从任意坐标出发的导航</li>
 * </ul>
 */
@Slf4j
@Service
public class NavigationService {

    @Autowired
    private CampusRoadNodeMapper roadNodeMapper;
    @Autowired
    private CampusRoadEdgeMapper roadEdgeMapper;

    // ==================== 路网缓存 ====================

    /** 全部节点，按 nodeId 索引 */
    private Map<Long, CampusRoadNode> nodeCache = new HashMap<>();
    /** 邻接表：nodeId -> [(neighborId, edgeWeight), ...] */
    private Map<Long, List<double[]>> adjacencyCache = new HashMap<>();
    /** 缓存是否已初始化 */
    private volatile boolean cacheInitialized = false;

    /**
     * 应用启动后预加载路网数据到内存缓存。
     * 后续路径规划请求直接从缓存读取，避免反复查询数据库。
     */
    @PostConstruct
    public void initCache() {
        refreshCache();
    }

    /**
     * 刷新路网缓存（手动触发或数据变更时调用）。
     */
    public synchronized void refreshCache() {
        log.info("开始加载路网数据到缓存...");

        List<CampusRoadNode> nodes = roadNodeMapper.selectList(
                new LambdaQueryWrapper<CampusRoadNode>().eq(CampusRoadNode::getStatus, 1));
        List<CampusRoadEdge> edges = roadEdgeMapper.selectList(
                new LambdaQueryWrapper<CampusRoadEdge>().eq(CampusRoadEdge::getStatus, 1));

        // 构建节点缓存
        nodeCache = nodes.stream()
                .collect(Collectors.toMap(CampusRoadNode::getNodeId, n -> n));

        // 构建邻接表（带难度权重）
        adjacencyCache = new HashMap<>();
        for (CampusRoadEdge edge : edges) {
            double weight = calculateEdgeWeight(edge);
            // 正向边
            adjacencyCache.computeIfAbsent(edge.getStartNodeId(), k -> new ArrayList<>())
                    .add(new double[]{edge.getEndNodeId(), weight});
            // 如果不是单向边，添加反向边
            if (!Boolean.TRUE.equals(edge.getIsOneWay())) {
                adjacencyCache.computeIfAbsent(edge.getEndNodeId(), k -> new ArrayList<>())
                        .add(new double[]{edge.getStartNodeId(), weight});
            }
        }

        cacheInitialized = true;
        log.info("路网缓存加载完成：{} 个节点，{} 条边", nodes.size(), edges.size());
    }

    /**
     * 计算边权重。
     * 权重 = 距离 × (1 + difficulty × 0.1)
     * difficulty=0 时权重=距离，difficulty=5 时权重=距离×1.5（通行更困难）
     */
    private double calculateEdgeWeight(CampusRoadEdge edge) {
        double distance = edge.getDistance().doubleValue();
        int difficulty = edge.getDifficulty() != null ? edge.getDifficulty() : 0;
        return distance * (1.0 + difficulty * 0.1);
    }

    // ==================== 路网查询 ====================

    /**
     * 获取完整路网数据（节点+边），用于前端可视化。
     */
    public RoadNetworkVO getRoadNetwork() {
        ensureCache();

        RoadNetworkVO vo = new RoadNetworkVO();
        vo.setNodes(nodeCache.values().stream().map(n -> {
            RoadNetworkVO.NodeVO nvo = new RoadNetworkVO.NodeVO();
            nvo.setNodeId(n.getNodeId());
            nvo.setNodeName(n.getNodeName());
            nvo.setX(n.getXCoordinate());
            nvo.setY(n.getYCoordinate());
            nvo.setZ(n.getZCoordinate());
            nvo.setIsLandmark(n.getIsLandmark());
            return nvo;
        }).collect(Collectors.toList()));

        // 边数据仍从数据库查询（量小且需要完整字段）
        List<CampusRoadEdge> edges = roadEdgeMapper.selectList(
                new LambdaQueryWrapper<CampusRoadEdge>().eq(CampusRoadEdge::getStatus, 1));
        vo.setEdges(edges.stream().map(e -> {
            RoadNetworkVO.EdgeVO evo = new RoadNetworkVO.EdgeVO();
            evo.setEdgeId(e.getEdgeId());
            evo.setStartNodeId(e.getStartNodeId());
            evo.setEndNodeId(e.getEndNodeId());
            evo.setDistance(e.getDistance());
            evo.setIsOneWay(e.getIsOneWay());
            evo.setDifficulty(e.getDifficulty());
            return evo;
        }).collect(Collectors.toList()));

        return vo;
    }

    // ==================== A* 路径规划 ====================

    /**
     * A* 最短路径算法。
     * <p>
     * 使用欧几里得距离作为启发函数，基于优先队列实现。
     * 边权重综合距离与通行难度，支持双向边。
     *
     * @param startNodeId 起点节点ID
     * @param endNodeId   终点节点ID
     * @return 路径结果（含路径节点、总距离、预计时间、转向指令）
     */
    public PathResultVO findShortestPath(Long startNodeId, Long endNodeId) {
        ensureCache();

        CampusRoadNode startNode = nodeCache.get(startNodeId);
        CampusRoadNode endNode = nodeCache.get(endNodeId);
        if (startNode == null || endNode == null) {
            return createNotFoundResult("起点或终点节点不存在");
        }
        if (startNodeId.equals(endNodeId)) {
            return createNotFoundResult("起点和终点不能相同");
        }

        // gScore: 从起点到当前节点的实际累计权重
        Map<Long, Double> gScore = new HashMap<>();
        // fScore: gScore + 启发式估计距离
        Map<Long, Double> fScore = new HashMap<>();
        // cameFrom: 记录路径前驱节点
        Map<Long, Long> cameFrom = new HashMap<>();
        // openSet: 待探索节点（按 fScore 升序排列）
        PriorityQueue<Long> openSet = new PriorityQueue<>(
                Comparator.comparingDouble(id -> fScore.getOrDefault(id, Double.MAX_VALUE)));
        // closedSet: 已探索节点
        Set<Long> closedSet = new HashSet<>();

        // 初始化：起点 gScore=0，fScore=启发距离
        gScore.put(startNodeId, 0.0);
        fScore.put(startNodeId, heuristic(startNode, endNode));
        openSet.add(startNodeId);

        while (!openSet.isEmpty()) {
            Long current = openSet.poll();

            // 到达终点，重建路径
            if (current.equals(endNodeId)) {
                return buildResult(cameFrom, current, gScore);
            }

            // 跳过已探索的节点（优先队列中可能存在重复条目）
            if (closedSet.contains(current)) continue;
            closedSet.add(current);

            // 遍历邻居
            List<double[]> neighbors = adjacencyCache.getOrDefault(current, Collections.emptyList());
            for (double[] neighborData : neighbors) {
                long neighborId = (long) neighborData[0];
                double edgeWeight = neighborData[1];

                if (closedSet.contains(neighborId)) continue;

                double tentativeG = gScore.get(current) + edgeWeight;
                if (tentativeG < gScore.getOrDefault(neighborId, Double.MAX_VALUE)) {
                    // 找到更优路径，更新
                    cameFrom.put(neighborId, current);
                    gScore.put(neighborId, tentativeG);
                    CampusRoadNode neighborNode = nodeCache.get(neighborId);
                    fScore.put(neighborId, tentativeG + heuristic(neighborNode, endNode));
                    openSet.add(neighborId);
                }
            }
        }

        // openSet 为空仍未到达终点，路径不存在
        return createNotFoundResult("两点之间无可达路径");
    }

    /**
     * 启发式函数：欧几里得距离（x-z 平面）。
     * 使用双精度浮点数，无精度丢失。
     */
    private double heuristic(CampusRoadNode from, CampusRoadNode to) {
        if (from == null || to == null) return 0;
        double dx = from.getXCoordinate().doubleValue() - to.getXCoordinate().doubleValue();
        double dz = from.getZCoordinate().doubleValue() - to.getZCoordinate().doubleValue();
        return Math.sqrt(dx * dx + dz * dz);
    }

    // ==================== 路径后处理 ====================

    /**
     * 重建路径并构建完整结果。
     * 包含路径简化、转向指令计算、预计时间估算。
     */
    private PathResultVO buildResult(Map<Long, Long> cameFrom, Long endNodeId,
                                     Map<Long, Double> gScore) {
        // 1. 重建路径节点序列
        List<Long> path = new ArrayList<>();
        Long current = endNodeId;
        path.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            path.add(current);
        }
        Collections.reverse(path);

        // 2. 路径简化：移除共线冗余节点（仅保留转向点）
        List<Long> simplifiedPath = simplifyPath(path);

        // 3. 计算总距离（使用实际几何距离，非权重）
        double totalDistance = calculatePathDistance(simplifiedPath);

        // 4. 生成转向导航指令
        List<NavigationStepVO> steps = generateNavigationSteps(simplifiedPath);

        // 5. 估算步行时间（1.4 m/s ≈ 5 km/h，校园平均步速）
        int estimatedTime = (int) Math.ceil(totalDistance / 1.4);

        // 6. 构建结果 VO
        PathResultVO result = new PathResultVO();
        result.setFound(true);
        result.setTotalDistance(BigDecimal.valueOf(totalDistance).setScale(2, RoundingMode.HALF_UP));
        result.setEstimatedTime(estimatedTime);
        result.setNodeCount(simplifiedPath.size());
        result.setSteps(steps);
        result.setPath(simplifiedPath.stream().map(id -> {
            CampusRoadNode node = nodeCache.get(id);
            PathResultVO.PathNodeVO pn = new PathResultVO.PathNodeVO();
            pn.setNodeId(node.getNodeId());
            pn.setNodeName(node.getNodeName());
            pn.setX(node.getXCoordinate());
            pn.setY(node.getYCoordinate());
            pn.setZ(node.getZCoordinate());
            return pn;
        }).collect(Collectors.toList()));

        return result;
    }

    /**
     * 路径简化：移除共线冗余节点。
     * 如果三个连续节点在同一直线上（偏移角<5°），则移除中间节点。
     * 保留所有转向点，使路径更简洁。
     */
    private List<Long> simplifyPath(List<Long> path) {
        if (path.size() <= 2) return new ArrayList<>(path);

        List<Long> simplified = new ArrayList<>();
        simplified.add(path.get(0)); // 保留起点

        for (int i = 1; i < path.size() - 1; i++) {
            CampusRoadNode prev = nodeCache.get(path.get(i - 1));
            CampusRoadNode curr = nodeCache.get(path.get(i));
            CampusRoadNode next = nodeCache.get(path.get(i + 1));

            // 计算向量
            double v1x = curr.getXCoordinate().doubleValue() - prev.getXCoordinate().doubleValue();
            double v1z = curr.getZCoordinate().doubleValue() - prev.getZCoordinate().doubleValue();
            double v2x = next.getXCoordinate().doubleValue() - curr.getXCoordinate().doubleValue();
            double v2z = next.getZCoordinate().doubleValue() - curr.getZCoordinate().doubleValue();

            // 计算偏转角（叉积判断方向，点积判断角度）
            double cross = v1x * v2z - v1z * v2x; // 叉积 z 分量
            double dot = v1x * v2x + v1z * v2z;   // 点积
            double angle = Math.abs(Math.atan2(cross, dot)); // 偏转角度（弧度）

            // 偏转角 > 5° 时保留该节点（是转向点）
            if (angle > Math.toRadians(5)) {
                simplified.add(path.get(i));
            }
        }

        simplified.add(path.get(path.size() - 1)); // 保留终点
        return simplified;
    }

    /**
     * 计算路径的实际几何总距离（x-z 平面欧几里得距离之和）。
     */
    private double calculatePathDistance(List<Long> path) {
        double total = 0;
        for (int i = 1; i < path.size(); i++) {
            CampusRoadNode a = nodeCache.get(path.get(i - 1));
            CampusRoadNode b = nodeCache.get(path.get(i));
            total += heuristic(a, b);
        }
        return total;
    }

    /**
     * 生成转向导航指令列表。
     * 根据相邻路径段的方位角变化，判断转向方向并生成指令文本。
     */
    private List<NavigationStepVO> generateNavigationSteps(List<Long> path) {
        List<NavigationStepVO> steps = new ArrayList<>();
        if (path.size() < 2) return steps;

        // 第一段：出发指令（视为直行）
        double prevBearing = calculateBearing(nodeCache.get(path.get(0)), nodeCache.get(path.get(1)));

        for (int i = 0; i < path.size() - 1; i++) {
            CampusRoadNode from = nodeCache.get(path.get(i));
            CampusRoadNode to = nodeCache.get(path.get(i + 1));

            double segmentDistance = heuristic(from, to);
            String fromName = from.getNodeName() != null ? from.getNodeName() : "节点" + from.getNodeId();
            String toName = to.getNodeName() != null ? to.getNodeName() : "节点" + to.getNodeId();

            NavigationStepVO step = new NavigationStepVO();
            step.setFromNodeId(from.getNodeId());
            step.setFromNodeName(fromName);
            step.setToNodeId(to.getNodeId());
            step.setToNodeName(toName);
            step.setDistance(BigDecimal.valueOf(segmentDistance).setScale(2, RoundingMode.HALF_UP));

            if (i == 0) {
                // 第一段：出发
                step.setDirection("STRAIGHT");
                step.setTurnAngle(0.0);
                step.setInstruction(String.format("从 %s 出发，直行 %.2f 米", fromName, segmentDistance));
            } else {
                // 计算转向角度
                double currBearing = calculateBearing(from, to);
                double turnAngle = normalizeAngle(currBearing - prevBearing);

                step.setTurnAngle(turnAngle);
                String direction = determineDirection(turnAngle);
                step.setDirection(direction);
                step.setInstruction(formatInstruction(direction, segmentDistance, toName));
            }

            steps.add(step);
            prevBearing = calculateBearing(from, to);
        }

        // 最后添加到达指令
        CampusRoadNode lastNode = nodeCache.get(path.get(path.size() - 1));
        NavigationStepVO arrive = new NavigationStepVO();
        arrive.setDirection("ARRIVE");
        arrive.setInstruction("到达目的地：" + (lastNode.getNodeName() != null ? lastNode.getNodeName() : "节点" + lastNode.getNodeId()));
        arrive.setDistance(BigDecimal.ZERO);
        steps.add(arrive);

        return steps;
    }

    /**
     * 计算两个节点间的方位角（0°=北，顺时针）。
     * Three.js 坐标系中 -Z 方向为北。
     */
    private double calculateBearing(CampusRoadNode from, CampusRoadNode to) {
        double dx = to.getXCoordinate().doubleValue() - from.getXCoordinate().doubleValue();
        double dz = to.getZCoordinate().doubleValue() - from.getZCoordinate().doubleValue();
        return Math.toDegrees(Math.atan2(dx, -dz));
    }

    /**
     * 将角度归一化到 [-180, 180] 范围。
     */
    private double normalizeAngle(double angle) {
        while (angle > 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    /**
     * 根据转向角度判断方向枚举。
     * 正值=右转，负值=左转。
     */
    private String determineDirection(double turnAngle) {
        double absAngle = Math.abs(turnAngle);
        if (absAngle < 15) return "STRAIGHT";
        if (absAngle < 45) return turnAngle > 0 ? "SLIGHT_RIGHT" : "SLIGHT_LEFT";
        if (absAngle < 120) return turnAngle > 0 ? "RIGHT" : "LEFT";
        if (absAngle < 165) return turnAngle > 0 ? "SHARP_RIGHT" : "SHARP_LEFT";
        return "UTURN";
    }

    /**
     * 格式化导航指令文本。
     */
    private String formatInstruction(String direction, double distance, String toName) {
        String dirText = switch (direction) {
            case "STRAIGHT" -> "直行";
            case "SLIGHT_LEFT" -> "稍向左转后直行";
            case "LEFT" -> "左转后直行";
            case "SHARP_LEFT" -> "急左转后直行";
            case "SLIGHT_RIGHT" -> "稍向右转后直行";
            case "RIGHT" -> "右转后直行";
            case "SHARP_RIGHT" -> "急右转后直行";
            case "UTURN" -> "掉头后直行";
            default -> "前行";
        };
        return String.format("%s %.2f 米，到达 %s", dirText, distance, toName);
    }

    // ==================== 最近节点查找 ====================

    /**
     * 查找距离指定坐标最近的路径节点。
     * 用于从任意位置发起导航（如从用户当前位置到某建筑物）。
     *
     * @param x x 坐标
     * @param z z 坐标
     * @return 最近节点ID，无节点时返回 null
     */
    public Long findNearestNode(double x, double z) {
        ensureCache();

        Long nearestId = null;
        double nearestDist = Double.MAX_VALUE;

        for (CampusRoadNode node : nodeCache.values()) {
            double dx = node.getXCoordinate().doubleValue() - x;
            double dz = node.getZCoordinate().doubleValue() - z;
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist < nearestDist) {
                nearestDist = dist;
                nearestId = node.getNodeId();
            }
        }

        return nearestId;
    }

    // ==================== 工具方法 ====================

    /** 确保缓存已初始化 */
    private void ensureCache() {
        if (!cacheInitialized) {
            refreshCache();
        }
    }

    /** 创建"未找到路径"结果 */
    private PathResultVO createNotFoundResult(String reason) {
        PathResultVO result = new PathResultVO();
        result.setFound(false);
        result.setPath(Collections.emptyList());
        result.setTotalDistance(BigDecimal.ZERO);
        result.setEstimatedTime(0);
        result.setNodeCount(0);
        result.setSteps(Collections.emptyList());
        return result;
    }
}
