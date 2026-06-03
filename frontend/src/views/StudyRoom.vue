<template>
  <div class="study-room-container">
    <div class="page-header">
      <h2>📚 自习室预约系统</h2>
      <el-badge :is-dot="notifications.length > 0" class="notification-badge">
        <el-button :icon="Bell" circle @click="showNotifications = true" />
      </el-badge>
    </div>

    <el-tabs v-model="activeTab" class="main-tabs">
      <!-- 预约主界面 -->
      <el-tab-pane label="预约自习" name="reserve">
        <div class="reserve-layout">
          <!-- 侧边栏：筛选与时间 -->
          <div class="filters-panel">
            <el-card shadow="hover">
              <template #header>预约设置</template>
              <el-form label-position="top">
                <el-form-item label="日期选择">
                  <el-date-picker 
                    v-model="searchDate" 
                    type="date" 
                    placeholder="选择日期" 
                    :disabled-date="disabledDate"
                    style="width: 100%"
                  />
                </el-form-item>
                <el-form-item label="时段选择">
                   <el-time-select
                      v-model="startTime"
                      start="08:00"
                      step="01:00"
                      end="22:00"
                      placeholder="开始时间"
                      style="width: 100%; margin-bottom: 8px"
                    />
                    <el-time-select
                      v-model="endTime"
                      start="08:00"
                      step="01:00"
                      end="22:00"
                      min-time="startTime"
                      placeholder="结束时间"
                      style="width: 100%"
                    />
                </el-form-item>
                <el-form-item label="座位偏好">
                  <el-checkbox-group v-model="preferences">
                    <el-checkbox label="POWER">电源插座</el-checkbox>
                    <el-checkbox label="WINDOW">靠窗位置</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
                <el-button type="primary" style="width: 100%" @click="fetchSeats" :loading="loading">
                  查询可用座位
                </el-button>
              </el-form>
            </el-card>

            <el-card shadow="hover" style="margin-top: 16px">
              <div class="legend">
                <div class="legend-item"><span class="dot available"></span> 可预约</div>
                <div class="legend-item"><span class="dot occupied"></span> 已占用</div>
                <div class="legend-item"><span class="dot maintenance"></span> 维护中</div>
                <div class="legend-item"><span class="dot selected"></span> 已选中</div>
              </div>
            </el-card>
          </div>

          <!-- 座位地图 -->
          <div class="seat-map-panel">
            <el-card shadow="never" class="map-card">
              <div class="seat-grid" v-loading="loading">
                <div 
                  v-for="seat in filteredSeats" 
                  :key="seat.id"
                  class="seat-item"
                  :class="[
                    seat.status.toLowerCase(), 
                    { selected: selectedSeat?.id === seat.id },
                    { 'has-power': seat.type === 'POWER' }
                  ]"
                  @click="selectSeat(seat)"
                >
                  <el-icon v-if="seat.type === 'POWER'" class="power-icon"><Lightning /></el-icon>
                  <span>{{ seat.name }}</span>
                </div>
              </div>
              <div class="map-footer" v-if="selectedSeat">
                <span>已选座位：<b>{{ selectedSeat.name }}</b> ({{ getSeatTypeLabel(selectedSeat.type) }})</span>
                <el-button type="success" @click="confirmReservation">确认预约</el-button>
              </div>
            </el-card>
          </div>
        </div>
      </el-tab-pane>

      <!-- 我的预约 -->
      <el-tab-pane label="我的预约" name="my-reservations">
        <el-card shadow="never">
          <el-table :data="myReservations" style="width: 100%" empty-text="暂无预约记录">
            <el-table-column prop="seat.name" label="座位号" width="120" />
            <el-table-column label="日期" width="150">
              <template #default="{ row }">
                {{ formatDate(row.startTime) }}
              </template>
            </el-table-column>
            <el-table-column label="时间段" width="200">
              <template #default="{ row }">
                {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button 
                  v-if="row.status === 'CONFIRMED'" 
                  type="primary" 
                  size="small" 
                  @click="handleCheckIn(row)"
                >
                  签到
                </el-button>
                <el-button 
                  v-if="['PENDING', 'CONFIRMED'].includes(row.status)" 
                  type="danger" 
                  size="small" 
                  @click="handleCancel(row)"
                >
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 评价反馈 -->
      <el-tab-pane label="评价反馈" name="feedback">
        <el-card style="max-width: 600px; margin: 0 auto">
          <el-form :model="feedbackForm" label-width="80px">
            <el-form-item label="评分">
              <el-rate v-model="feedbackForm.rate" />
            </el-form-item>
            <el-form-item label="反馈内容">
              <el-input v-model="feedbackForm.content" type="textarea" rows="4" placeholder="请输入您的使用体验或建议..." />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="submitFeedback">提交反馈</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 消息通知抽屉 -->
    <el-drawer v-model="showNotifications" title="消息通知" direction="rtl" size="300px">
      <div v-if="notifications.length === 0" style="text-align: center; color: #909399; margin-top: 20px;">
        暂无通知
      </div>
      <div v-else class="notification-list">
        <div v-for="note in notifications" :key="note.id" class="notification-item" :class="{ unread: !note.isRead }">
          <div class="note-title">{{ note.title }}</div>
          <div class="note-content">{{ note.content }}</div>
          <div class="note-time">{{ formatDate(note.createdAt) }}</div>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Lightning, Bell } from '@element-plus/icons-vue'
import http from '../api/http'
import { auth } from '../store/auth'

const activeTab = ref('reserve')
const loading = ref(false)
const showNotifications = ref(false)
const notifications = ref([])

const isAdmin = computed(() => {
  const user = auth.getUser()
  return user && user.role === 'ADMIN'
})

// Search
const searchDate = ref(new Date())
const startTime = ref('09:00')
const endTime = ref('12:00')
const preferences = ref([])
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 8.64e7
}

// Data
const allSeats = ref([])
const myReservations = ref([])
const selectedSeat = ref(null)
const occupiedSeatIds = ref([]) // IDs of seats occupied in the selected time

// Feedback
const feedbackForm = reactive({
  rate: 5,
  content: ''
})

// Fetch Seats & Availability
const fetchSeats = async () => {
  loading.value = true
  selectedSeat.value = null
  try {
    const res = await http.get('/api/study-room/seats')
    allSeats.value = res.data || []
    
    // Check availability if time is set
    await checkAvailability()
  } catch (e) {
    ElMessage.error('获取座位数据失败')
  } finally {
    loading.value = false
  }
}

const checkAvailability = async () => {
  if (!searchDate.value || !startTime.value || !endTime.value) return
  
  // Use local date string construction to avoid timezone shifts
  const year = searchDate.value.getFullYear();
  const month = String(searchDate.value.getMonth() + 1).padStart(2, '0');
  const day = String(searchDate.value.getDate()).padStart(2, '0');
  const dateStr = `${year}-${month}-${day}`;
  
  const startISO = `${dateStr}T${startTime.value}:00`
  const endISO = `${dateStr}T${endTime.value}:00`
  
  try {
     const res = await http.post('/api/study-room/availability', {
        startTime: startISO,
        endTime: endISO
     })
     occupiedSeatIds.value = res.data || []
     console.log('Occupied seats:', occupiedSeatIds.value)
  } catch (e) {
     console.error('Check availability failed', e)
  }
}

// 监听 Tab 切换，刷新数据
watch(activeTab, (val) => {
  if (val === 'reserve') {
    fetchSeats()
  } else if (val === 'my-reservations') {
    fetchMyReservations()
  }
})

const filteredSeats = computed(() => {
  return allSeats.value.filter(s => {
    if (preferences.value.includes('POWER') && s.type !== 'POWER') return false
    if (preferences.value.includes('WINDOW') && s.type !== 'WINDOW') return false
    return true
  }).map(s => {
    // Determine dynamic status
    let status = s.status
    if (occupiedSeatIds.value.includes(s.id) && status === 'AVAILABLE') {
       status = 'OCCUPIED'
    }
    return { ...s, status: status }
  })
})

const selectSeat = (seat) => {
  if (seat.status !== 'AVAILABLE') return
  selectedSeat.value = seat
}

const confirmReservation = async () => {
  if (!selectedSeat.value) return
  if (!searchDate.value || !startTime.value || !endTime.value) {
    ElMessage.warning('请完善时间信息')
    return
  }

  // Use local date string construction
  const year = searchDate.value.getFullYear();
  const month = String(searchDate.value.getMonth() + 1).padStart(2, '0');
  const day = String(searchDate.value.getDate()).padStart(2, '0');
  const dateStr = `${year}-${month}-${day}`;

  const startISO = `${dateStr}T${startTime.value}:00`
  const endISO = `${dateStr}T${endTime.value}:00`

  try {
    await http.post('/api/study-room/reserve', {
      seatId: selectedSeat.value.id,
      startTime: startISO,
      endTime: endISO
    })
    ElMessage.success('预约成功')
    selectedSeat.value = null
    fetchMyReservations()
    activeTab.value = 'my-reservations'
    // Refresh seats
    fetchSeats()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '预约失败')
  }
}

const fetchNotifications = async () => {
  try {
    const res = await http.get('/api/notifications')
    notifications.value = res.data || []
  } catch (e) {
    console.error('Fetch notifications failed', e)
  }
}

// My Reservations
const fetchMyReservations = async () => {
  try {
    const res = await http.get('/api/study-room/reservations/my')
    myReservations.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const handleCheckIn = async (row) => {
  try {
    await http.post(`/api/study-room/check-in/${row.id}`)
    ElMessage.success('签到成功')
    fetchMyReservations()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '签到失败')
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定要取消预约吗？', '提示', { type: 'warning' })
    await http.post(`/api/study-room/cancel/${row.id}`)
    ElMessage.success('已取消')
    fetchMyReservations()
    fetchSeats() // release seat
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.response?.data?.message || '操作失败')
  }
}

// Utils
const formatDate = (iso) => new Date(iso).toLocaleDateString()
const formatTime = (iso) => new Date(iso).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})
const getStatusLabel = (status) => {
  const map = {
    'PENDING': '待确认', 'CONFIRMED': '已预约', 'CHECKED_IN': '使用中',
    'COMPLETED': '已结束', 'CANCELLED': '已取消', 'MISSED': '已违约'
  }
  return map[status] || status
}
const getStatusType = (status) => {
  const map = {
    'CONFIRMED': 'primary', 'CHECKED_IN': 'success', 'CANCELLED': 'info', 'MISSED': 'danger'
  }
  return map[status] || ''
}
const getSeatTypeLabel = (type) => {
  return type === 'POWER' ? '电源座' : '普通座'
}

// Admin Logic (Mock)
const toggleMaintenance = (row) => {
  ElMessage.info('管理员功能演示：状态更新')
  row.status = row.status === 'MAINTENANCE' ? 'AVAILABLE' : 'MAINTENANCE'
}

// Feedback
const submitFeedback = async () => {
  if (!feedbackForm.content && !feedbackForm.rate) {
    ElMessage.warning('请填写反馈内容或评分')
    return
  }
  
  try {
    await http.post('/api/study-room/feedback', {
      rate: feedbackForm.rate,
      content: feedbackForm.content
    })
    ElMessage.success('感谢您的反馈！')
    feedbackForm.content = ''
    feedbackForm.rate = 5
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '提交失败')
  }
}

onMounted(() => {
  fetchSeats()
  fetchMyReservations()
  fetchNotifications()
})

// 监听时间变化，自动刷新占用状态
watch([searchDate, startTime, endTime], () => {
    checkAvailability()
})
</script>

<style scoped>
.study-room-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.main-tabs {
  flex: 1;
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.05);
  display: flex;
  flex-direction: column;
}

:deep(.el-tabs__content) {
  flex: 1;
  overflow: auto;
}

.reserve-layout {
  display: flex;
  gap: 20px;
  height: 100%;
}

.filters-panel {
  width: 300px;
  flex-shrink: 0;
}

.seat-map-panel {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.map-card {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.seat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
  gap: 12px;
  padding: 20px;
  flex: 1;
  overflow-y: auto;
}

.seat-item {
  height: 60px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
  position: relative;
  font-size: 12px;
  color: #606266;
}

.seat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.seat-item.selected {
  background-color: #67C23A;
  color: #fff;
  border-color: #67C23A;
}

.seat-item.occupied {
  background-color: #F56C6C;
  color: #fff;
  cursor: not-allowed;
  opacity: 0.6;
}

.seat-item.maintenance {
  background-color: #909399;
  color: #fff;
  cursor: not-allowed;
}

.power-icon {
  position: absolute;
  top: 4px;
  right: 4px;
  font-size: 10px;
  color: #E6A23C;
}

.seat-item.selected .power-icon {
  color: #fff;
}

.map-footer {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.legend {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 12px;
}

.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 6px;
  border: 1px solid #dcdfe6;
}

.dot.available { background: #fff; }
.dot.occupied { background: var(--el-color-danger); border-color: var(--el-color-danger); }
.dot.maintenance { background: var(--el-color-info); border-color: var(--el-color-info); }
.dot.selected { background: #67C23A; border-color: #67C23A; }

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-item {
  padding: 12px;
  border-radius: 4px;
  background: #f4f4f5;
  border-left: 3px solid #909399;
}

.notification-item.unread {
  background: #ecf5ff;
  border-left-color: #409EFF;
}

.note-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.note-content {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}

.note-time {
  font-size: 12px;
  color: #909399;
  text-align: right;
}
</style>
