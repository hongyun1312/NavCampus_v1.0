<template>
  <el-tabs type="border-card">
    <el-tab-pane label="账目记录">
      <div class="toolbar">
        <el-button type="primary" size="small" @click="fetchRecords">刷新</el-button>
      </div>
      <el-table :data="records" style="width: 100%" v-loading="loadingRecords">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="amount" label="金额" />
        <el-table-column prop="type" label="类型" />
        <el-table-column prop="date" label="日期" />
        <el-table-column prop="description" label="描述" />
        <el-table-column label="操作">
          <template #default="scope">
            <el-popconfirm title="确定删除吗？" @confirm="deleteRecord(scope.row.id)">
              <template #reference>
                <el-button type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { auth } from '../../store/auth'
import { ElMessage } from 'element-plus'

const records = ref([])
const loadingRecords = ref(false)

const token = auth.getToken()
const headers = { 
  'Authorization': `Bearer ${token}`,
  'Content-Type': 'application/json'
}

async function fetchRecords() {
  loadingRecords.value = true
  try {
    const res = await fetch('/api/admin/records', { headers })
    if (res.ok) {
      records.value = await res.json()
    } else {
      ElMessage.error('获取记录失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  } finally {
    loadingRecords.value = false
  }
}

async function deleteRecord(id) {
  try {
    const res = await fetch(`/api/admin/records/${id}`, {
      method: 'DELETE',
      headers
    })
    if (res.ok) {
      ElMessage.success('删除成功')
      fetchRecords()
    } else {
      ElMessage.error('删除失败')
    }
  } catch (e) {
    ElMessage.error('网络错误')
  }
}

onMounted(fetchRecords)
</script>

<style scoped>
.toolbar {
  margin-bottom: 15px;
}
</style>
