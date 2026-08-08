<template>
  <!-- 记账页面：列表/筛选与新增记录（支持收入/支出/转账、时间精确到分钟、富文本备注） -->
  <el-card>
    <el-row :gutter="12" style="margin-bottom:12px">
      <el-col :span="6">
        <el-date-picker v-model="range" type="datetimerange" value-format="YYYY-MM-DDTHH:mm:ss" start-placeholder="开始" end-placeholder="结束" />
      </el-col>
      <el-col :span="4">
        <el-button @click="load">查询</el-button>
        <el-button type="danger" @click="delAll">一键删除</el-button>
      </el-col>
    </el-row>
    <el-table :data="pagedRecords" size="small">
      <el-table-column prop="time" label="时间" width="180" />
      <el-table-column prop="type" label="类型" width="90" />
      <el-table-column prop="amount" label="金额" width="120" />
      <el-table-column prop="category.name" label="分类" />
      <el-table-column prop="account.name" label="账户" />
      <el-table-column prop="targetAccount.name" label="目标账户" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin-top:12px; display:flex; justify-content:flex-end">
      <el-pagination
        background
        :page-sizes="[5,10,15,20]"
        :page-size="pageSize"
        :current-page="currentPage"
        :total="records.length"
        layout="sizes, prev, pager, next, jumper"
        @size-change="onSizeChange"
        @current-change="onPageChange"
      />
    </div>
  </el-card>

  <el-card style="margin-top:12px">
    <h4>新增记录</h4>
    <el-form :model="form" label-width="100px" @submit.prevent="create">
      <el-form-item label="类型">
        <el-select v-model="form.type" placeholder="选择类型">
          <el-option label="收入" value="INCOME" />
          <el-option label="支出" value="EXPENSE" />
          <el-option label="转账" value="TRANSFER" />
        </el-select>
      </el-form-item>
      <el-form-item label="金额">
        <el-input v-model.number="form.amount" type="number" step="0.01" />
      </el-form-item>
      <el-form-item label="时间">
        <el-date-picker v-model="formTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" placeholder="选择时间" />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" filterable placeholder="选择分类">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="账户">
        <el-select v-model="form.accountId" filterable placeholder="选择账户">
          <el-option v-for="a in accounts" :key="a.id" :label="a.name" :value="a.id" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="form.type==='TRANSFER'" label="目标账户">
        <el-select v-model="form.targetAccountId" filterable placeholder="选择目标账户">
          <el-option v-for="a in accounts" :key="a.id" :label="a.name" :value="a.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <!-- 备注输入：为避免第三方富文本库未安装导致页面无法加载，这里使用内置文本域。
             如需富文本，可改回 QuillEditor 并确保依赖已安装与样式已引入。 -->
        <el-input
          v-model="form.remark"
          type="textarea"
          :rows="4"
          placeholder="输入备注（支持多行）" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" native-type="submit" :loading="creating" :disabled="creating">提交</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
/**
 * 记录页逻辑：列表查询/时间范围筛选、新增与删除。
 * 备注使用富文本（Quill）。
 */
import { ref, reactive, onMounted, computed, watch } from 'vue'
import http from '../api/http'
import { ElMessage, ElMessageBox } from 'element-plus'

const records = ref([])
const categories = ref([])
const accounts = ref([])
const range = ref([])
// 新增记录表单数据
const form = reactive({ type: 'EXPENSE', amount: 0, time: '', categoryId: null, accountId: null, targetAccountId: null, remark: '' })
// 到分钟
const formTime = ref()
const creating = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)
const pagedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return records.value.slice(start, start + pageSize.value)
})

onMounted(async () => {
  try {
    await loadBasics()
    await load()
  } catch (e) {
    ElMessage.error('加载数据失败')
  }
})

// 根据记录类型过滤分类（收入仅显示 INCOME，支出仅显示 EXPENSE）
const filteredCategories = computed(() => {
  if (!categories.value?.length) return []
  const want = form.type === 'INCOME' ? 'INCOME' : 'EXPENSE'
  return categories.value.filter(c => c.type === want)
})

// 当类型变更为非转账时清空目标账户
watch(() => form.type, (t) => {
  if (t !== 'TRANSFER') form.targetAccountId = null
})

async function loadBasics() {
  const [cats, accs] = await Promise.all([http.get('/api/categories'), http.get('/api/accounts')])
  categories.value = cats.data
  accounts.value = accs.data
}
async function load() {
  try {
    if (range.value?.length === 2) {
      const [start, end] = range.value
      const p = { start, end }
      const { data } = await http.get('/api/records/range', { params: p })
      records.value = data
    } else {
      const { data } = await http.get('/api/records')
      records.value = data
    }
    currentPage.value = 1
  } catch (e) {
    const msg = e?.response?.data?.message || e?.message || '查询记录失败'
    ElMessage.error(msg)
  }
}
async function create() {
  // 基础校验：类型、金额、账户、时间；转账时需目标账户
  if (!form.type) return ElMessage.error('请选择类型')
  if (!form.amount || Number(form.amount) <= 0) return ElMessage.error('请输入有效金额')
  if (!formTime.value) return ElMessage.error('请选择时间')
  if (!form.accountId) return ElMessage.error('请选择账户')
  if (form.type === 'TRANSFER' && !form.targetAccountId) return ElMessage.error('请选择目标账户')

  form.time = formTime.value
  form.amount = parseFloat(Number(form.amount).toFixed(2))
  creating.value = true
  try {
    await http.post('/api/records', form)
    ElMessage.success('新增成功')
    await load()
    Object.assign(form, { type: 'EXPENSE', amount: 0, time: '', categoryId: null, accountId: null, targetAccountId: null, remark: '' })
    formTime.value = null
  } catch (e) {
    ElMessage.error('新增失败')
  } finally {
    creating.value = false
  }
}
async function del(id) {
  try {
    await ElMessageBox.confirm('确认删除该记录吗？', '提示', { type: 'warning' })
    await http.delete(`/api/records/${id}`)
    ElMessage.success('删除成功')
    await load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}
async function delAll() {
  try {
    await ElMessageBox.confirm('确认删除所有记录吗？此操作不可恢复！', '警告', { type: 'warning' })
    await http.delete('/api/records')
    ElMessage.success('已删除所有记录')
    await load()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}
function onSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
}
function onPageChange(page) {
  currentPage.value = page
}

</script>

<style>
/* 响应式布局简化适配 */
</style>
