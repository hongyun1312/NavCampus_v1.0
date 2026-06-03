<template>
  <div class="data-manage">
    <h2>系统数据概览</h2>
    <el-table :data="records" style="width: 100%">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="time" label="时间" width="180" />
      <el-table-column prop="amount" label="金额" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="location" label="地点" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-popconfirm title="确定删除此记录吗？" @confirm="deleteRecord(scope.row.id)">
            <template #reference>
              <el-button size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '../../api/http'
import { ElMessage } from 'element-plus'

const records = ref([])

const fetchRecords = async () => {
  try {
    const res = await http.get('/api/admin/records')
    records.value = res.data
  } catch (e) {
    ElMessage.error('获取记录失败')
  }
}

const deleteRecord = async (id) => {
  try {
    await http.delete(`/api/admin/records/${id}`)
    ElMessage.success('删除成功')
    fetchRecords()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(fetchRecords)
</script>
