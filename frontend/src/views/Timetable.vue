<template>
  <el-row :gutter="12">
    <el-col :span="12">
      <el-card shadow="never">
        <h4>今日课程</h4>
        <div style="float:right; margin-top:-30px">
          <el-button size="small" type="danger" @click="clearTimetable">清空</el-button>
          <el-button size="small" @click="importVisible = true">导入</el-button>
        </div>
        <el-timeline>
          <el-timeline-item v-for="c in todayCourses" :key="c.id" :timestamp="c.time" :color="c.important ? '#F56C6C' : ''">
            {{ c.name }} · {{ c.room }} · {{ c.teacher }}
          </el-timeline-item>
        </el-timeline>
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card shadow="never">
        <el-space style="margin-bottom:8px">
          <el-radio-group v-model="viewMode">
            <el-radio-button label="week">周视图</el-radio-button>
            <el-radio-button label="month">月视图</el-radio-button>
          </el-radio-group>
        </el-space>
        <div v-if="viewMode==='week'" class="timetable-grid">
          <div class="grid-header-cell"></div>
          <div v-for="day in weekDays" :key="day" class="grid-header-cell">{{ day }}</div>
          
          <template v-for="time in timeSlots" :key="time">
            <div class="grid-time-cell">{{ time }}</div>
            <div v-for="(day, index) in weekDays" :key="day + time" class="grid-cell">
              <div v-if="getCourse(index, time)" class="course-card" :class="{ 'course-important': getCourse(index, time).important }">
                <div class="course-name">{{ getCourse(index, time).name }}</div>
                <div class="course-room">{{ getCourse(index, time).room }}</div>
                <div class="course-teacher">{{ getCourse(index, time).teacher }}</div>
              </div>
            </div>
          </template>
        </div>
        <div v-else class="calendar-wrapper">
          <el-calendar v-model="calendarDate">
            <template #date-cell="{ data }">
              <div class="custom-calendar-cell" :class="{ 'is-weekend': data.date.getDay() === 0 || data.date.getDay() === 6 }">
                <div class="date-num" :class="{ 'is-today': data.date.toDateString() === new Date().toDateString() }">{{ data.day.split('-').slice(2).join('') }}</div>
                <div v-if="hasExam(data.date)" class="exam-badge">考</div>
              </div>
            </template>
          </el-calendar>
        </div>
      </el-card>
    </el-col>
  </el-row>
  <el-card shadow="never" style="margin-top:12px">
    <el-row>
      <el-col :span="12">
        <div>最近考试倒计时：<b>{{ examCountdown }} 天</b></div>
      </el-col>
      <el-col :span="12">
        <el-tag type="danger" v-if="importantExamSoon">重要考试临近</el-tag>
      </el-col>
    </el-row>
  </el-card>

  <el-card shadow="never" style="margin-top:12px">
    <h4>教师查询</h4>
    <el-space>
      <el-input v-model="teacherQuery" placeholder="姓名" @keyup.enter="searchTeachers" />
      <el-button @click="searchTeachers">查询</el-button>
    </el-space>
    <el-table :data="teacherList" style="margin-top:12px">
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="title" label="职称" width="100" />
      <el-table-column prop="department" label="部门" width="150" />
      <el-table-column prop="email" label="邮箱" width="200" />
      <el-table-column prop="phone" label="电话" width="150" />
      <el-table-column prop="researchArea" label="研究方向" />
    </el-table>
  </el-card>

  <el-dialog v-model="importVisible" title="导入课表 (JSON)">
    <p>请输入JSON格式的课程列表：<el-button link type="primary" @click="fillSample">填入示例数据</el-button></p>
    <el-input type="textarea" v-model="importJson" rows="10" placeholder='[{"name":"C++","room":"101","teacher":"Wang","weekday":1,"time":"08:00"}]' />
    <template #footer>
      <el-button @click="doImport" type="primary">确认导入</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../api/http'

const now = new Date()
const todayCourses = ref([])
const weekCourses = ref([])
const examsList = ref([])
const viewMode = ref('week')
const calendarDate = ref(now)
const examDate = ref(now)
const examCountdown = computed(() => Math.max(0, Math.ceil((examDate.value.getTime() - new Date().getTime()) / 86400000)))
const importantExamSoon = computed(() => examCountdown.value <= 7)

const weekDays = ['一', '二', '三', '四', '五', '六', '日']
const timeSlots = computed(() => {
  const times = new Set(weekCourses.value.map(c => c.time))
  return Array.from(times).sort()
})

function getCourse(dayIndex, time) {
  return weekCourses.value.find(c => {
    // 兼容数字(1-7)和汉字(一...日)
    const d = c.weekday
    const isNum = typeof d === 'number'
    const target = isNum ? (dayIndex + 1) : weekDays[dayIndex]
    return c.weekday == target && c.time === time
  })
}

function hasExam(date) {
  if (!examsList.value.length) return false
  const dStr = date.getFullYear() + '-' + String(date.getMonth() + 1).padStart(2, '0') + '-' + String(date.getDate()).padStart(2, '0')
  return examsList.value.some(e => e.date === dStr)
}

const importVisible = ref(false)
const importJson = ref('')
const teacherQuery = ref('')
const teacherList = ref([])

const sampleData = [
  // 周一
  { "name": "高等数学", "room": "A座教学楼 101", "teacher": "张教授", "weekday": 1, "time": "08:00", "important": true },
  { "name": "大学英语", "room": "B座教学楼 203", "teacher": "李老师", "weekday": 1, "time": "10:00", "important": false },
  { "name": "计算机导论", "room": "A座教学楼 305", "teacher": "王博士", "weekday": 1, "time": "14:00", "important": false },
  // 周二
  { "name": "线性代数", "room": "A座教学楼 102", "teacher": "赵讲师", "weekday": 2, "time": "08:00", "important": true },
  { "name": "C++程序设计", "room": "B座教学楼 机房5", "teacher": "陈老师", "weekday": 2, "time": "14:00", "important": true },
  { "name": "体育", "room": "南体育场", "teacher": "刘教练", "weekday": 2, "time": "16:00", "important": false },
  // 周三
  { "name": "大学物理", "room": "A座教学楼 401", "teacher": "钱教授", "weekday": 3, "time": "08:00", "important": true },
  { "name": "马克思主义原理", "room": "A座教学楼 101", "teacher": "孙老师", "weekday": 3, "time": "10:00", "important": false },
  { "name": "数据结构", "room": "B座教学楼 302", "teacher": "周老师", "weekday": 3, "time": "14:00", "important": true },
  { "name": "创新创业", "room": "图书馆 创客中心", "teacher": "吴导师", "weekday": 3, "time": "19:00", "important": false },
  // 周四
  { "name": "概率论", "room": "A座教学楼 103", "teacher": "郑教授", "weekday": 4, "time": "08:00", "important": true },
  { "name": "数据库原理", "room": "B座教学楼 机房2", "teacher": "冯老师", "weekday": 4, "time": "10:00", "important": true },
  { "name": "形势与政策", "room": "大礼堂", "teacher": "校领导", "weekday": 4, "time": "16:00", "important": false },
  // 周五
  { "name": "操作系统", "room": "B座教学楼 306", "teacher": "陈教授", "weekday": 5, "time": "08:00", "important": true },
  { "name": "计算机网络", "room": "B座教学楼 308", "teacher": "楚老师", "weekday": 5, "time": "10:00", "important": true },
  { "name": "心理健康", "room": "A座教学楼 205", "teacher": "魏老师", "weekday": 5, "time": "14:00", "important": false }
]

onMounted(load)

function fillSample() {
  importJson.value = JSON.stringify(sampleData, null, 2)
}

async function clearTimetable() {
  try {
    await ElMessageBox.confirm('确定要清空所有课程表吗？此操作不可恢复。', '警告', {
      confirmButtonText: '确定清空',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await http.delete('/api/timetable')
    ElMessage.success('课程表已清空')
    load()
  } catch (e) {
    // cancelled or error
  }
}

async function doImport() {
  try {
    const list = JSON.parse(importJson.value)
    await http.post('/api/timetable/import', list)
    ElMessage.success('导入成功')
    importVisible.value = false
    load()
  } catch (e) {
    ElMessage.error('导入失败：格式错误或服务器异常')
  }
}

async function searchTeachers() {
  const { data } = await http.get('/api/teachers', { params: { query: teacherQuery.value } })
  teacherList.value = data
}

async function load() {
  try {
    const [today, week, exams] = await Promise.all([
      http.get('/api/timetable/today'),
      http.get('/api/timetable/week'),
      http.get('/api/timetable/exams')
    ])
    todayCourses.value = today.data || []
    weekCourses.value = week.data || []
    examsList.value = exams.data || []
    const upcoming = (exams.data || []).sort((a,b) => new Date(a.date) - new Date(b.date))[0]
    if (upcoming) examDate.value = new Date(upcoming.date)
  } catch {
    // 后端不可用时回退示例数据
    todayCourses.value = [
      { id: 1, time: '08:00', name: '高等数学', room: 'A101', teacher: '王老师' },
      { id: 2, time: '10:00', name: '大学英语', room: 'B203', teacher: '李老师' }
    ]
    weekCourses.value = [
      { weekday: '一', time: '08:00', name: '高等数学', room: 'A101', teacher: '王老师' }
    ]
    examDate.value = new Date(now.getFullYear(), now.getMonth(), now.getDate() + 12)
  }
}
</script>

<style>
h4 { margin-bottom: 8px; }

/* Timetable Grid */
.timetable-grid {
  display: grid;
  grid-template-columns: 60px repeat(7, 1fr);
  border: 1px solid #ebeef5;
  border-right: none;
  border-bottom: none;
  margin-top: 12px;
}
.grid-header-cell, .grid-time-cell, .grid-cell {
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  text-align: center;
  min-height: 50px;
  box-sizing: border-box;
}
.grid-header-cell {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #606266;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.grid-time-cell {
  background-color: #f5f7fa;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  padding: 4px;
}
.grid-cell {
  padding: 2px;
}
.course-card {
  background-color: #ecf5ff;
  border-radius: 4px;
  padding: 4px;
  font-size: 12px;
  color: #409eff;
  border: 1px solid #d9ecff;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
  overflow: hidden;
}
.course-card.course-important {
  background-color: #fef0f0;
  color: #f56c6c;
  border-color: #fde2e2;
}
.course-name {
  font-weight: bold;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.course-room, .course-teacher {
  font-size: 10px;
  opacity: 0.8;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* Calendar Customization */
.calendar-wrapper :deep(.el-calendar-table .el-calendar-day) {
  padding: 0;
  height: 60px;
}
.custom-calendar-cell {
  height: 100%;
  padding: 4px;
  position: relative;
}
.custom-calendar-cell.is-weekend {
  background-color: #fafafa;
}
.date-num {
  font-size: 14px;
  font-weight: bold;
}
.date-num.is-today {
  color: #409eff;
}
.exam-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  background-color: #f56c6c;
  color: white;
  font-size: 10px;
  padding: 1px 4px;
  border-radius: 2px;
}
</style>
