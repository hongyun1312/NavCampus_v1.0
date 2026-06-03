<template>
  <el-row :gutter="12">
    <el-col :span="12">
      <el-card>
        <h4>头像</h4>
        <input type="file" accept="image/*" @change="onFile" />
        <div v-if="imgUrl" style="margin-top:8px">
          <img :src="imgUrl" style="max-width:200px; border-radius:8px" />
          <el-row style="margin-top:8px">
            <el-col :span="6"><el-input-number v-model="crop.x" :min="0" :max="500" label="X" /></el-col>
            <el-col :span="6"><el-input-number v-model="crop.y" :min="0" :max="500" label="Y" /></el-col>
            <el-col :span="6"><el-input-number v-model="crop.w" :min="10" :max="500" label="W" /></el-col>
            <el-col :span="6"><el-input-number v-model="crop.h" :min="10" :max="500" label="H" /></el-col>
          </el-row>
          <el-button style="margin-top:8px" @click="doCrop">裁剪</el-button>
          <div v-if="croppedUrl" style="margin-top:8px">
            <img :src="croppedUrl" style="max-width:200px; border-radius:8px" />
          </div>
        </div>
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card>
        <h4>昵称</h4>
        <el-input v-model="nickname" placeholder="输入昵称" />
        <el-button style="margin-top:8px" type="primary" @click="saveProfile">保存</el-button>
        <h4 style="margin-top:12px">隐私设置</h4>
        <el-switch v-model="visibleToFriends" /> 对好友可见
        <el-switch v-model="visibleToPublic" style="margin-left:12px" /> 对公众可见
        <h4 style="margin-top:12px">操作日志</h4>
        <el-date-picker v-model="logRange" type="daterange" />
        <el-table :data="logs" style="margin-top:8px">
          <el-table-column prop="time" label="时间" width="180" />
          <el-table-column prop="action" label="操作" />
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const imgUrl = ref('')
const croppedUrl = ref('')
const crop = ref({ x: 0, y: 0, w: 200, h: 200 })
const nickname = ref(localStorage.getItem('nickname') || '')
const visibleToFriends = ref(localStorage.getItem('visibleToFriends') === 'true')
const visibleToPublic = ref(localStorage.getItem('visibleToPublic') === 'true')
const logRange = ref([])
const logs = ref([{ time: new Date().toISOString(), action: '登录' }])

function onFile(e) {
  const f = e.target.files[0]
  if (!f) return
  imgUrl.value = URL.createObjectURL(f)
}
function doCrop() {
  const img = new Image()
  img.src = imgUrl.value
  img.onload = () => {
    const c = document.createElement('canvas')
    c.width = crop.value.w
    c.height = crop.value.h
    const ctx = c.getContext('2d')
    ctx.drawImage(img, crop.value.x, crop.value.y, crop.value.w, crop.value.h, 0, 0, crop.value.w, crop.value.h)
    croppedUrl.value = c.toDataURL('image/png')
  }
}
function saveProfile() {
  localStorage.setItem('nickname', nickname.value)
  localStorage.setItem('visibleToFriends', String(visibleToFriends.value))
  localStorage.setItem('visibleToPublic', String(visibleToPublic.value))
  ElMessage.success('保存成功')
}
</script>

<style>
h4 { margin-bottom: 8px; }
</style>
