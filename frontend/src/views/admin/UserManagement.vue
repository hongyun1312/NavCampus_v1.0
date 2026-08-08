<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>用户列表</span>
        <el-button type="primary" size="small" @click="fetchUsers">刷新</el-button>
      </div>
    </template>
    
    <el-table :data="users" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="role" label="角色">
        <template #default="scope">
          <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'info'">{{ scope.row.role }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="250">
        <template #default="scope">
          <el-button size="small" @click="openEdit(scope.row)">编辑角色</el-button>
          <el-button size="small" type="info" @click="openNotifications(scope.row)">查看通知</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="修改角色" width="30%" append-to-body>
      <el-form :model="form">
        <el-form-item label="角色">
          <el-select v-model="form.role">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveRole">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="notificationDialogVisible" :title="currentUserName + ' 的通知'" width="50%" append-to-body>
      <el-table :data="userNotifications" v-loading="notificationLoading" style="width: 100%" height="400">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" width="150" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="时间" width="160" />
      </el-table>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { auth } from '../../store/auth'
import { ElMessage } from 'element-plus'

const users = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ id: null, role: 'USER' })

// User Notifications Logic
const notificationDialogVisible = ref(false)
const notificationLoading = ref(false)
const userNotifications = ref([])
const currentUserName = ref('')

function getHeaders() {
  const token = auth.getToken()
  return { 
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
}

async function fetchUsers() {
  loading.value = true
  try {
    const res = await fetch('/api/admin/users', { headers: getHeaders() })
    if (res.ok) {
      users.value = await res.json()
    } else if (res.status === 401) {
      ElMessage.error('登录已过期或权限不足，请重新登录')
      auth.clear()
      location.href = '/login'
    } else {
      ElMessage.error('获取用户列表失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    loading.value = false
  }
}

function openEdit(row) {
  form.value = { id: row.id, role: row.role }
  dialogVisible.value = true
}

async function openNotifications(row) {
  currentUserName.value = row.username
  notificationDialogVisible.value = true
  notificationLoading.value = true
  userNotifications.value = []
  
  try {
    const res = await fetch(`/api/admin/users/${row.id}/notifications`, { headers: getHeaders() })
    if (res.ok) {
      userNotifications.value = await res.json()
    } else {
      ElMessage.error('获取通知失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    notificationLoading.value = false
  }
}

async function saveRole() {
  try {
    const res = await fetch(`/api/admin/users/${form.value.id}/role`, {
      method: 'PUT',
      headers: getHeaders(),
      body: JSON.stringify({ role: form.value.role })
    })
    if (res.ok) {
      ElMessage.success('修改成功')
      dialogVisible.value = false
      fetchUsers()
    } else {
      ElMessage.error('修改失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  }
}

onMounted(fetchUsers)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
