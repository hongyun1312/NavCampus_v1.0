<template>
  <!-- 账户管理：列表与新增/编辑/删除，支持类型与图标 -->
  <el-card>
    <el-row justify="space-between" style="margin-bottom:12px">
      <el-col><h4>账户列表</h4></el-col>
      <el-col><el-button type="primary" @click="openAdd">新增账户</el-button></el-col>
    </el-row>
    <el-table :data="list">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="balance" label="余额" />
      <el-table-column prop="icon" label="图标类名" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="visible" title="账户">
    <el-form :model="form" label-width="100px">
      <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="类型">
        <el-select v-model="form.type">
          <el-option label="现金" value="CASH" />
          <el-option label="银行卡" value="BANK_CARD" />
          <el-option label="微信" value="WECHAT" />
          <el-option label="支付宝" value="ALIPAY" />
          <el-option label="其他" value="OTHER" />
        </el-select>
      </el-form-item>
      <el-form-item label="余额"><el-input v-model.number="form.balance" type="number" step="0.01" /></el-form-item>
      <el-form-item label="图标类名"><el-input v-model="form.icon" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible=false">取消</el-button>
      <el-button type="primary" @click="save" :loading="saving" :disabled="saving">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import http from '../api/http'
import { ElMessage } from 'element-plus'

const list = ref([])
const visible = ref(false)
const form = reactive({ id: null, name: '', type: 'CASH', balance: 0, icon: '' })
const saving = ref(false)

onMounted(load)
async function load() {
  try {
    const { data } = await http.get('/api/accounts')
    list.value = data
  } catch {
    ElMessage.error('加载账户失败')
  }
}
function openAdd() {
  Object.assign(form, { id: null, name: '', type: 'CASH', balance: 0, icon: '' })
  visible.value = true
}
function openEdit(row) {
  Object.assign(form, row)
  visible.value = true
}
async function save() {
  saving.value = true
  try {
    form.balance = Number(form.balance).toFixed(2)
    if (form.id) await http.put(`/api/accounts/${form.id}`, form)
    else await http.post('/api/accounts', form)
    ElMessage.success('保存成功')
    visible.value = false
    await load()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}
async function remove(id) {
  try {
    await http.delete(`/api/accounts/${id}`)
    ElMessage.success('删除成功')
    await load()
  } catch {
    ElMessage.error('删除失败')
  }
}
</script>
