<template>
  <el-row :gutter="12">
    <el-col :span="12">
      <el-card>
        <h4>个人中心</h4>
        <el-space style="margin-bottom:8px">
          <el-avatar :size="64" :src="croppedUrl || imgUrl" />
          <input type="file" accept="image/*" @change="onFile" />
        </el-space>
        <div v-if="imgUrl">
          <el-row>
            <el-col :span="6"><el-input-number v-model="crop.x" :min="0" :max="500" label="X" /></el-col>
            <el-col :span="6"><el-input-number v-model="crop.y" :min="0" :max="500" label="Y" /></el-col>
            <el-col :span="6"><el-input-number v-model="crop.w" :min="10" :max="500" label="W" /></el-col>
            <el-col :span="6"><el-input-number v-model="crop.h" :min="10" :max="500" label="H" /></el-col>
          </el-row>
          <el-button style="margin-top:8px" @click="doCrop">裁剪</el-button>
        </div>
        <el-divider />
        <h5>昵称</h5>
        <el-input v-model="nickname" placeholder="输入昵称" />
        <el-divider />
        <h5>隐私设置</h5>
        <el-switch v-model="visibleToFriends" /> 对好友可见
        <el-switch v-model="visibleToPublic" style="margin-left:12px" /> 对公众可见
      </el-card>
    </el-col>
    <el-col :span="12">
      <el-card>
        <h4>系统设置与偏好</h4>
        <h5>画面质量</h5>
        <el-select v-model="quality">
          <el-option label="低" value="low" />
          <el-option label="中" value="medium" />
          <el-option label="高" value="high" />
        </el-select>
        <el-switch v-model="effects" style="margin-left:12px" /> 特效
        <el-divider />
        <h5>通知偏好</h5>
        <el-switch v-model="push" /> 推送
        <el-time-picker v-model="dndStart" placeholder="免打扰开始" style="margin-left:12px" />
        <el-time-picker v-model="dndEnd" placeholder="免打扰结束" style="margin-left:12px" />
        <el-divider />
        <h5>3D灵敏度</h5>
        <el-slider v-model="rotateSpeed" :min="0.1" :max="3" :step="0.1" show-input />
        <el-slider v-model="zoomSpeed" :min="0.1" :max="3" :step="0.1" show-input />
      </el-card>
    </el-col>
  </el-row>
  <el-button type="primary" style="margin-top:12px" @click="save">保存全部</el-button>
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
const quality = ref(localStorage.getItem('quality') || 'medium')
const effects = ref(localStorage.getItem('effects') === 'true')
const rotateSpeed = ref(Number(localStorage.getItem('rotateSpeed') || 1))
const zoomSpeed = ref(Number(localStorage.getItem('zoomSpeed') || 1))
const push = ref(localStorage.getItem('push') !== 'false')
const dndStart = ref(null)
const dndEnd = ref(null)
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
function save() {
  localStorage.setItem('nickname', nickname.value)
  localStorage.setItem('visibleToFriends', String(visibleToFriends.value))
  localStorage.setItem('visibleToPublic', String(visibleToPublic.value))
  localStorage.setItem('quality', quality.value)
  localStorage.setItem('effects', String(effects.value))
  localStorage.setItem('rotateSpeed', String(rotateSpeed.value))
  localStorage.setItem('zoomSpeed', String(zoomSpeed.value))
  localStorage.setItem('push', String(push.value))
  localStorage.setItem('dndStart', dndStart.value ? String(dndStart.value) : '')
  localStorage.setItem('dndEnd', dndEnd.value ? String(dndEnd.value) : '')
  ElMessage.success('已保存')
}
</script>

<style>
h4 { margin-bottom: 8px; }
h5 { margin-bottom: 8px; }
</style>
