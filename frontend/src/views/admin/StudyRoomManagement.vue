<template>
  <div class="study-room-management">
    <div class="header">
      <h2>自习室管理后台</h2>
    </div>
    <el-card>
      <el-tabs>
        <!-- Tab 1: 座位管理 -->
        <el-tab-pane label="座位管理">
          <div class="actions" style="margin-bottom: 20px;">
             <el-button type="primary" @click="fetchSeats">刷新列表</el-button>
          </div>
          <el-table :data="allSeats" height="500" border stripe v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="座位名称" width="120" />
            <el-table-column prop="section" label="区域" />
            <el-table-column prop="type" label="类型" width="100">
               <template #default="{ row }">
                 <el-tag :type="row.type === 'POWER' ? 'warning' : 'info'">{{ row.type }}</el-tag>
               </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作">
              <template #default="{ row }">
                <el-button size="small" type="danger" @click="toggleMaintenance(row)">
                  {{ row.status === 'MAINTENANCE' ? '结束维护' : '设为维护' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        
        <!-- Tab 2: 数据统计 -->
        <el-tab-pane label="数据统计">
           <div class="stats-container">
             <el-row :gutter="20">
               <el-col :span="8">
                 <el-statistic title="总座位数" :value="stats.total" />
               </el-col>
               <el-col :span="8">
                 <el-statistic title="当前占用" :value="stats.occupied" />
               </el-col>
               <el-col :span="8">
                 <el-statistic title="占用率" :value="stats.percentage" suffix="%" :precision="1">
                   <template #suffix>
                     <el-icon style="vertical-align: -0.125em">
                       <Warning />
                     </el-icon>
                   </template>
                 </el-statistic>
               </el-col>
             </el-row>
             <div style="margin-top: 40px; height: 300px; display: flex; align-items: center; justify-content: center; background: #f5f7fa; border-radius: 8px;">
               <span style="color: #909399;">📊 详细趋势图表开发中...</span>
             </div>
           </div>
        </el-tab-pane>

        <!-- Tab 3: 消息通知 -->
        <el-tab-pane label="消息通知">
          <div class="notification-panel">
            <el-form :inline="true" :model="notificationForm" class="demo-form-inline">
              <el-form-item label="标题">
                <el-input v-model="notificationForm.title" placeholder="通知标题" />
              </el-form-item>
              <el-form-item label="类型">
                <el-select v-model="notificationForm.type" placeholder="选择类型" style="width: 120px">
                  <el-option label="系统公告" value="SYSTEM" />
                  <el-option label="维护通知" value="MAINTENANCE" />
                  <el-option label="紧急通知" value="EMERGENCY" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="sendNotification">发布通知</el-button>
              </el-form-item>
            </el-form>
            <el-input v-model="notificationForm.content" type="textarea" rows="3" placeholder="通知内容..." style="margin-bottom: 20px" />
            
            <el-table :data="notifications" height="400" border stripe>
              <el-table-column prop="title" label="标题" width="150" />
              <el-table-column prop="content" label="内容" show-overflow-tooltip />
              <el-table-column prop="type" label="类型" width="100">
                <template #default="{ row }">
                   <el-tag :type="getNotifyTypeTag(row.type)">{{ getNotifyTypeLabel(row.type) }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createdAt" label="发布时间" width="180">
                 <template #default="{ row }">
                   {{ formatTime(row.createdAt) }}
                 </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                   <el-button type="danger" size="small" @click="deleteNotification(row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <!-- Tab 4: 黑名单管理 -->
        <el-tab-pane label="黑名单管理">
          <div class="blacklist-panel">
            <div class="actions" style="margin-bottom: 20px;">
              <el-button type="primary" @click="fetchUsers">刷新用户列表</el-button>
            </div>
            <el-table :data="users" height="500" border stripe>
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="username" label="用户名" width="150" />
              <el-table-column prop="email" label="邮箱" />
              <el-table-column prop="role" label="角色" width="100" />
              <el-table-column prop="blacklisted" label="状态" width="120">
                <template #default="{ row }">
                  <el-tag :type="row.blacklisted ? 'danger' : 'success'">
                    {{ row.blacklisted ? '已封禁' : '正常' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button 
                    :type="row.blacklisted ? 'success' : 'danger'" 
                    size="small" 
                    @click="toggleBlacklist(row)"
                    :disabled="row.role === 'ADMIN'"
                  >
                    {{ row.blacklisted ? '解封' : '封禁' }}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import http from '../../api/http'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Warning } from '@element-plus/icons-vue'

// --- State ---
const loading = ref(false)
const allSeats = ref([])
const stats = ref({ total: 0, occupied: 0, percentage: 0 })

const notifications = ref([])
const notificationForm = reactive({
  title: '',
  content: '',
  type: 'SYSTEM'
})

const users = ref([])
const logs = ref([])

// --- API Calls ---

// 1. Seats
const fetchSeats = async () => {
  loading.value = true
  try {
    const res = await http.get('/api/study-room/seats')
    allSeats.value = res.data || []
  } catch (e) {
    ElMessage.error('获取座位数据失败')
  } finally {
    loading.value = false
  }
}

const toggleMaintenance = async (row) => {
  try {
    const newStatus = row.status === 'MAINTENANCE' ? 'AVAILABLE' : 'MAINTENANCE'
    await http.post(`/api/study-room/seats/${row.id}/status`, { status: newStatus })
    ElMessage.success('状态更新成功')
    row.status = newStatus
    fetchStats()
  } catch (e) {
    ElMessage.error('更新失败')
  }
}

const getStatusType = (status) => {
  const map = { 'AVAILABLE': 'success', 'MAINTENANCE': 'danger', 'OCCUPIED': 'warning' }
  return map[status] || 'info'
}

// 2. Stats
const fetchStats = async () => {
  try {
    const res = await http.get('/api/study-room/stats')
    stats.value = res.data || { total: 0, occupied: 0, percentage: 0 }
  } catch (e) {
    console.error(e)
  }
}

// 3. Notifications
const fetchNotifications = async () => {
  try {
    const res = await http.get('/api/notifications')
    notifications.value = res.data || []
  } catch (e) {
    console.error(e)
  }
}

const sendNotification = async () => {
  if (!notificationForm.title || !notificationForm.content) {
    ElMessage.warning('请填写完整')
    return
  }
  try {
    await http.post('/api/notifications', { ...notificationForm })
    ElMessage.success('发布成功')
    notificationForm.title = ''
    notificationForm.content = ''
    fetchNotifications()
  } catch (e) {
    ElMessage.error('发布失败')
  }
}

const deleteNotification = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该通知吗？', '提示', { type: 'warning' })
    await http.delete(`/api/notifications/${id}`)
    ElMessage.success('已删除')
    fetchNotifications()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const getNotifyTypeLabel = (type) => {
  const map = { 'SYSTEM': '系统公告', 'MAINTENANCE': '维护通知', 'EMERGENCY': '紧急通知' }
  return map[type] || type
}

const getNotifyTypeTag = (type) => {
  const map = { 'SYSTEM': '', 'MAINTENANCE': 'warning', 'EMERGENCY': 'danger' }
  return map[type] || 'info'
}

const formatTime = (iso) => new Date(iso).toLocaleString()

// 4. Blacklist (Users)
const fetchUsers = async () => {
  try {
    const res = await http.get('/api/admin/users')
    users.value = res.data || []
  } catch (e) {
    ElMessage.error('获取用户列表失败')
  }
}

const toggleBlacklist = async (row) => {
  try {
    const action = row.blacklisted ? '解封' : '封禁'
    await ElMessageBox.confirm(`确定要${action}用户 ${row.username} 吗？`, '提示', { type: 'warning' })
    
    await http.put(`/api/admin/users/${row.id}/blacklist`, { isBlacklisted: !row.blacklisted })
    ElMessage.success(`${action}成功`)
    row.blacklisted = !row.blacklisted
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

// --- Lifecycle ---
onMounted(() => {
  fetchSeats()
  fetchStats()
  fetchNotifications()
  fetchUsers()
})
</script>

<style scoped>
.study-room-management {
  padding: 20px;
}
.header {
  margin-bottom: 20px;
}
.stats-container {
  padding: 20px;
}
.notification-panel, .blacklist-panel {
  padding: 10px;
}
</style>
