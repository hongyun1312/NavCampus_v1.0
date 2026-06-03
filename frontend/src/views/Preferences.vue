<template>
  <!-- 偏好设置：主题色与仪表盘显示偏好（前端本地存储） -->
  <el-card>
    <h4>偏好设置</h4>
    <el-form label-width="120px">
      <el-form-item label="主题颜色">
        <el-color-picker v-model="themeColor" />
      </el-form-item>
      <el-form-item label="显示饼图">
        <el-switch v-model="pref.showPie" />
      </el-form-item>
      <el-form-item label="显示柱状图">
        <el-switch v-model="pref.showBar" />
      </el-form-item>
      <el-form-item label="显示折线图">
        <el-switch v-model="pref.showLine" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
/**
 * 偏好页逻辑：保存主题色与图表显示偏好到本地。
 */
import { ref } from 'vue'
const themeColor = ref(localStorage.getItem('themeColor') || '#409EFF')
const pref = ref(JSON.parse(localStorage.getItem('prefs') || '{"showPie":true,"showBar":true,"showLine":true}'))

function save() {
  localStorage.setItem('themeColor', themeColor.value)
  localStorage.setItem('prefs', JSON.stringify(pref.value))
  ElMessage.success('已保存偏好')
}
</script>

