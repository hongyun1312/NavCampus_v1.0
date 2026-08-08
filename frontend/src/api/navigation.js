/**
 * 导航 API 模块。
 * 提供路网数据查询、A*路径规划、最近节点查找等接口。
 */
import http from './http'

/**
 * 获取完整路网数据（节点+边）。
 * @returns {Promise} 路网数据 { nodes: [], edges: [] }
 */
export function getRoadNetwork() {
  return http.get('/api/navigation/network')
}

/**
 * A* 最短路径规划。
 * @param {number} startNodeId 起点节点ID
 * @param {number} endNodeId 终点节点ID
 * @returns {Promise} 路径结果 { found, path, totalDistance, estimatedTime, nodeCount, steps }
 */
export function findShortestPath(startNodeId, endNodeId) {
  return http.get('/api/navigation/path', {
    params: { startNodeId, endNodeId }
  })
}

/**
 * 查找距离指定坐标最近的路径节点。
 * @param {number} x x 坐标
 * @param {number} z z 坐标
 * @returns {Promise} { nodeId: number }
 */
export function findNearestNode(x, z) {
  return http.get('/api/navigation/nearest-node', {
    params: { x, z }
  })
}

/**
 * 刷新路网缓存（数据变更后调用）。
 * @returns {Promise}
 */
export function refreshCache() {
  return http.post('/api/navigation/cache/refresh')
}
