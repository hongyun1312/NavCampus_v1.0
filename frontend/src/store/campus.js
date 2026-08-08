import { reactive } from 'vue'

export const campusState = reactive({
  // Layers
  showCourses: false,
  showSpending: false,
  showWeather: false,
  showLabels: true,

  // Quick Actions (Triggered by sidebar, consumed by Campus3D)
  actionTrigger: null, // 'quickRecord', etc.
})
