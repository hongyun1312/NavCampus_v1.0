<template>
  <!-- 预算管理：设置整体/分类的预算额度，按周期（yyyy-MM）管理，查看阈值提醒通过通知页 -->
  <el-card>
    <el-row :gutter="12">
      <el-col :span="6">
        <el-input v-model="period" placeholder="周期（yyyy-MM）" />
      </el-col>
      <el-col :span="4">
        <el-button @click="load">查询</el-button>
      </el-col>
      <el-col :span="8">
        <el-button type="primary" @click="openAdd">新增预算</el-button>
      </el-col>
    </el-row>
    <el-table :data="list" style="margin-top:12px">
      <el-table-column prop="type" label="类型" />
      <el-table-column prop="amount" label="额度" />
      <el-table-column prop="period" label="周期" />
      <el-table-column prop="category.name" label="分类" />
    </el-table>
  </el-card>

  <el-dialog v-model="visible" title="设置预算">
    <el-form :model="form" label-width="120px">
      <el-form-item label="类型">
        <el-select v-model="form.type">
          <el-option label="整体预算" value="TOTAL" />
          <el-option label="分类预算" value="CATEGORY" />
        </el-select>
      </el-form-item>
      <el-form-item label="额度">
        <el-input v-model.number="form.amount" type="number" step="0.01" />
      </el-form-item>
      <el-form-item label="周期">
        <el-input v-model="form.period" />
      </el-form-item>
      <el-form-item v-if="form.type==='CATEGORY'" label="分类">
        <el-select v-model="form.category.id" filterable placeholder="选择分类">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
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
/**
 * 预算页逻辑：创建与查询预算，阈值提醒通过通知页展示。
 */
import { ref, reactive, onMounted } from 'vue'
import http from '../api/http'
import { ElMessage } from 'element-plus'

const period = ref(new Date().toISOString().slice(0, 7))
const list = ref([])
const visible = ref(false)
const categories = ref([])
const form = reactive({ type: 'TOTAL', amount: 0, period: period.value, category: { id: null } })
const saving = ref(false)

onMounted(async () => {
  try {
    const { data } = await http.get('/api/categories')
    categories.value = data
    await load()
  } catch {
    ElMessage.error('初始化失败')
  }
})

async function load() {
  try {
    const { data } = await http.get('/api/budgets', { params: { period: period.value } })
    list.value = data
  } catch {
    ElMessage.error('查询预算失败')
  }
}
function openAdd() {
  Object.assign(form, { type: 'TOTAL', amount: 0, period: period.value, category: { id: null } })
  visible.value = true
}
async function save() {
  // 构造提交体：当 TOTAL 时不传 category；CATEGORY 时传 category.id
  const payload = { type: form.type, amount: Number(form.amount).toFixed(2), period: form.period }
  if (form.type === 'CATEGORY' && form.category.id) payload.category = { id: form.category.id }
  saving.value = true
  try {
    await http.post('/api/budgets', payload)
    ElMessage.success('设置成功')
    visible.value = false
    await load()
  } catch {
    ElMessage.error('设置失败')
  } finally {
    saving.value = false
  }
}
</script>
