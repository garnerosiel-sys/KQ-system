const { createApp, ref, reactive, computed, onMounted } = Vue
createApp({
  setup() {
    const currentModule = ref('checkin')
    const userInfo = reactive(JSON.parse(localStorage.getItem('user') || '{}'))
    const token = ref(localStorage.getItem('token'))

    const modules = { dashboard: '数据看板', checkin: '打卡记录', department: '部门管理', rule: '考勤规则', company: '公司信息', leave: '请假申请', makeup: '补卡申请', overtime: '加班申请', workstation: '工作台日志', user: '用户列表', workstationMonitor: '工作台监控' }
    const moduleTitle = computed(() => modules[currentModule.value] || '考勤管理系统')

    const axiosInstance = axios.create({ baseURL: '/api', timeout: 10000, headers: { 'Content-Type': 'application/json' } })
    axiosInstance.interceptors.request.use(config => { const t = localStorage.getItem('token'); if (t) config.headers['Authorization'] = 'Bearer ' + t; return config })
    axiosInstance.interceptors.response.use(r => r, e => { if (e.response?.status === 401) { localStorage.removeItem('token'); localStorage.removeItem('user'); alert('登录已失效，请重新登录'); window.location.href = 'login.html' } return Promise.reject(e) })

    const location = ref(null)
    const locationLoading = ref(true)
    const currentAddress = ref('定位中...')
    const morningStatus = ref('未打卡')
    const eveningStatus = ref('未打卡')
    const checkinTime = ref(null)
    const checkoutTime = ref(null)
    const recentRecords = ref([])
    const todayStatus = ref('未打卡')

    // 工作台监控状态
    const wsActivities = ref([])
    const wsStats = reactive({ total: 0, checkedIn: 0, late: 0, absent: 0 })
    const proxyForm = reactive({ userId: null, type: 1 })
    const proxyUsers = ref([])
    const proxyLoading = ref(false)
    const wsCountdown = ref(10)
    let wsTimer = null

    const statusClass = computed(() => {
        if (todayStatus.value === '正常') return 'success'
        if (todayStatus.value === '迟到') return 'warning'
        return 'pending'
    })

    const departments = ref([])
    const showDepartmentModal = ref(false)
    const editingDepartment = ref(null)
    const departmentForm = reactive({ id: null, name: '' })

    const rules = ref([])
    const showRuleModal = ref(false)
    const editingRule = ref(null)
    const ruleForm = reactive({ id: null, ruleName: '', workStartTime: '', workEndTime: '', centerLongitude: '114.376899', centerLatitude: '30.500573', allowedRadius: 1000 })

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

    // 公司信息
    const company = ref(null)
    const showCompanyModal = ref(false)
    const companyForm = reactive({ name: '', centerLatitude: '', centerLongitude: '', radius: 500, address: '' })

    // 统计
    const stats = reactive({ totalUsers: 0, todayCheckins: 0, lateCount: 0, pendingApprovals: 0 })

    // 通知
    const notifications = ref([])

    const formatDate = (date) => { if (!date) return '-'; const d = Array.isArray(date) ? new Date(date[0], date[1]-1, date[2]) : new Date(date); if (isNaN(d.getTime())) return '-'; return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}` }
    const formatDateTime = (date) => { if (!date) return '-'; const d = Array.isArray(date) ? new Date(date[0], date[1]-1, date[2], date[3]||0, date[4]||0, date[5]||0) : new Date(date); if (isNaN(d.getTime())) return '-'; return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}` }
    const getStatusClass = (status) => ({ '待审批': 'pending', '已通过': 'approved', '已驳回': 'rejected' }[status] || status)
    const getLeaveTypeName = (type) => ({ '病假': '病假', '事假': '事假', '年假': '年假', '产假': '产假', '陪产假': '陪产假' }[type] || type)
    const getRoleName = (role) => ({ 'admin': '管理员', 'user': '普通用户', 'workstation': '工作台' }[role] || role)

    const getDepartmentName = (id) => { const dept = departments.value.find(d => d.id === id); return dept ? dept.name : '-' }
    const getUserRealName = (userId) => { const user = users.value.find(u => u.id === userId); return user ? user.realName : '-' }

    const loadCheckinData = async () => {
    try {
        // 同时获取两个API的数据
        const [todayRes, recordsRes] = await Promise.all([
            axiosInstance.get('/attendance/today'),
            axiosInstance.get('/attendance-record/today')
        ]);

        if (todayRes.data.code === 200) {
            const record = todayRes.data.data || {};
            morningStatus.value = record.checkInTime ? '已打卡' : '未打卡';
            eveningStatus.value = record.checkOutTime ? '已打卡' : '未打卡';
            checkinTime.value = record.checkInTime || null;
            checkoutTime.value = record.checkOutTime || null;
            todayStatus.value = record.status || '未打卡';
        }

        // 从attendance-record API获取详细的打卡记录
        if (recordsRes.data.code === 200) {
            const recordData = recordsRes.data.data;
            recentRecords.value = [];

            if (recordData) {
                if (recordData.checkInTime) {
                    recentRecords.value.push({ id: recordData.id, checkinTime: recordData.checkInTime, type: 'morning', status: recordData.status || '正常' });
                }
                if (recordData.checkOutTime) {
                    recentRecords.value.push({ id: recordData.id, checkinTime: recordData.checkOutTime, type: 'evening', status: recordData.status || '正常' });
                }
            }
        }

        // 兜底：如果 attendance_record 表没数据，用 attendance 表的时间填充 recentRecords
        if (recentRecords.value.length === 0 && (checkinTime.value || checkoutTime.value)) {
            if (checkinTime.value) {
                recentRecords.value.push({ id: 0, checkinTime: checkinTime.value, type: 'morning', status: '正常' });
            }
            if (checkoutTime.value) {
                recentRecords.value.push({ id: 0, checkinTime: checkoutTime.value, type: 'evening', status: '正常' });
            }
        }
    } catch (e) {
        console.error(e);
        alert('加载打卡数据失败: ' + (e.response?.data?.message || e.message))
    }
}

    const loadDepartments = async () => { try { const res = await axiosInstance.get('/department/list'); if (res.data.code === 200) departments.value = res.data.data } catch (e) { console.error(e); alert('加载部门数据失败: ' + (e.response?.data?.message || e.message)) } }
    const addDepartment = () => { editingDepartment.value = null; Object.assign(departmentForm, { id: null, name: '' }); showDepartmentModal.value = true }
    const editDepartment = (dept) => { editingDepartment.value = dept; Object.assign(departmentForm, { id: dept.id, name: dept.name }); showDepartmentModal.value = true }
    const saveDepartment = async () => { try { if (editingDepartment.value) await axiosInstance.put('/department/update', departmentForm); else await axiosInstance.post('/department/add', departmentForm); showDepartmentModal.value = false; loadDepartments() } catch (e) { alert(e.response?.data?.message || '保存失败') } }
    const deleteDepartment = async (id) => { if (confirm('确定删除该部门？')) { try { await axiosInstance.delete(`/department/${id}`); loadDepartments() } catch (e) { alert(e.response?.data?.message || '删除失败') } } }

    const loadRules = async () => { try { const res = await axiosInstance.get('/rule/list'); if (res.data.code === 200) { const list = res.data.data || []; for (const r of list) { if (r.centerLongitude && r.centerLatitude) { r._addr = await reverseGeocode(Number(r.centerLongitude), Number(r.centerLatitude)); } else { r._addr = '-'; } } rules.value = list; } } catch (e) { console.error(e); } }
    const addRule = () => { editingRule.value = null; Object.assign(ruleForm, { id: null, ruleName: '', workStartTime: '', workEndTime: '', centerLongitude: '114.376899', centerLatitude: '30.500573', allowedRadius: 1000 }); showRuleModal.value = true }
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
    const addWorkstationLog = async () => { if (!workstationForm.workstationId) { alert('请选择工作台'); return } if (!workstationForm.actionDetail) { alert('请填写日志详情'); return } try { await axiosInstance.post('/workstation-log/add', workstationForm); showWorkstationModal.value = false; loadWorkstationLogs(); Object.assign(workstationForm, { workstationId: null, actionDetail: '' }) } catch (e) { alert(e.response?.data?.message || '添加失败') } }
    const deleteWorkstationLog = async (id) => { if (confirm('确定删除该日志？')) { try { await axiosInstance.delete(`/workstation-log/${id}`); loadWorkstationLogs() } catch (e) { alert(e.response?.data?.message || '删除失败') } } }

    const loadUsers = async () => { try { const res = await axiosInstance.get('/user/list'); if (res.data.code === 200) { users.value = res.data.data || []; workstationUsers.value = users.value.filter(u => u.role === 'workstation') } } catch (e) { console.error(e); alert('加载用户数据失败: ' + (e.response?.data?.message || e.message)) } }
    const addUser = () => { editingUser.value = null; Object.assign(userForm, { id: null, username: '', password: '', realName: '', departmentId: null, role: 'user', phone: '' }); showUserModal.value = true }
    const editUser = (user) => { editingUser.value = user; Object.assign(userForm, { id: user.id, username: user.username, password: '', realName: user.realName, departmentId: user.departmentId, role: user.role, phone: user.phone }); showUserModal.value = true }
    const saveUser = async () => { try { if (editingUser.value) await axiosInstance.put('/user/update', userForm); else await axiosInstance.post('/user/add', userForm); showUserModal.value = false; loadUsers() } catch (e) { alert(e.response?.data?.message || '保存失败') } }
    const deleteUser = async (id) => { if (confirm('确定删除该用户？')) { try { await axiosInstance.delete(`/user/${id}`); loadUsers() } catch (e) { alert(e.response?.data?.message || '删除失败') } } }

    // 公司信息
    const loadCompany = async () => { try { const res = await axiosInstance.get('/company/info'); if (res.data.code === 200 && res.data.data) { company.value = res.data.data; Object.assign(companyForm, res.data.data); if (company.value.centerLongitude && company.value.centerLatitude) { company.value._addr = await reverseGeocode(Number(company.value.centerLongitude), Number(company.value.centerLatitude)); } } } catch (e) { console.error(e); } }
    const saveCompany = async () => { try { await axiosInstance.put('/company/update', companyForm); showCompanyModal.value = false; loadCompany(); alert('公司信息已更新'); } catch (e) { alert(e.response?.data?.message || '保存失败') } }

    // 通知
    const checkNotifications = async () => {
        try {
            const [leaveRes, makeupRes, overtimeRes] = await Promise.all([
                axiosInstance.get(userInfo.role === 'admin' ? '/leave/list' : '/leave/my'),
                axiosInstance.get(userInfo.role === 'admin' ? '/makeup/list' : '/makeup/my'),
                axiosInstance.get(userInfo.role === 'admin' ? '/overtime/list' : '/overtime/my'),
            ]);
            const count = (arr, status) => (arr.data?.data || []).filter(r => r.status === status).length;
            const approved = count(leaveRes, '已通过') + count(makeupRes, '已通过') + count(overtimeRes, '已通过');
            const rejected = count(leaveRes, '已驳回') + count(makeupRes, '已驳回') + count(overtimeRes, '已驳回');
            notifications.value = [];
            if (approved > 0) notifications.value.push({ type: 'success', msg: `${approved} 条申请已通过` });
            if (rejected > 0) notifications.value.push({ type: 'danger', msg: `${rejected} 条申请已驳回` });
            stats.pendingApprovals = count(leaveRes, '待审批') + count(makeupRes, '待审批') + count(overtimeRes, '待审批');
        } catch (e) { /* silent */ }
    };

    // 统计加载
    const loadStats = async () => {
        try {
            const [usersRes, attendanceRes] = await Promise.all([
                axiosInstance.get('/user/list'),
                axiosInstance.get('/attendance/all?page=1&pageSize=1000'),
            ]);
            if (usersRes.data.code === 200) stats.totalUsers = (usersRes.data.data || []).filter(u => u.role === 'user').length;
            if (attendanceRes.data.code === 200) {
                const list = attendanceRes.data.data.list || [];
                const today = new Date().toISOString().slice(0, 10);
                const todayList = list.filter(r => r.createTime && r.createTime.startsWith(today));
                stats.todayCheckins = todayList.filter(r => r.status === 1).length;
                stats.lateCount = todayList.filter(r => r.status === 0).length;
            }
        } catch (e) { /* silent */ }
    };

    const handleCheckin = async (type, event) => {
    if (!location.value) {
        alert('无法获取位置信息，请确保已开启定位权限');
        return
    }

    const btn = event.target;
    const originalText = btn.textContent;

    try {
        // 显示加载中状态
        btn.textContent = '打卡中...';
        btn.disabled = true;

        const url = type === 'morning' ? '/attendance/check-in' : '/attendance/check-out';
        await axiosInstance.post(url, {
            latitude: location.value.latitude,
            longitude: location.value.longitude,
            address: currentAddress.value
        });

        // 先刷新数据，等Vue更新DOM后再弹窗
        await loadCheckinData();
        await new Promise(resolve => setTimeout(resolve, 100));
        alert(type === 'morning' ? '上班打卡成功！' : '下班打卡成功！');
    } catch (e) {
        const errorMsg = e.response?.data?.message || '打卡失败';
        alert(errorMsg);

        // 如果是距离问题，显示具体信息
        if (errorMsg.includes('超出允许范围')) {
            const match = errorMsg.match(/约 ([\d.]+) 米/);
            if (match) {
                alert(`您距离公司约 ${match[1]} 米，超出允许打卡范围`);
            }
        }
    } finally {
        btn.textContent = originalText;
        btn.disabled = false;
    }
}

    const handleLogout = () => { localStorage.removeItem('token'); localStorage.removeItem('user'); window.location.href = 'login.html' }

    const switchModule = (module) => { stopWsTimer(); currentModule.value = module; if (module === 'checkin') { loadCheckinData(); getLocation() } else if (module === 'dashboard') { loadStats(); loadCompany() } else if (module === 'department') { loadDepartments(); loadCompany(); loadStats() } else if (module === 'rule') loadRules(); else if (module === 'company') loadCompany(); else if (module === 'leave') loadLeaveRequests(); else if (module === 'makeup') loadMakeupRequests(); else if (module === 'overtime') loadOvertimeRequests(); else if (module === 'workstation') { loadWorkstationLogs(); loadUsers() } else if (module === 'user') { loadUsers(); loadDepartments() } else if (module === 'workstationMonitor') { getLocation(); loadProxyUsers(); loadWsActivities(); startWsTimer(); } }

    const reverseGeocode = async (lng, lat) => {
        // 等待 SDK 加载（最多等 5 秒）
        let waited = 0;
        while ((typeof AMap === 'undefined' || !AMap.Geocoder) && waited < 50) {
            await new Promise(r => setTimeout(r, 100));
            waited++;
        }
        if (typeof AMap === 'undefined' || !AMap.Geocoder) {
            console.log('Amap SDK 加载超时，显示坐标');
            return `${lat.toFixed(6)}, ${lng.toFixed(6)}`;
        }
        return new Promise((resolve) => {
            new AMap.Geocoder().getAddress([lng, lat], (status, result) => {
                console.log('Amap 逆地理:', status, result?.regeocode?.formattedAddress);
                if (status === 'complete' && result.regeocode) {
                    resolve(result.regeocode.formattedAddress);
                } else {
                    resolve(`${lat.toFixed(6)}, ${lng.toFixed(6)}`);
                }
            });
        });
    };

    const getLocation = () => {
        locationLoading.value = true;
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(async pos => {
                const lat = pos.coords.latitude;
                const lng = pos.coords.longitude;
                location.value = { latitude: lat, longitude: lng };
                currentAddress.value = await reverseGeocode(lng, lat);
                locationLoading.value = false;
            }, err => {
                locationLoading.value = false;
                currentAddress.value = '无法获取位置';
                console.error(err);
            });
        } else {
            locationLoading.value = false;
            currentAddress.value = '浏览器不支持定位';
        }
    };

    // ========== 工作台监控方法 ==========
    const loadWsActivities = async () => {
        try {
            const res = await axiosInstance.get('/workstation-log/today');
            if (res.data.code === 200) {
                wsActivities.value = res.data.data || [];
                // 计算统计
                const allUsers = users.value.filter(u => u.role === 'user');
                wsStats.total = allUsers.length;
                wsStats.checkedIn = 0;
                wsStats.late = 0;
                const todayUserIds = new Set();
                for (const act of wsActivities.value) {
                    if (act.actionDetail && act.actionDetail.includes('上班打卡')) {
                        todayUserIds.add(act.workstationId);
                        if (act.actionDetail.includes('正常')) wsStats.checkedIn++;
                        else wsStats.late++;
                    }
                }
                wsStats.absent = Math.max(0, wsStats.total - todayUserIds.size);
            }
        } catch (e) {
            console.error('加载活动流失败', e);
        }
    };

    const loadProxyUsers = async () => {
        try {
            const res = await axiosInstance.get('/user/list');
            if (res.data.code === 200) {
                proxyUsers.value = (res.data.data || []).filter(u => u.role === 'user');
                users.value = res.data.data || [];
            }
        } catch (e) {
            console.error('加载用户列表失败', e);
        }
    };

    const handleProxyPunch = async () => {
        if (!proxyForm.userId) { alert('请选择员工'); return }
        if (!location.value) { alert('无法获取位置信息'); return }
        proxyLoading.value = true;
        try {
            await axiosInstance.post('/attendance/punch-for', {
                userId: proxyForm.userId,
                type: proxyForm.type,
                latitude: location.value.latitude,
                longitude: location.value.longitude,
                address: currentAddress.value,
                operatorName: userInfo.realName || userInfo.username
            });
            alert('代打卡成功！');
            loadWsActivities();
        } catch (e) {
            alert(e.response?.data?.message || '代打卡失败');
        } finally {
            proxyLoading.value = false;
        }
    };

    const startWsTimer = () => {
        if (wsTimer) clearInterval(wsTimer);
        wsCountdown.value = 10;
        wsTimer = setInterval(() => {
            wsCountdown.value--;
            if (wsCountdown.value <= 0) {
                wsCountdown.value = 10;
                loadWsActivities();
            }
        }, 1000);
    };

    const stopWsTimer = () => {
        if (wsTimer) { clearInterval(wsTimer); wsTimer = null; }
    };

    onMounted(() => {
        if (!localStorage.getItem('token')) { window.location.href = 'login.html'; return }
        const hash = window.location.hash.replace('#', '');
        if (hash === 'workstation' && userInfo.role === 'workstation') {
            switchModule('workstationMonitor');
        } else if (userInfo.role === 'workstation') {
            switchModule('workstationMonitor');
        } else {
            switchModule('checkin');
            if (userInfo.role === 'admin') { loadStats(); loadCompany(); }
            checkNotifications();
        }
    })

    return {
        currentModule,
        moduleTitle,
        userInfo,
        switchModule,
        location,
        locationLoading,
        currentAddress,
        morningStatus,
        eveningStatus,
        checkinTime,
        checkoutTime,
        todayStatus,
        statusClass,
        recentRecords,
        handleCheckin,
        handleLogout,
        departments,
        showDepartmentModal,
        editingDepartment,
        departmentForm,
        addDepartment,
        editDepartment,
        saveDepartment,
        deleteDepartment,
        rules,
        showRuleModal,
        editingRule,
        ruleForm,
        addRule,
        editRule,
        saveRule,
        deleteRule,
        leaveRequests,
        showLeaveModal,
        leaveForm,
        submitLeave,
        approveLeave,
        makeupRequests,
        showMakeupModal,
        makeupForm,
        submitMakeup,
        approveMakeup,
        overtimeRequests,
        showOvertimeModal,
        overtimeForm,
        submitOvertime,
        approveOvertime,
        workstationLogs,
        workstationUsers,
        showWorkstationModal,
        workstationForm,
        addWorkstationLog,
        deleteWorkstationLog,
        users,
        showUserModal,
        editingUser,
        userForm,
        addUser,
        editUser,
        saveUser,
        deleteUser,
        formatDate,
        formatDateTime,
        getStatusClass,
        getLeaveTypeName,
        getRoleName,
        getDepartmentName,
        getUserRealName,
        // 工作台监控
        wsActivities,
        wsStats,
        proxyForm,
        proxyUsers,
        proxyLoading,
        wsCountdown,
        loadWsActivities,
        handleProxyPunch,
        // 公司 + 统计 + 通知
        company,
        companyForm,
        showCompanyModal,
        loadCompany,
        saveCompany,
        stats,
        notifications
    }
  }
}).mount('#app')