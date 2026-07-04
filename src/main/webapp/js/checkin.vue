<template>
  <div class="checkin-container">
    <div class="header">
      <h1>考勤打卡</h1>
      <p class="date">{{ currentDate }}</p>
    </div>

    <div class="status-card" v-if="todayRecord">
      <div class="card-title">今日打卡状态</div>
      <div class="status-grid">
        <div class="status-item">
          <div class="status-label">上班打卡</div>
          <div class="status-time" :class="getStatusClass(todayRecord.checkInStatus)">
            {{ formatTime(todayRecord.checkInTime) }}
          </div>
          <div class="status-desc">{{ getStatusDesc(todayRecord.checkInStatus) }}</div>
          <div class="distance-info" v-if="todayRecord.checkInDistance">
            距离: {{ todayRecord.checkInDistance.toFixed(2) }}米
          </div>
        </div>
        <div class="status-item">
          <div class="status-label">下班打卡</div>
          <div class="status-time" :class="getStatusClass(todayRecord.checkOutStatus)">
            {{ formatTime(todayRecord.checkOutTime) }}
          </div>
          <div class="status-desc">{{ getStatusDesc(todayRecord.checkOutStatus) }}</div>
          <div class="distance-info" v-if="todayRecord.checkOutDistance">
            距离: {{ todayRecord.checkOutDistance.toFixed(2) }}米
          </div>
        </div>
      </div>
    </div>

    <div class="action-area">
      <div class="location-info" v-if="location">
        <p>当前位置: {{ location.latitude }}, {{ location.longitude }}</p>
      </div>
      <div class="location-info" v-else-if="!locationLoading">
        <p class="warning">无法获取位置信息，请检查定位权限</p>
      </div>
      <div class="location-info" v-else>
        <p>正在获取位置...</p>
      </div>

      <div class="button-group">
        <button 
          class="check-btn check-in-btn" 
          :disabled="hasCheckedIn || !location || isChecking"
          @click="handleCheckIn"
        >
          <span v-if="isChecking">打卡中...</span>
          <span v-else>上班打卡</span>
        </button>
        <button 
          class="check-btn check-out-btn" 
          :disabled="hasCheckedOut || !hasCheckedIn || !location || isChecking"
          @click="handleCheckOut"
        >
          <span v-if="isChecking">打卡中...</span>
          <span v-else>下班打卡</span>
        </button>
      </div>
    </div>

    <div class="history-section">
      <div class="section-title">最近打卡记录</div>
      <div class="history-list">
        <div class="history-item" v-for="record in recentRecords" :key="record.id">
          <div class="history-date">{{ record.recordDate }}</div>
          <div class="history-times">
            <span>上班: {{ formatTime(record.checkInTime) }}</span>
            <span>下班: {{ formatTime(record.checkOutTime) }}</span>
          </div>
          <div class="history-status">
            <span :class="getStatusClass(record.checkInStatus)">{{ getStatusDesc(record.checkInStatus) }}</span>
            <span :class="getStatusClass(record.checkOutStatus)">{{ getStatusDesc(record.checkOutStatus) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { attendanceApi } from '../api/attendance.js'

const todayRecord = ref(null)
const recentRecords = ref([])
const location = ref(null)
const locationLoading = ref(true)
const isChecking = ref(false)

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' })
})

const hasCheckedIn = computed(() => {
  return todayRecord.value && todayRecord.value.checkInTime
})

const hasCheckedOut = computed(() => {
  return todayRecord.value && todayRecord.value.checkOutTime
})

const formatTime = (time) => {
  if (!time) return '未打卡'
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

const getStatusClass = (status) => {
  switch (status) {
    case 0: return 'status-normal'
    case 1: return 'status-late'
    case 2: return 'status-early'
    case 3: return 'status-missing'
    case 4: return 'status-abnormal'
    default: return 'status-missing'
  }
}

const getStatusDesc = (status) => {
  switch (status) {
    case 0: return '正常'
    case 1: return '迟到'
    case 2: return '早退'
    case 3: return '缺卡'
    case 4: return '异常'
    default: return '缺卡'
  }
}

const getLocation = () => {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        location.value = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        }
        locationLoading.value = false
      },
      (error) => {
        console.error('获取位置失败:', error)
        locationLoading.value = false
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    )
  } else {
    locationLoading.value = false
  }
}

const loadTodayRecord = async () => {
  try {
    const response = await attendanceApi.getTodayRecord()
    if (response.code === 200) {
      todayRecord.value = response.data
    }
  } catch (error) {
    console.error('获取今日打卡记录失败:', error)
  }
}

const loadRecentRecords = async () => {
  try {
    const now = new Date()
    const endDate = now.toISOString().split('T')[0]
    const startDate = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]
    const response = await attendanceApi.getRecordsByDateRange(startDate, endDate)
    if (response.code === 200) {
      recentRecords.value = response.data.filter(r => r.recordDate !== endDate)
    }
  } catch (error) {
    console.error('获取最近打卡记录失败:', error)
  }
}

const handleCheckIn = async () => {
  if (!location.value || isChecking.value) return
  
  isChecking.value = true
  try {
    const response = await attendanceApi.checkIn(location.value.longitude, location.value.latitude)
    if (response.code === 200) {
      alert('上班打卡成功！')
      loadTodayRecord()
    } else {
      alert(response.message || '打卡失败')
    }
  } catch (error) {
    console.error('打卡失败:', error)
    alert(error.response?.data?.message || '打卡失败，请重试')
  } finally {
    isChecking.value = false
  }
}

const handleCheckOut = async () => {
  if (!location.value || isChecking.value) return
  
  isChecking.value = true
  try {
    const response = await attendanceApi.checkOut(location.value.longitude, location.value.latitude)
    if (response.code === 200) {
      alert('下班打卡成功！')
      loadTodayRecord()
    } else {
      alert(response.message || '打卡失败')
    }
  } catch (error) {
    console.error('打卡失败:', error)
    alert(error.response?.data?.message || '打卡失败，请重试')
  } finally {
    isChecking.value = false
  }
}

onMounted(() => {
  getLocation()
  loadTodayRecord()
  loadRecentRecords()
})
</script>

<style scoped>
.checkin-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.header h1 {
  font-size: 28px;
  color: #333;
  margin: 0;
}

.header .date {
  color: #999;
  margin-top: 8px;
}

.status-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 24px;
  color: white;
  margin-bottom: 30px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
}

.status-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.status-item {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 12px;
  padding: 16px;
}

.status-label {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 8px;
}

.status-time {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 4px;
}

.status-desc {
  font-size: 12px;
  opacity: 0.8;
}

.distance-info {
  font-size: 11px;
  opacity: 0.7;
  margin-top: 8px;
}

.action-area {
  background: #f8f9fa;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 30px;
}

.location-info {
  text-align: center;
  margin-bottom: 20px;
  font-size: 14px;
  color: #666;
}

.location-info .warning {
  color: #e74c3c;
}

.button-group {
  display: flex;
  gap: 16px;
}

.check-btn {
  flex: 1;
  height: 56px;
  border: none;
  border-radius: 12px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.check-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.check-in-btn {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
}

.check-in-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(17, 153, 142, 0.4);
}

.check-out-btn {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.check-out-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.4);
}

.history-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 16px;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
}

.history-date {
  font-weight: 600;
  color: #333;
}

.history-times {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #666;
}

.history-status {
  display: flex;
  gap: 8px;
  font-size: 12px;
}

.status-normal {
  color: #27ae60;
}

.status-late {
  color: #f39c12;
}

.status-early {
  color: #e67e22;
}

.status-missing {
  color: #95a5a6;
}

.status-abnormal {
  color: #e74c3c;
}
</style>