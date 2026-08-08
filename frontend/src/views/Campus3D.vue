<template>
  <div class="campus-container" @touchmove.prevent>
    <!-- 3D 场景容器 -->
    <div ref="canvasRoot" class="canvas-wrapper"></div>

    <!-- 建筑信息面板 (点击建筑后显示) -->
    <!-- 使用 transition 添加淡入淡出动画 -->
    <transition name="el-fade-in-linear">
      <div v-if="selectedBuilding" class="building-panel" :style="panelStyle">
        <!-- 面板头部：标题和关闭按钮 -->
        <div class="panel-header">
          <h3>{{ selectedBuilding.name }}</h3>
          <el-button link @click="closePanel">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        
        <!-- 面板内容区域 -->
        <div class="panel-content">
          <!-- 标签展示：类型 -->
          <div class="tags">
            <el-tag size="small" effect="dark">{{ selectedBuilding.typeLabel }}</el-tag>
          </div>

          <!-- 餐饮/购物服务：显示快速记账功能 -->
          <div v-if="['canteen', 'shop'].includes(selectedBuilding.type)" class="service-section">
            <h4><el-icon><Money /></el-icon> 快速记账</h4>
            <div class="quick-record-form">
              <el-input-number v-model="quickAmount" :min="0" :step="1" size="small" placeholder="金额" />
              <el-button type="primary" size="small" @click="handleQuickRecord">记一笔</el-button>
            </div>
            <p class="hint">默认分类: {{ selectedBuilding.defaultCategory }}</p>
          </div>

          <!-- 教学服务：显示今日课程 -->
          <div v-if="selectedBuilding.type === 'teaching'" class="service-section">
            <h4><el-icon><Reading /></el-icon> 今日课程</h4>
            <ul class="info-list" v-if="buildingCourses.length > 0">
              <li v-for="(c, i) in buildingCourses" :key="i">
                <span class="time">{{ c.time }}</span>
                <span class="name">{{ c.name }}</span>
                <span class="room">{{ c.room }}</span>
              </li>
            </ul>
            <p v-else class="hint">该教学楼今日暂无课程</p>
          </div>

          <!-- 图书馆服务：显示拥挤度 -->
          <div v-if="selectedBuilding.type === 'study'" class="service-section">
            <h4><el-icon><Timer /></el-icon> 自习室状态</h4>
            <el-progress :percentage="studyStats.percentage || 0" :format="formatOccupancy" :status="getOccupancyStatus(studyStats.percentage || 0)" />
            <p class="hint">{{ getOccupancyHint(studyStats.percentage || 0) }}</p>
            <el-button type="primary" size="small" style="width: 100%; margin-top: 8px" @click="$router.push('/study-room')">
              预约座位
            </el-button>
          </div>

           <!-- 通用操作按钮 -->
          <div class="panel-actions">
             <el-button size="small" @click="showDetails">
               详情
             </el-button>
          </div>
        </div>
      </div>
    </transition>

    <!-- 导航路径测试面板 -->
    <div class="nav-panel" v-if="showNavPanel">
      <div class="nav-panel-header">
        <h3>🧭 路径规划测试</h3>
        <el-button link @click="showNavPanel = false"><el-icon><Close /></el-icon></el-button>
      </div>
      <div class="nav-panel-body">
        <el-button size="small" :type="showRoadNetwork ? 'primary' : 'default'" @click="toggleRoadNetwork" style="width:100%;margin-bottom:8px">
          {{ showRoadNetwork ? '隐藏路网' : '显示路网' }}
        </el-button>
        <el-select v-model="navStartNode" placeholder="选择起点" size="small" filterable style="width:100%;margin-bottom:8px">
          <el-option v-for="n in landmarkNodes" :key="n.nodeId" :label="n.nodeName + ' (#' + n.nodeId + ')'" :value="n.nodeId" />
        </el-select>
        <el-select v-model="navEndNode" placeholder="选择终点" size="small" filterable style="width:100%;margin-bottom:8px">
          <el-option v-for="n in landmarkNodes" :key="n.nodeId" :label="n.nodeName + ' (#' + n.nodeId + ')'" :value="n.nodeId" />
        </el-select>
        <el-button type="primary" size="small" @click="findPath" :loading="navLoading" style="width:100%;margin-bottom:4px">
          规划路径 (A*)
        </el-button>
        <el-button size="small" @click="clearPath" style="width:100%">
          清除路径
        </el-button>
        <!-- 路径规划结果展示 -->
        <div v-if="pathResult" class="path-result">
          <template v-if="pathResult.found">
            <!-- 概要信息 -->
            <div class="path-summary">
              <span class="path-badge">✅ 规划成功</span>
              <div class="path-stats">
                <div class="stat-item">
                  <span class="stat-label">总距离</span>
                  <span class="stat-value">{{ Number(pathResult.totalDistance).toFixed(1) }} m</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">预计时间</span>
                  <span class="stat-value">{{ formatDuration(pathResult.estimatedTime) }}</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">途经节点</span>
                  <span class="stat-value">{{ pathResult.nodeCount }} 个</span>
                </div>
              </div>
            </div>
            <!-- 转向导航指令列表 -->
            <div class="nav-steps" v-if="pathResult.steps && pathResult.steps.length > 0">
              <div class="steps-title">📋 导航指令</div>
              <div v-for="(step, idx) in pathResult.steps" :key="idx" class="step-item">
                <span class="step-icon">{{ getStepIcon(step.direction) }}</span>
                <span class="step-text">{{ step.instruction }}</span>
              </div>
            </div>
          </template>
          <p v-else class="path-fail">❌ 未找到可行路径</p>
        </div>
      </div>
    </div>

    <!-- 导航按钮 (悬浮) -->
    <el-button v-if="!showNavPanel" class="nav-toggle-btn" circle @click="toggleNavPanel">
      <el-icon><Position /></el-icon>
    </el-button>

    <!-- 快捷记账弹窗 (全局) -->
    <el-dialog v-model="showQuickRecordPopup" title="一键记账" width="300px">
      <el-form :model="quickRecordForm">
        <el-form-item label="金额">
          <el-input-number v-model="quickRecordForm.amount" :min="0" />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="quickRecordForm.location" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="quickRecordForm.remark" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showQuickRecordPopup = false">取消</el-button>
        <el-button type="primary" @click="handleGlobalQuickRecord">确定</el-button>
      </template>
    </el-dialog>

    <!-- 课程表弹窗 -->
    <el-dialog v-model="showSchedulePopup" title="今日全校课程" width="600px">
      <el-table :data="todayCourses" height="400">
        <el-table-column property="time" label="时间" width="80" />
        <el-table-column property="name" label="课程" width="120" />
        <el-table-column property="room" label="地点" width="150" />
        <el-table-column property="teacher" label="教师" />
      </el-table>
    </el-dialog>

    <!-- 环境控制面板 -->
    <div class="env-control-panel">
      <div class="panel-header">环境控制</div>
      
      <div class="control-item">
        <span class="label">时间模式</span>
        <el-switch v-model="envControl.isRealTime" active-text="实时" inactive-text="手动" size="small" />
      </div>

      <div class="control-item" v-if="!envControl.isRealTime">
        <span class="label">时间调节</span>
        <el-slider v-model="manualHour" :min="0" :max="24" :step="0.1" :format-tooltip="formatTimeTooltip" @input="handleTimeChange" />
      </div>
      
      <div class="control-item">
         <span class="label">当前时间</span>
         <span class="value">{{ formatTime(envControl.time) }}</span>
      </div>

      <div class="control-item">
        <span class="label">天气状况</span>
        <el-select v-model="envControl.manualWeather" :disabled="envControl.isRealTime && campusState.showWeather" size="small" style="width: 100px" @change="updateEnvironment">
          <el-option label="晴朗" value="clear"><span style="display:flex;align-items:center"><el-icon><Sunny /></el-icon> 晴朗</span></el-option>
          <el-option label="多云" value="cloudy"><span style="display:flex;align-items:center"><el-icon><PartlyCloudy /></el-icon> 多云</span></el-option>
          <el-option label="阴天" value="overcast"><span style="display:flex;align-items:center"><el-icon><PartlyCloudy /></el-icon> 阴天</span></el-option>
          <el-option label="降雨" value="rain"><span style="display:flex;align-items:center"><el-icon><Lightning /></el-icon> 降雨</span></el-option>
          <el-option label="降雪" value="snow"><span style="display:flex;align-items:center"><el-icon><Moon /></el-icon> 降雪</span></el-option>
        </el-select>
      </div>
      <div class="hint" v-if="envControl.isRealTime && campusState.showWeather">
        已连接沈阳实时气象
      </div>
    </div>

    <!-- 加载遮罩层 -->
    <div v-if="loading" class="loading-overlay">
      <el-icon class="is-loading"><Loading /></el-icon>
      <p>正在加载虚拟校园...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick, watch, computed, reactive } from 'vue'
import { ElMessage, ElDialog, ElTable, ElTableColumn } from 'element-plus'
import { Close, Money, Reading, Timer, Position, Loading, Sunny, Moon, PartlyCloudy, Lightning } from '@element-plus/icons-vue'
import { campusState } from '../store/campus'
import axios from 'axios'
import http from '../api/http'
import { CampusScene3D, buildingData } from '../utils/CampusScene3D.js'
import { getRoadNetwork, findShortestPath } from '../api/navigation'

// --- 状态变量定义 ---
const canvasRoot = ref(null)      // 3D 画布挂载点引用
const loading = ref(true)         // 加载状态
const selectedBuilding = ref(null)// 当前选中的建筑对象
const panelPosition = ref({ x: 0, y: 0 }) // 信息面板的屏幕坐标
const quickAmount = ref(15)       // 快捷记账默认金额
const todayCourses = ref([])      // 今日课程数据
const defaultAccountId = ref(null)// 默认账户ID
const studyStats = ref({}) // 自习室状态
const studyStatsLoading = ref(false)
let studyStatsInterval = null // 自习室状态轮询定时器
let scene3D = null // 3D 场景实例

// --- 导航路径测试状态 ---
const showNavPanel = ref(false)
const navStartNode = ref(null)
const navEndNode = ref(null)
const landmarkNodes = ref([])
const allRoadNodes = ref([])
const allRoadEdges = ref([])
const navLoading = ref(false)
const pathResult = ref(null)
const showRoadNetwork = ref(false)


// --- 环境控制状态 ---
const envControl = reactive({
  time: new Date(),         // 模拟时间
  isRealTime: true,         // 是否同步真实时间
  manualWeather: 'clear'    // 天气模式
})
const manualHour = ref(new Date().getHours() + new Date().getMinutes()/60)
let timeInterval = null     // 时间同步定时器

// 格式化时间显示
const formatTime = (date) => {
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}
const formatTimeTooltip = (val) => {
  const h = Math.floor(val)
  const m = Math.floor((val - h) * 60)
  return `${h.toString().padStart(2,'0')}:${m.toString().padStart(2,'0')}`
}

const getOccupancyStatus = (p) => {
  if (p >= 90) return 'exception'
  if (p >= 60) return 'warning'
  return 'success'
}

const getOccupancyHint = (p) => {
  if (p >= 90) return '人员爆满，建议稍后'
  if (p >= 60) return '座位紧张，请尽快预约'
  return '座位充足，欢迎学习'
}

// 获取自习室状态
const fetchStudyStats = async () => {
  studyStatsLoading.value = true
  try {
    const res = await http.get('/api/study-room/stats')
    studyStats.value = res.data
  } catch (e) {
    console.error('Fetch study stats failed', e)
  } finally {
    studyStatsLoading.value = false
  }
}

// 监听选中的建筑
watch(selectedBuilding, (val) => {
  // 清除旧定时器
  if (studyStatsInterval) {
    clearInterval(studyStatsInterval)
    studyStatsInterval = null
  }
  
  if (val && val.type === 'study') {
    fetchStudyStats()
    // 开启轮询 (每30秒刷新)
    studyStatsInterval = setInterval(fetchStudyStats, 30000)
  }
})

// 监听今日课程数据变化，如果图层已开启则刷新箭头
watch(todayCourses, () => {
  if (campusState.showCourses) {
    if (scene3D) scene3D.toggleCourses(true, todayCourses.value)
  }
})

// 处理滑块时间变更
const handleTimeChange = (val) => {
  const date = new Date()
  date.setHours(Math.floor(val))
  date.setMinutes((val % 1) * 60)
  envControl.time = date
  updateEnvironment()
}

// 更新环境
const updateEnvironment = () => {
    if (scene3D) {
        scene3D.updateEnvironment(envControl.time, envControl.manualWeather)
    }
}

// 获取实时天气 (沈阳)
const fetchWeather = async () => {
  try {
    // 优先使用后端代理，如果失败尝试直接请求 Open-Meteo
    let data
    try {
       const res = await http.get('/api/weather?city=shenyang')
       data = res.data
    } catch {
       const res = await axios.get('https://api.open-meteo.com/v1/forecast?latitude=41.80&longitude=123.43&current_weather=true')
       data = res.data
    }
    
    if (data && data.current_weather) {
      const code = data.current_weather.weathercode
      // WMO Weather interpretation codes
      let mode = 'clear'
      if (code <= 1) mode = 'clear'
      else if (code <= 3) mode = 'cloudy'
      else if (code <= 48) mode = 'overcast' // fog
      else if (code <= 67 || (code >= 80 && code <= 82)) mode = 'rain'
      else if (code >= 71) mode = 'snow'
      
      envControl.manualWeather = mode
      ElMessage.success(`已同步沈阳天气: ${mode === 'clear' ? '晴' : mode === 'rain' ? '雨' : mode === 'snow' ? '雪' : '多云'}`)
      updateEnvironment()
    }
  } catch (e) {
    console.error('Weather fetch failed', e)
  }
}

// 实时时间循环
const startTimeLoop = () => {
    timeInterval = setInterval(() => {
        if (envControl.isRealTime) {
            envControl.time = new Date()
            manualHour.value = envControl.time.getHours() + envControl.time.getMinutes() / 60
            updateEnvironment()
            
            // 每 10 分钟更新一次天气
            if (envControl.time.getMinutes() % 10 === 0 && campusState.showWeather) {
               fetchWeather()
            }
        }
    }, 60000) // 每分钟更新
}

// --- 弹窗控制状态 ---
const showQuickRecordPopup = ref(false)
const showSchedulePopup = ref(false)
const quickRecordForm = reactive({
  amount: 0,
  remark: '',
  location: '未知地点'
})

// --- 计算属性 ---

// 动态计算面板的样式（位置）
const panelStyle = computed(() => ({
  left: panelPosition.value.x + 'px',
  top: panelPosition.value.y + 'px'
}))

// 过滤出当前选中教学楼的今日课程
const buildingCourses = computed(() => {
  if (!selectedBuilding.value || !todayCourses.value) return []
  return todayCourses.value.filter(c => c.room && c.room.includes(selectedBuilding.value.name))
})

// --- 业务方法 ---

const formatOccupancy = (percentage) => `拥挤度 ${percentage}%`

const closePanel = () => {
  selectedBuilding.value = null
  if (scene3D) scene3D.selectedBuilding = null // Sync back to scene
}

// 处理特定地点的快速记账
const handleQuickRecord = async () => {
  if (!selectedBuilding.value) return
  if (!defaultAccountId.value) {
    ElMessage.warning('正在获取账户信息，请稍后...')
    return
  }
  
  try {
    const now = new Date()
    const timeStr = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 19)
    
    const record = {
      amount: quickAmount.value,
      type: 'EXPENSE',
      category: null, 
      categoryId: null,
      remark: `在${selectedBuilding.value.name}的快捷消费`,
      location: selectedBuilding.value.name,
      time: timeStr,
      accountId: defaultAccountId.value
    }
    
    const response = await http.post('/api/records', record)

    if (response.status === 200 || response.status === 201) {
      ElMessage.success('记账成功！')
      quickAmount.value = 15 // 重置金额
      closePanel()
    } else {
      ElMessage.error('记账失败')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('记账失败：' + (e.response?.data?.message || e.message))
  }
}

// 处理全局一键记账（从 store 触发）
const handleGlobalQuickRecord = async () => {
    if (!defaultAccountId.value) {
        ElMessage.warning('未找到默认账户')
        return
    }
    try {
        const now = new Date()
        const timeStr = new Date(now.getTime() - now.getTimezoneOffset() * 60000).toISOString().slice(0, 19)
        const record = {
            amount: quickRecordForm.amount,
            type: 'EXPENSE',
            accountId: defaultAccountId.value,
            time: timeStr,
            location: quickRecordForm.location,
            remark: quickRecordForm.remark
        }
        await http.post('/api/records', record)
        ElMessage.success('记账成功')
        showQuickRecordPopup.value = false
    } catch(e) {
        ElMessage.error('记账失败')
    }
}

const showDetails = () => {
  ElMessage.info('查看详情功能开发中...')
}


// --- 导航路径测试方法 ---

/**
 * 切换导航测试面板的显示/隐藏状态。
 * 首次打开面板时，自动从后端加载路网数据（节点+边），
 * 用于填充起点/终点下拉选择器。
 */
const toggleNavPanel = async () => {
  showNavPanel.value = !showNavPanel.value
  // 首次打开时懒加载路网数据，避免页面初始化时的无效请求
  if (showNavPanel.value && allRoadNodes.value.length === 0) {
    await loadRoadNetworkForDropdown()
  }
}

/**
 * 从后端获取完整路网数据并填充到下拉选择器。
 * 调用 GET /api/navigation/network 接口，返回 { nodes, edges } 结构。
 * - nodes：全部路网节点（含地标节点和路径节点）
 * - edges：路网边（节点间的连接关系与距离）
 * 将地标节点（isLandmark=true）单独筛选出来，供起点/终点下拉框使用。
 */
const loadRoadNetworkForDropdown = async () => {
  try {
    const res = await getRoadNetwork()
    const data = res.data
    if (data && data.nodes) {
      allRoadNodes.value = data.nodes       // 存储全部节点，用于 3D 可视化渲染
      allRoadEdges.value = data.edges || [] // 存储全部边，用于 3D 可视化渲染
      // 使用路径节点作为起终点选项（地标节点已从路网中移除，无连接边）
      landmarkNodes.value = data.nodes.filter(n => !n.isLandmark)
    }
  } catch (e) {
    console.error('加载路网数据失败:', e)
    ElMessage.error('加载路网数据失败')
  }
}

/**
 * 切换 3D 场景中路网可视化的显示/隐藏。
 * - 首次显示时：从后端加载数据并调用 scene3D.renderRoadNetwork() 渲染
 * - 后续切换：仅切换 Three.js Group 的 visible 属性，避免重复渲染
 * - 隐藏时：调用 scene3D.toggleRoadNetwork(false) 设置不可见
 */
const toggleRoadNetwork = async () => {
  showRoadNetwork.value = !showRoadNetwork.value
  // 首次开启时懒加载路网数据
  if (showRoadNetwork.value && allRoadNodes.value.length === 0) {
    await loadRoadNetworkForDropdown()
  }
  if (showRoadNetwork.value) {
    // 已有路网数据时进行渲染或显示
    if (allRoadNodes.value.length > 0 && scene3D) {
      if (scene3D.roadNetworkGroup) {
        // 路网已渲染过，直接切换为可见（避免重复创建几何体）
        scene3D.toggleRoadNetwork(true)
      } else {
        // 首次渲染：将节点和边数据传入 Three.js 场景
        scene3D.renderRoadNetwork({ nodes: allRoadNodes.value, edges: allRoadEdges.value })
      }
    }
  } else {
    // 隐藏路网：仅设置 visible=false，不销毁几何体
    if (scene3D) scene3D.toggleRoadNetwork(false)
  }
}

/**
 * 调用后端 A* 算法进行最短路径规划，并在 3D 场景中高亮显示结果路径。
 * 调用 GET /api/navigation/path?startNodeId=&endNodeId= 接口。
 * 成功后调用 scene3D.highlightPath() 在 3D 场景中绘制绿色路径管线。
 */
const findPath = async () => {
  // 前置校验：起点和终点不能为空
  if (!navStartNode.value || !navEndNode.value) {
    ElMessage.warning('请选择起点和终点')
    return
  }
  // 前置校验：起点和终点不能相同
  if (navStartNode.value === navEndNode.value) {
    ElMessage.warning('起点和终点不能相同')
    return
  }
  navLoading.value = true
  try {
    const res = await findShortestPath(navStartNode.value, navEndNode.value)
    const result = res.data
    if (result && result.found && result.path && result.path.length > 0) {
      pathResult.value = result
      // 在 3D 场景中高亮显示路径（绿色管线 + 起终点标记球）
      if (scene3D) scene3D.highlightPath(result.path)
      const mins = Math.floor(result.estimatedTime / 60)
      const secs = result.estimatedTime % 60
      const timeStr = mins > 0 ? `${mins}分${secs}秒` : `${secs}秒`
      ElMessage.success(`路径规划成功！距离${Number(result.totalDistance).toFixed(0)}米，预计${timeStr}`)
    } else {
      ElMessage.warning('未找到可行路径')
    }
  } catch (e) {
    console.error('路径规划失败:', e)
    ElMessage.error('路径规划失败: ' + (e.message || '未知错误'))
  } finally {
    navLoading.value = false
  }
}

/**
 * 清除 3D 场景中的路径高亮显示。
 * 重置路径结果状态并调用 scene3D.clearPathHighlight() 移除路径几何体。
 */
const clearPath = () => {
  pathResult.value = null
  if (scene3D) scene3D.clearPathHighlight()
}

/**
 * 格式化预计步行时间（秒转"X分Y秒"格式）。
 * @param {number} seconds 预计秒数
 * @returns {string} 格式化后的时间文本
 */
const formatDuration = (seconds) => {
  if (!seconds) return '0秒'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return mins > 0 ? `${mins}分${secs}秒` : `${secs}秒`
}

/**
 * 根据转向方向返回对应的 emoji 图标。
 * @param {string} direction 方向枚举值
 * @returns {string} emoji 图标
 */
const getStepIcon = (direction) => {
  const icons = {
    STRAIGHT: '⬆️',
    SLIGHT_LEFT: '↖️',
    LEFT: '⬅️',
    SHARP_LEFT: '⬅️',
    SLIGHT_RIGHT: '↗️',
    RIGHT: '➡️',
    SHARP_RIGHT: '➡️',
    UTURN: '🔄',
    ARRIVE: '📍'
  }
  return icons[direction] || '➡️'
}

// --- 监听 Store 状态 (与其他组件交互) ---

watch(() => campusState.showCourses, (val) => {
  // 切换教学楼高亮显示
  if (scene3D) scene3D.toggleCourses(val, todayCourses.value)
  if (val && (!todayCourses.value || todayCourses.value.length === 0)) {
      ElMessage.info('今日教学楼暂无课程')
  }
})

watch(() => campusState.showSpending, (val) => {
  if (scene3D) scene3D.toggleSpending(val)
})

watch(() => campusState.showWeather, (val) => {
  if (val) {
     if (envControl.isRealTime) {
         fetchWeather()
     } else {
         updateEnvironment()
     }
  } else {
     envControl.manualWeather = 'clear'
     updateEnvironment()
  }
})

watch(() => campusState.actionTrigger, (action) => {
  if (!action) return
  if (action.type === 'quickRecord') {
      showQuickRecordPopup.value = true
  } else if (action.type === 'viewSchedule') {
      showSchedulePopup.value = true
  }
  campusState.actionTrigger = null
})

watch(() => campusState.showLabels, (val) => {
    if (scene3D) scene3D.toggleLabels(val)
})

onMounted(async () => {
  // 等待 DOM 完全渲染后再初始化 3D 场景，
  // 确保 canvasRoot 容器已具有正确的宽高（非 0）
  await nextTick()

  // 校验容器尺寸：如果宽高为 0（DOM 尚未布局完成），则延迟重试
  if (canvasRoot.value && (canvasRoot.value.clientWidth === 0 || canvasRoot.value.clientHeight === 0)) {
    console.warn('[Campus3D] 容器尺寸为 0，等待 100ms 后重试...')
    await new Promise(resolve => setTimeout(resolve, 100))
  }

  // 初始化 3D 场景
  scene3D = new CampusScene3D(canvasRoot.value, {
      onLoad: () => {
          loading.value = false
      },
      onError: (msg) => {
          loading.value = false
          ElMessage.error(msg)
      },
      onBuildingSelected: (building) => {
          selectedBuilding.value = building
      },
      onPanelUpdate: (pos) => {
          panelPosition.value = pos
      }
  })
  
  await scene3D.init()
  
  // 初始化环境
  updateEnvironment()
  fetchWeather()
  startTimeLoop()
  
  // 获取今日课程数据
  try {
      const res = await http.get('/api/timetable/today')
      todayCourses.value = res.data || []
  } catch(e) {
      console.error('Failed to fetch courses', e)
  }
  // 获取默认账户ID (用于记账)
  try {
      const res = await http.get('/api/accounts')
      if (res.data && res.data.length > 0) {
          defaultAccountId.value = res.data[0].id
      }
  } catch(e) {
      console.error('Failed to fetch accounts', e)
  }
})

/**
 * 组件销毁时调用。
 * 清理定时器并释放 3D 场景的全部资源（WebGL 上下文、几何体、材质）。
 */
onBeforeUnmount(() => {
  if (timeInterval) clearInterval(timeInterval)
  if (studyStatsInterval) clearInterval(studyStatsInterval)
  if (scene3D) {
    scene3D.dispose()
    scene3D = null
  }
})

</script>

<style scoped>
.campus-container {
  width: 100%;
  height: 100%; /* 适应父容器高度 */
  position: relative;
  overflow: hidden; /* 禁止滚动 */
  touch-action: none; /* 禁止触摸默认行为 */
}

.canvas-wrapper {
  width: 100%;
  height: 100%;
  display: block;
}

.loading-overlay {
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

/* 建筑信息面板样式 */
.building-panel {
  position: absolute;
  width: 280px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.1);
  backdrop-filter: blur(10px);
  padding: 16px;
  z-index: 100; /* 提高层级，防止被标签遮挡 */
  pointer-events: auto;
  transition: opacity 0.2s; /* 平滑过渡 */
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  border-bottom: 1px solid #eee;
  padding-bottom: 8px;
}

.panel-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #1e293b;
}

.tags {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.service-section {
  margin-bottom: 16px;
}

.service-section h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 6px;
}

.quick-record-form {
  display: flex;
  gap: 8px;
}

.hint {
  font-size: 12px;
  color: #999;
  margin: 4px 0 0 0;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list li {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
  padding: 4px 0;
  border-bottom: 1px dashed #eee;
}

/* 环境控制面板 */
.env-control-panel {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 220px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(12px);
  padding: 14px;
  border-radius: 14px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  border: 1px solid rgba(255, 255, 255, 0.8);
  z-index: 90;
}

.env-control-panel .panel-header {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #475569;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f1f5f9;
}

.env-control-panel .control-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 13px;
}

.env-control-panel .value {
  font-family: monospace;
  font-weight: bold;
}

.panel-actions {
  display: flex;
  gap: 8px;
  margin-top: 16px;
}

/* 场景标签样式 (通过 CSS2DRenderer 渲染) */
:deep(.scene-label) {
  color: #333;
  font-size: 12px;
  font-weight: bold;
  text-shadow: 0 0 4px white;
  background: rgba(255,255,255,0.6);
  padding: 2px 6px;
  border-radius: 4px;
  pointer-events: none; /* 让点击穿透到 canvas，不阻挡模型点击 */
  user-select: none;
  /* z-index 由 labelRenderer 容器控制，通常较低 */
}

.nav-panel {
  position: absolute;
  top: 16px;
  left: 16px;
  width: 280px;
  background: rgba(255, 255, 255, 0.96);
  border-radius: 14px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  z-index: 100;
  overflow: hidden;
}
.nav-panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
}
.nav-panel-header h3 { margin: 0; font-size: 14px; }
.nav-panel-body { padding: 12px 14px; }
.path-result {
  margin-top: 10px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e0f2fe;
}

/* 路径概要 */
.path-summary {
  padding: 10px 12px;
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
}
.path-badge {
  display: inline-block;
  font-size: 12px;
  font-weight: 600;
  color: #0284c7;
  margin-bottom: 6px;
}
.path-stats {
  display: flex;
  justify-content: space-between;
  gap: 4px;
}
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}
.stat-item .stat-label {
  font-size: 10px;
  color: #64748b;
}
.stat-item .stat-value {
  font-size: 13px;
  font-weight: 700;
  color: #0f172a;
}

/* 导航指令列表 */
.nav-steps {
  padding: 8px 12px;
  background: #f8fafc;
  max-height: 200px;
  overflow-y: auto;
}
.steps-title {
  font-size: 11px;
  font-weight: 600;
  color: #64748b;
  margin-bottom: 6px;
}
.step-item {
  display: flex;
  align-items: flex-start;
  gap: 6px;
  padding: 4px 0;
  font-size: 12px;
  color: #334155;
  border-bottom: 1px solid #f1f5f9;
}
.step-item:last-child {
  border-bottom: none;
}
.step-icon {
  flex-shrink: 0;
  font-size: 14px;
  line-height: 18px;
}
.step-text {
  line-height: 18px;
}

.path-fail {
  padding: 10px 12px;
  font-size: 12px;
  color: #dc2626;
  background: #fef2f2;
}
.nav-toggle-btn {
  position: absolute;
  top: 16px;
  left: 16px;
  z-index: 99;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style>
