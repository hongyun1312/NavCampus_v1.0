<template>
  <!-- 批量导入：Excel/CSV上传并导入 -->
  <el-card>
    <h4>批量导入</h4>
    <el-row :gutter="12">
      <el-col :span="12">
        <el-upload :http-request="uploadExcel" :show-file-list="false" :disabled="pendingExcel">
          <el-button type="primary" :loading="pendingExcel" :disabled="pendingExcel">导入 Excel</el-button>
        </el-upload>
      </el-col>
      <el-col :span="12">
        <el-upload :http-request="uploadCsv" :show-file-list="false" :disabled="pendingCsv">
          <el-button :loading="pendingCsv" :disabled="pendingCsv">导入 CSV</el-button>
        </el-upload>
      </el-col>
    </el-row>
    <div style="margin-top:12px">已导入：{{ imported }} 条</div>
  </el-card>
</template>

<script setup>
import { ref } from 'vue'
import http from '../api/http'
import { ElMessage } from 'element-plus'

const imported = ref(0)
const pendingExcel = ref(false)
const pendingCsv = ref(false)
async function uploadExcel({ file }) {
  const fd = new FormData()
  fd.append('file', file)
  pendingExcel.value = true
  try {
    const { data } = await http.post('/api/import/excel', fd)
    imported.value = data.imported
    ElMessage.success(`Excel 导入成功：${data.imported} 条`)
  } catch {
    ElMessage.error('Excel 导入失败')
  } finally {
    pendingExcel.value = false
  }
}
async function uploadCsv({ file }) {
  const fd = new FormData()
  fd.append('file', file)
  pendingCsv.value = true
  try {
    const { data } = await http.post('/api/import/csv', fd)
    imported.value = data.imported
    ElMessage.success(`CSV 导入成功：${data.imported} 条`)
  } catch {
    ElMessage.error('CSV 导入失败')
  } finally {
    pendingCsv.value = false
  }
}
</script>
