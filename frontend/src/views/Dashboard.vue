<template>
  <!-- 仪表盘：收入/支出/结余概览，分类饼图、日趋势柱状图、结余折线图，支持导出PNG/PDF -->
  <el-card>
    <el-row :gutter="12">
      <el-col :span="8"><el-statistic title="本月收入" :value="stats.income" /></el-col>
      <el-col :span="8"><el-statistic title="本月支出" :value="stats.expense" /></el-col>
      <el-col :span="8"><el-statistic title="本月结余" :value="stats.balance" /></el-col>
    </el-row>
  </el-card>
  <el-row :gutter="12" style="margin-top:12px">
    <el-col :span="8">
      <el-card>
        <div class="card-title">分类占比（饼图）</div>
        <div ref="pieRef" style="height:320px"></div>
        <el-space>
          <el-button @click="exportPNG('pie')">导出PNG</el-button>
          <el-button @click="exportPDF('pie')">导出PDF</el-button>
        </el-space>
      </el-card>
    </el-col>
    <el-col :span="8">
      <el-card>
        <div class="card-title">日趋势（柱状图）</div>
        <div ref="barRef" style="height:320px"></div>
        <el-space>
          <el-button @click="exportPNG('bar')">导出PNG</el-button>
          <el-button @click="exportPDF('bar')">导出PDF</el-button>
        </el-space>
      </el-card>
    </el-col>
    <el-col :span="8">
      <el-card>
        <div class="card-title">结余变化（折线图）</div>
        <div ref="lineRef" style="height:320px"></div>
        <el-space>
          <el-button @click="exportPNG('line')">导出PNG</el-button>
          <el-button @click="exportPDF('line')">导出PDF</el-button>
        </el-space>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
/**
 * 仪表盘页面：
 * - 加载后端统计接口 /api/stats/aggregate?period=yyyy-MM
 * - 使用 ECharts 渲染饼图、柱状图与折线图
 * - 支持导出 PNG 与 PDF
 */
import { onMounted, ref, reactive } from 'vue'
import http from '../api/http'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import html2canvas from 'html2canvas'
import jsPDF from 'jspdf'

const pieRef = ref(null)
const barRef = ref(null)
const lineRef = ref(null)
const stats = reactive({ income: 0, expense: 0, balance: 0, categoryPie: {}, trend: [], balanceLine: [] })
const categories = ref([])
const period = new Date().toISOString().slice(0, 7) // yyyy-MM

onMounted(load)

async function load() {
  const [statsRes, catsRes] = await Promise.all([
    http.get('/api/stats/aggregate', { params: { period } }),
    http.get('/api/categories')
  ])
  Object.assign(stats, statsRes.data)
  categories.value = catsRes.data || []
  renderCharts()
}

function renderCharts() {
  // 饼图
  const pie = echarts.init(pieRef.value)
  pie.setOption({
    tooltip: {},
    series: [{
      type: 'pie',
      radius: '60%',
      data: (categories.value.length
        ? categories.value.map(c => ({ name: c.name, value: Number(stats.categoryPie?.[c.name] || 0) }))
        : Object.entries(stats.categoryPie).map(([name, value]) => ({ name, value }))
      )
    }]
  })
  // 柱状图（日收入/支出）
  const bar = echarts.init(barRef.value)
  bar.setOption({
    tooltip: {},
    legend: { data: ['收入', '支出'] },
    xAxis: { type: 'category', data: stats.trend.map(d => d.date.slice(5)) },
    yAxis: { type: 'value' },
    series: [
      { name: '收入', type: 'bar', data: stats.trend.map(d => d.income) },
      { name: '支出', type: 'bar', data: stats.trend.map(d => d.expense) }
    ]
  })
  // 折线图（结余累计）
  const line = echarts.init(lineRef.value)
  line.setOption({
    tooltip: {},
    xAxis: { type: 'category', data: stats.balanceLine.map(d => d.date.slice(5)) },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: stats.balanceLine.map(d => d.balance) }]
  })
}

// 导出 PNG：将图表容器截图
async function exportPNG(which) {
  try {
    const el = which === 'pie' ? pieRef.value : which === 'bar' ? barRef.value : lineRef.value
    const canvas = await html2canvas(el)
    const link = document.createElement('a')
    link.href = canvas.toDataURL('image/png')
    link.download = `chart-${which}.png`
    link.click()
    ElMessage.success('PNG 导出成功')
  } catch {
    ElMessage.error('PNG 导出失败')
  }
}
// 导出 PDF：将图表容器绘制到 PDF
async function exportPDF(which) {
  try {
    const el = which === 'pie' ? pieRef.value : which === 'bar' ? barRef.value : lineRef.value
    const canvas = await html2canvas(el)
    const imgData = canvas.toDataURL('image/png')
    const pdf = new jsPDF('landscape', 'pt', 'a4')
    const w = pdf.internal.pageSize.getWidth()
    const h = (canvas.height / canvas.width) * w
    pdf.addImage(imgData, 'PNG', 20, 20, w - 40, h - 40)
    pdf.save(`chart-${which}.pdf`)
    ElMessage.success('PDF 导出成功')
  } catch {
    ElMessage.error('PDF 导出失败')
  }
}
</script>

<style>
.card-title { margin-bottom: 8px; font-weight: bold; }
</style>
