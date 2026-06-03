<template>
  <el-row :gutter="12">
    <el-col :span="8">
      <el-card>
        <h4>校园卡余额</h4>
        <div style="font-size:22px">{{ balance }}</div>
        <h4 style="margin-top:12px">最近充值</h4>
        <el-table :data="recentTopups">
          <el-table-column prop="time" label="时间" width="180" />
          <el-table-column prop="amount" label="金额" />
        </el-table>
      </el-card>
    </el-col>
    <el-col :span="8">
      <el-card>
        <h4>月度账单</h4>
        <el-select v-model="rangeMonth" style="width:160px">
          <el-option v-for="m in months" :key="m" :label="m" :value="m" />
        </el-select>
        <div ref="pieRef" style="height:280px; margin-top:8px"></div>
      </el-card>
    </el-col>
    <el-col :span="8">
      <el-card>
        <h4>消费趋势</h4>
        <div ref="lineRef" style="height:280px"></div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import http from '../api/http'

const balance = ref(0)
const recentTopups = ref([])
const pieRef = ref(null)
const lineRef = ref(null)
const rangeMonth = ref(new Date().toISOString().slice(0, 7))
const months = Array.from({ length: 6 }, (_, i) => {
  const d = new Date(); d.setMonth(d.getMonth() - i); return d.toISOString().slice(0,7)
})

onMounted(async () => {
  await load()
})

watch(rangeMonth, async () => {
  await load()
})

async function load() {
  const { data } = await http.get('/api/records')
  const cardRecords = data.filter(r => r.account?.name === '校园卡')
  balance.value = cardRecords.reduce((acc, r) => acc + (r.type === 'INCOME' ? r.amount : -r.amount), 0).toFixed(2)
  recentTopups.value = cardRecords.filter(r => r.type === 'INCOME').slice(0, 5)
  const monthData = cardRecords.filter(r => r.time?.slice(0,7) === rangeMonth.value)
  const byCategory = {}
  monthData.forEach(r => {
    const k = r.category?.name || '其他'
    byCategory[k] = (byCategory[k] || 0) + r.amount
  })
  const pie = echarts.init(pieRef.value)
  pie.setOption({ series: [{ type: 'pie', radius: '60%', data: Object.entries(byCategory).map(([n,v]) => ({ name: n, value: v })) }] })
  const byDay = {}
  monthData.forEach(r => {
    const d = r.time?.slice(0,10)
    byDay[d] = (byDay[d] || 0) + (r.type === 'EXPENSE' ? r.amount : 0)
  })
  const days = Object.keys(byDay).sort()
  const line = echarts.init(lineRef.value)
  line.setOption({ xAxis: { type:'category', data: days }, yAxis:{ type:'value' }, series:[{ type:'line', data: days.map(d => byDay[d]) }] })
}
</script>

<style>
h4 { margin-bottom: 8px; }
</style>
