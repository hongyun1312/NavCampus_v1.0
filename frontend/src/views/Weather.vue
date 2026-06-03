<template>
  <div class="weather-container">
    <!-- 顶部工具栏：城市切换 -->
    <el-card shadow="never" class="mb-3">
      <el-space>
        <span>当前城市：</span>
        <el-select v-model="city" placeholder="选择城市" style="width: 120px" @change="loadWeather">
          <el-option label="沈阳" value="shenyang" />
          <el-option label="北京" value="beijing" />
          <el-option label="上海" value="shanghai" />
          <el-option label="广州" value="guangzhou" />
          <el-option label="成都" value="chengdu" />
        </el-select>
        <el-button circle icon="Refresh" @click="loadWeather" />
        <span class="update-time" v-if="lastUpdate">更新时间：{{ lastUpdate }}</span>
      </el-space>
    </el-card>

    <el-row :gutter="12">
      <!-- 左侧：当前天气详情 -->
      <el-col :span="8">
        <el-card shadow="hover" class="current-card">
          <div class="current-weather">
            <div class="temp-huge">{{ current?.temperature }}°</div>
            <div class="condition">
              <span class="weather-icon">{{ getWeatherIcon(current?.weathercode) }}</span>
              <span class="weather-text">{{ getWeatherText(current?.weathercode) }}</span>
            </div>
          </div>
          <el-divider />
          <div class="details-grid">
            <div class="detail-item">
              <div class="label">风速</div>
              <div class="value">{{ current?.windspeed }} m/s</div>
            </div>
            <div class="detail-item">
              <div class="label">湿度</div>
              <div class="value">{{ currentHumidity }}%</div>
            </div>
            <div class="detail-item">
              <div class="label">日出</div>
              <div class="value">{{ todayDaily?.sunrise ? formatTime(todayDaily.sunrise) : '--:--' }}</div>
            </div>
            <div class="detail-item">
              <div class="label">日落</div>
              <div class="value">{{ todayDaily?.sunset ? formatTime(todayDaily.sunset) : '--:--' }}</div>
            </div>
          </div>
          <el-alert v-if="alertText" :title="alertText" type="error" show-icon :closable="false" style="margin-top: 16px" />
        </el-card>
      </el-col>

      <!-- 右侧：24小时趋势图 -->
      <el-col :span="16">
        <el-card shadow="hover">
          <h4>24小时气温趋势</h4>
          <div ref="hourlyRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：7天预报 -->
    <el-card shadow="hover" class="mt-3">
      <h4>未来7天预报</h4>
      <div class="forecast-row">
        <div v-for="(day, index) in dailyForecast" :key="index" class="forecast-item">
          <div class="f-date">{{ formatDate(day.time) }}</div>
          <div class="f-icon">{{ getWeatherIcon(day.weathercode) }}</div>
          <div class="f-temp">
            <span class="max">{{ day.tempMax }}°</span>
            <span class="separator">/</span>
            <span class="min">{{ day.tempMin }}°</span>
          </div>
          <div class="f-text">{{ getWeatherText(day.weathercode) }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import http from '../api/http'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const city = ref('shenyang')
const weatherData = ref(null)
const lastUpdate = ref('')
const hourlyRef = ref(null)
let chartInstance = null

// 核心数据计算
const current = computed(() => weatherData.value?.current_weather)
const currentHumidity = computed(() => {
  if (!weatherData.value?.hourly?.relativehumidity_2m) return 0
  // 获取当前小时对应的湿度
  const hourIndex = new Date().getHours()
  return weatherData.value.hourly.relativehumidity_2m[hourIndex] || 0
})
const todayDaily = computed(() => {
  if (!weatherData.value?.daily) return null
  return {
    sunrise: weatherData.value.daily.sunrise[0],
    sunset: weatherData.value.daily.sunset[0]
  }
})
const dailyForecast = computed(() => {
  if (!weatherData.value?.daily) return []
  const d = weatherData.value.daily
  return d.time.map((t, i) => ({
    time: t,
    weathercode: d.weathercode[i],
    tempMax: d.temperature_2m_max[i],
    tempMin: d.temperature_2m_min[i]
  }))
})
const alertText = computed(() => {
  const w = current.value?.windspeed || 0
  return w >= 15 ? '大风预警：风速较高，请注意安全' : ''
})

onMounted(() => {
  loadWeather()
  window.addEventListener('resize', resizeChart)
})

function resizeChart() {
  chartInstance?.resize()
}

async function loadWeather() {
  try {
    const { data } = await http.get('/api/weather', { params: { city: city.value } })
    if (data.error) {
      ElMessage.error('获取天气失败')
      return
    }
    weatherData.value = data
    lastUpdate.value = new Date().toLocaleTimeString()
    nextTick(renderChart)
  } catch (e) {
    ElMessage.error('网络异常，无法获取天气')
  }
}

function renderChart() {
  if (!hourlyRef.value || !weatherData.value?.hourly) return
  
  if (chartInstance) chartInstance.dispose()
  chartInstance = echarts.init(hourlyRef.value)
  
  const h = weatherData.value.hourly
  // 取未来24小时
  const nowIdx = new Date().getHours()
  const times = h.time.slice(nowIdx, nowIdx + 24).map(t => t.slice(11, 16))
  const temps = h.temperature_2m.slice(nowIdx, nowIdx + 24)

  chartInstance.setOption({
    grid: { top: 30, right: 20, bottom: 20, left: 40 },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: times },
    yAxis: { type: 'value', axisLabel: { formatter: '{value} °C' } },
    series: [{
      data: temps,
      type: 'line',
      smooth: true,
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64,158,255,0.5)' },
          { offset: 1, color: 'rgba(64,158,255,0.1)' }
        ])
      },
      itemStyle: { color: '#409eff' }
    }]
  })
}

// 辅助函数
function formatTime(isoStr) {
  return isoStr ? isoStr.slice(11, 16) : ''
}
function formatDate(isoStr) {
  const d = new Date(isoStr)
  return `${d.getMonth() + 1}/${d.getDate()}`
}

// 天气代码映射
function getWeatherText(code) {
  if (code === undefined) return '--'
  if (code === 0) return '晴'
  if (code >= 1 && code <= 3) return '多云'
  if (code >= 45 && code <= 48) return '雾'
  if (code >= 51 && code <= 55) return '毛毛雨'
  if (code >= 61 && code <= 67) return '雨'
  if (code >= 71 && code <= 77) return '雪'
  if (code >= 80 && code <= 82) return '阵雨'
  if (code >= 95 && code <= 99) return '雷雨'
  return '未知'
}

function getWeatherIcon(code) {
  if (code === undefined) return '❓'
  if (code === 0) return '☀️'
  if (code >= 1 && code <= 3) return '⛅'
  if (code >= 45 && code <= 48) return '🌫️'
  if (code >= 51 && code <= 67) return '🌧️'
  if (code >= 71 && code <= 77) return '❄️'
  if (code >= 95 && code <= 99) return '⛈️'
  return '🌦️'
}
</script>

<style scoped>
.mb-3 { margin-bottom: 12px; }
.mt-3 { margin-top: 12px; }

.current-card {
  height: 380px;
  display: flex;
  flex-direction: column;
}

.current-weather {
  text-align: center;
  padding: 20px 0;
}
.temp-huge {
  font-size: 64px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}
.condition {
  margin-top: 10px;
  font-size: 20px;
  color: #606266;
}
.weather-icon { font-size: 24px; margin-right: 8px; }

.details-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  padding: 10px;
}
.detail-item {
  text-align: center;
}
.detail-item .label {
  font-size: 12px;
  color: #909399;
}
.detail-item .value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.forecast-row {
  display: flex;
  justify-content: space-between;
  overflow-x: auto;
  padding: 10px 0;
}
.forecast-item {
  flex: 1;
  text-align: center;
  min-width: 80px;
  border-right: 1px solid #ebeef5;
}
.forecast-item:last-child { border-right: none; }

.f-date { font-size: 14px; color: #909399; margin-bottom: 5px; }
.f-icon { font-size: 24px; margin-bottom: 5px; }
.f-temp { font-size: 14px; font-weight: bold; margin-bottom: 5px; }
.f-temp .max { color: #f56c6c; }
.f-temp .min { color: #409eff; }
.f-text { font-size: 12px; color: #606266; }

.update-time {
  font-size: 12px;
  color: #909399;
  margin-left: 10px;
}
</style>
