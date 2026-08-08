<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>通知管理</span>
        <el-button type="primary" size="small" @click="dialogVisible = true">发布通知</el-button>
      </div>
    </template>

    <el-table :data="notifications" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" show-overflow-tooltip />
      <el-table-column prop="createdAt" label="发布时间" />
      <el-table-column label="操作" width="120">
        <template #default="scope">
          <el-popconfirm title="确定删除吗？" @confirm="deleteNotification(scope.row.id)">
            <template #reference>
              <el-button type="danger" size="small">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" title="发布通知" width="40%" append-to-body>
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="publish">发布</el-button>
        </span>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { auth } from '../../store/auth'
import { ElMessage } from 'element-plus'

const notifications = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ title: '', content: '' })

const token = auth.getToken()
const headers = { 
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}

async function fetchNotifications() {
  loading.value = true
  try {
    const res = await fetch('/api/admin/notifications', { headers })
    if (res.ok) {
      notifications.value = await res.json()
    }
  } finally {
    loading.value = false
  }
}

async function publish() {
  if (!form.value.title || !form.value.content) return ElMessage.warning('请填写完整')
  try {
    const res = await fetch('/api/admin/notifications', {
      method: 'POST',
      headers,
      body: JSON.stringify(form.value)
    })
    if (res.ok) {
      ElMessage.success('发布成功')
      dialogVisible.value = false
      form.value = { title: '', content: '' }
      fetchNotifications()
    } else {
      ElMessage.error('发布失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  }
}

async function deleteNotification(id) {
  try {
    const res = await fetch(`/api/admin/notifications/${id}`, {
      method: 'DELETE',
      headers
    })
    if (res.ok) {
      ElMessage.success('删除成功')
      fetchNotifications()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  }
}

onMounted(fetchNotifications)
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
