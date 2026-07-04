const { createApp, ref, reactive, computed, onMounted } = Vue
createApp({
  setup() {
    const currentModule = ref('checkin')
    const userInfo = reactive(JSON.parse(localStorage.getItem('user') || '{}'))
    const token = ref(localStorage.getItem('token'))

    const modules = { checkin: '打卡记录', department: '部门管理', rule: '考勤规则', leave: '请假申请', makeup: '补卡申请', overtime: '加班申请', workstation: '工作台日志', user: '用户列表' }
    const moduleTitle = computed(() => modules[currentModule.value] || '考勤管理系统')

    const axiosInstance = axios.create({ baseURL: '/api', timeout: 10000, headers: { 'Content-Type': 'application/json' } })
    axiosInstance.interceptors.request.use(config => { const t = localStorage.getItem('token'); if (t) config.headers['Authorization'] = 'Bearer ' + t; return config })
    axiosInstance.interceptors.response.use(r => r, e => { if (e.response?.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('user'); alert('登录已失效，请重新登录'); window.location.href = 'login.html' } return Promise.reject(e) })

    const location = ref(null)
    const locationLoading = ref(true)
    const morningStatus = ref('未打卡')
    const eveningStatus = ref('未打卡')
    const recentRecords = ref([])

    const departments = ref([])
    const showDepartmentModal = ref(false)
    const editingDepartment = ref(null)
    const departmentForm = reactive({ id: null, name: '' })

    const rules = ref([])
    const showRuleModal = ref(false)
    const editingRule = ref(null)
    const ruleForm = reactive({ id: null, ruleName: '', workStartTime: '', workEndTime: '', centerLongitude: 116.4074, centerLatitude: 39.9042, allowedRadius: 100 })

    const leaveRequests = ref([])
    const showLeaveModal = ref(false)
    const leaveForm = reactive({ leaveType: '病假', startTime: '', endTime: '', reason: '' })

    const makeupRequests = ref([])
    const showMakeupModal = ref(false)
    const makeupForm = reactive({ recordDate: '', makeupTime: '', makeupType: '上班补卡', reason: '' })

    const overtimeRequests = ref([])
    const showOvertimeModal = ref(false)
    const overtimeForm = reactive({ startTime: '', endTime: '', reason: '' })

    const workstationLogs = ref([])
    const workstationUsers = ref([])
    const showWorkstationModal = ref(false)
    const workstationForm = reactive({ workstationId: null, actionDetail: '' })

    const users = ref([])
    const showUserModal = ref(false)
    const editingUser = ref(null)
    const userForm = reactive({ id: null, username: '', password: '', realName: '', departmentId: null, role: 'user', phone: '' })

    const formatDate = (date) => { if (!date) return '-'; const d = new Date(date); return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}` }
    const formatDateTime = (date) => { if (!date) return '-'; const d = new Date(date); return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}` }
    const getStatusClass = (status) => ({ '待审批': 'pending', '已通过': 'approved', '已驳回': 'rejected' }[status] || status)
    const getLeaveTypeName = (type) => ({ '病假': '病假', '事假': '事假', '年假': '年假', '产假': '产假', '陪产假': '陪产假' }[type] || type)
    const getRoleName = (role) => ({ 'admin': '管理员', 'user': '普通用户', 'workstation': '工作台' }[role] || role)

    const getDepartmentName = (id) => { const dept = departments.value.find(d => d.id === id); return dept ? dept.name : '-' }
    const getUserRealName = (userId) => { const user = users.value.find(u => u.id === userId); return user ? user.realName : '-' }

    const loadCheckinData = async () => { try { const res = await axiosInstance.get('/attendance/today'); if (res.data.code === 200) { const record = res.data.data || {}; morningStatus.value = record.checkInTime ? '已打卡' : '未打卡'; eveningStatus.value = record.checkOutTime ? '已打卡' : '未打卡'; recentRecords.value = []; if (record.checkInTime) recentRecords.value.push({ id: record.id, checkinTime: record.checkInTime, type: 'morning', status: record.status }); if (record.checkOutTime) recentRecords.value.push({ id: record.id, checkinTime: record.checkOutTime, type: 'evening', status: record.status }) } } catch (e) { console.error(e); alert('加载打卡数据失败: ' + (e.response?.data?.message || e.message)) } }

    const loadDepartments = async () => { try { const res = await axiosInstance.get('/department/list'); if (res.data.code === 200) departments.value = res.data.data } catch (e) { console.error(e); alert('加载部门数据失败: ' + (e.response?.data?.message || e.message)) } }
    const addDepartment = () => { editingDepartment.value = null; Object.assign(departmentForm, { id: null, name: '' }); showDepartmentModal.value = true }
    const editDepartment = (dept) => { editingDepartment.value = dept; Object.assign(departmentForm, { id: dept.id, name: dept.name }); showDepartmentModal.value = true }
    const saveDepartment = async () => { try { if (editingDepartment.value) await axiosInstance.put('/department/update', departmentForm); else await axiosInstance.post('/department/add', departmentForm); showDepartmentModal.value = false; loadDepartments() } catch (e) { alert(e.response?.data?.message || '保存失败') } }
    const deleteDepartment = async (id) => { if (confirm('确定删除该部门？')) { try { await axiosInstance.delete(`/department/${id}`); loadDepartments() } catch (e) { alert(e.response?.data?.message || '删除失败') } } }

    const loadRules = async () => { try { const res = await axiosInstance.get('/rule/list'); if (res.data.code === 200) rules.value = res.data.data || [] } catch (e) { console.error(e); alert('加载考勤规则失败: ' + (e.response?.data?.message || e.message)) } }
    const addRule = () => { editingRule.value = null; Object.assign(ruleForm, { id: null, ruleName: '', workStartTime: '', workEndTime: '', centerLongitude: 116.4074, centerLatitude: 39.9042, allowedRadius: 100 }); showRuleModal.value = true }
    const editRule = (rule) => { editingRule.value = rule; Object.assign(ruleForm, { id: rule.id, ruleName: rule.ruleName, workStartTime: rule.workStartTime, workEndTime: rule.workEndTime, centerLongitude: rule.centerLongitude?.toString() || '', centerLatitude: rule.centerLatitude?.toString() || '', allowedRadius: rule.allowedRadius }); showRuleModal.value = true }
    const saveRule = async () => { try { if (editingRule.value) await axiosInstance.put('/rule/update', ruleForm); else await axiosInstance.post('/rule/add', ruleForm); showRuleModal.value = false; loadRules() } catch (e) { alert(e.response?.data?.message || '保存失败') } }
    const deleteRule = async (id) => { if (confirm('确定删除该规则？')) { try { await axiosInstance.delete(`/rule/${id}`); loadRules() } catch (e) { alert(e.response?.data?.message || '删除失败') } } }

    const loadLeaveRequests = async () => { try { const url = userInfo.role === 'admin' ? '/leave/list' : '/leave/my'; const res = await axiosInstance.get(url); if (res.data.code === 200) leaveRequests.value = res.data.data || [] } catch (e) { console.error(e); alert('加载请假数据失败: ' + (e.response?.data?.message || e.message)) } }
    const submitLeave = async () => { try { await axiosInstance.post('/leave/submit', leaveForm); showLeaveModal.value = false; loadLeaveRequests(); Object.assign(leaveForm, { leaveType: '病假', startTime: '', endTime: '', reason: '' }) } catch (e) { alert(e.response?.data?.message || '提交失败') } }
    const approveLeave = async (id, status) => { try { await axiosInstance.put('/leave/approve', { id, status }); loadLeaveRequests() } catch (e) { alert(e.response?.data?.message || '操作失败') } }

    const loadMakeupRequests = async () => { try { const url = userInfo.role === 'admin' ? '/makeup/list' : '/makeup/my'; const res = await axiosInstance.get(url); if (res.data.code === 200) makeupRequests.value = res.data.data || [] } catch (e) { console.error(e); alert('加载补卡数据失败: ' + (e.response?.data?.message || e.message)) } }
    const submitMakeup = async () => { try { await axiosInstance.post('/makeup/submit', makeupForm); showMakeupModal.value = false; loadMakeupRequests(); Object.assign(makeupForm, { recordDate: '', makeupTime: '', makeupType: '上班补卡', reason: '' }) } catch (e) { alert(e.response?.data?.message || '提交失败') } }
    const approveMakeup = async (id, status) => { try { await axiosInstance.put('/makeup/approve', { id, status }); loadMakeupRequests() } catch (e) { alert(e.response?.data?.message || '操作失败') } }

    const loadOvertimeRequests = async () => { try { const url = userInfo.role === 'admin' ? '/overtime/list' : '/overtime/my'; const res = await axiosInstance.get(url); if (res.data.code === 200) overtimeRequests.value = res.data.data || [] } catch (e) { console.error(e); alert('加载加班数据失败: ' + (e.response?.data?.message || e.message)) } }
    const submitOvertime = async () => { try { await axiosInstance.post('/overtime/submit', overtimeForm); showOvertimeModal.value = false; loadOvertimeRequests(); Object.assign(overtimeForm, { startTime: '', endTime: '', reason: '' }) } catch (e) { alert(e.response?.data?.message || '提交失败') } }
    const approveOvertime = async (id, status) => { try { await axiosInstance.put('/overtime/approve', { id, status }); loadOvertimeRequests() } catch (e) { alert(e.response?.data?.message || '操作失败') } }

    const loadWorkstationLogs = async () => { try { const res = await axiosInstance.get('/workstation-log/list'); if (res.data.code === 200) workstationLogs.value = res.data.data || [] } catch (e) { console.error(e); alert('加载工作台日志失败: ' + (e.response?.data?.message || e.message)) } }
    const addWorkstationLog = async () => { try { await axiosInstance.post('/workstation-log/add', workstationForm); showWorkstationModal.value = false; loadWorkstationLogs(); Object.assign(workstationForm, { workstationId: null, actionDetail: '' }) } catch (e) { alert(e.response?.data?.message || '添加失败') } }
    const deleteWorkstationLog = async (id) => { if (confirm('确定删除该日志？')) { try { await axiosInstance.delete(`/workstation-log/${id}`); loadWorkstationLogs() } catch (e) { alert(e.response?.data?.message || '删除失败') } } }

    const loadUsers = async () => { try { const res = await axiosInstance.get('/user/list'); if (res.data.code === 200) { users.value = res.data.data || []; workstationUsers.value = users.value.filter(u => u.role === 'workstation') } } catch (e) { console.error(e); alert('加载用户数据失败: ' + (e.response?.data?.message || e.message)) } }
    const addUser = () => { editingUser.value = null; Object.assign(userForm, { id: null, username: '', password: '', realName: '', departmentId: null, role: 'user', phone: '' }); showUserModal.value = true }
    const editUser = (user) => { editingUser.value = user; Object.assign(userForm, { id: user.id, username: user.username, password: '', realName: user.realName, departmentId: user.departmentId, role: user.role, phone: user.phone }); showUserModal.value = true }
    const saveUser = async () => { try { if (editingUser.value) await axiosInstance.put('/user/update', userForm); else await axiosInstance.post('/user/add', userForm); showUserModal.value = false; loadUsers() } catch (e) { alert(e.response?.data?.message || '保存失败') } }
    const deleteUser = async (id) => { if (confirm('确定删除该用户？')) { try { await axiosInstance.delete(`/user/${id}`); loadUsers() } catch (e) { alert(e.response?.data?.message || '删除失败') } } }

    const handleCheckin = async (type) => { if (!location.value) { alert('无法获取位置信息'); return } try { const url = type === 'morning' ? '/attendance/check-in' : '/attendance/check-out'; await axiosInstance.post(url, { latitude: location.value.latitude, longitude: location.value.longitude }); loadCheckinData(); alert(type === 'morning' ? '上班打卡成功' : '下班打卡成功') } catch (e) { alert(e.response?.data?.message || '打卡失败') } }

    const handleLogout = () => { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = 'login.html' }

    const switchModule = (module) => { currentModule.value = module; if (module === 'checkin') { loadCheckinData(); getLocation() } else if (module === 'department') loadDepartments(); else if (module === 'rule') loadRules(); else if (module === 'leave') loadLeaveRequests(); else if (module === 'makeup') loadMakeupRequests(); else if (module === 'overtime') loadOvertimeRequests(); else if (module === 'workstation') { loadWorkstationLogs(); loadUsers() } else if (module === 'user') { loadUsers(); loadDepartments() } }

    const getLocation = () => { locationLoading.value = true; if (navigator.geolocation) { navigator.geolocation.getCurrentPosition(pos => { location.value = { latitude: pos.coords.latitude, longitude: pos.coords.longitude }; locationLoading.value = false }, err => { locationLoading.value = false; console.error(err) }) } else { locationLoading.value = false } }

    onMounted(() => { if (!localStorage.getItem('token')) { window.location.href = 'login.html'; return } switchModule('checkin') })

    return { currentModule, moduleTitle, userInfo, switchModule, location, locationLoading, morningStatus, eveningStatus, recentRecords, handleCheckin, handleLogout, departments, showDepartmentModal, editingDepartment, departmentForm, addDepartment, editDepartment, saveDepartment, deleteDepartment, rules, showRuleModal, editingRule, ruleForm, addRule, editRule, saveRule, deleteRule, leaveRequests, showLeaveModal, leaveForm, submitLeave, approveLeave, makeupRequests, showMakeupModal, makeupForm, submitMakeup, approveMakeup, overtimeRequests, showOvertimeModal, overtimeForm, submitOvertime, approveOvertime, workstationLogs, workstationUsers, showWorkstationModal, workstationForm, addWorkstationLog, deleteWorkstationLog, users, showUserModal, editingUser, userForm, addUser, editUser, saveUser, deleteUser, formatDate, formatDateTime, getStatusClass, getLeaveTypeName, getRoleName, getDepartmentName, getUserRealName }
  }
}).mount('#app')