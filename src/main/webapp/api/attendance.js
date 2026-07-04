import axios from 'axios'

const instance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
}, error => {
  return Promise.reject(error)
})

instance.interceptors.response.use(response => {
  if (response.data.code === 401) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    window.location.href = '/login.html'
  }
  return response.data
}, error => {
  return Promise.reject(error)
})

export const attendanceApi = {
  checkIn: (longitude, latitude) => {
    return instance.post('/attendance/check-in', {
      longitude,
      latitude
    })
  },

  checkOut: (longitude, latitude) => {
    return instance.post('/attendance/check-out', {
      longitude,
      latitude
    })
  },

  getTodayRecord: () => {
    return instance.get('/attendance/today')
  },

  getRecordByDate: (recordDate) => {
    return instance.get(`/attendance/date/${recordDate}`)
  },

  getRecordsByDateRange: (startDate, endDate) => {
    return instance.get('/attendance/range', {
      params: { startDate, endDate }
    })
  },

  getRecordsPage: (params) => {
    return instance.get('/attendance/page', { params })
  },

  getRecordById: (id) => {
    return instance.get(`/attendance/${id}`)
  },

  updateRecord: (record) => {
    return instance.put('/attendance/update', record)
  },

  deleteRecord: (id) => {
    return instance.delete(`/attendance/${id}`)
  }
}

export const userApi = {
  login: (username, password) => {
    return instance.post('/login', { username, password })
  },

  register: (username, password, realName, phone) => {
    return instance.post('/register', { username, password, realName, phone })
  }
}

export default instance