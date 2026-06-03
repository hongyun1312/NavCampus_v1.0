<template>
  <el-card>
    <el-row justify="space-between" style="margin-bottom:12px">
      <el-col><h4>分类列表</h4></el-col>
      <el-col>
        <el-space>
          <router-link :to="{ name: 'records' }"><el-button>去记账</el-button></router-link>
          <el-button type="primary" @click="openAdd">新增分类</el-button>
        </el-space>
      </el-col>
    </el-row>
    <el-table :data="list">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="type" label="类型" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="visible" title="分类">
    <el-form :model="form" label-width="100px">
      <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
      <el-form-item label="类型">
        <el-select v-model="form.type">
          <el-option label="收入" value="INCOME" />
          <el-option label="支出" value="EXPENSE" />
        </el-select>
      </el-form-item>
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
const form = reactive({ id: null, name: '', type: 'EXPENSE' })
const saving = ref(false)

onMounted(load)
async function load() {
  try {
    const { data } = await http.get('/api/categories')
    list.value = data
  } catch {
    ElMessage.error('加载分类失败')
  }
}
function openAdd() {
  Object.assign(form, { id: null, name: '', type: 'EXPENSE' })
  visible.value = true
}
function openEdit(row) {
  Object.assign(form, row)
  visible.value = true
}
async function save() {
  saving.value = true
  try {
    if (form.id) await http.put(`/api/categories/${form.id}`, form)
    else await http.post('/api/categories', form)
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
    await http.delete(`/api/categories/${id}`)
    ElMessage.success('删除成功')
    await load()
  } catch {
    ElMessage.error('删除失败')
  }
}
</script>

