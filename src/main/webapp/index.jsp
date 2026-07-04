<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
  <meta http-equiv="Pragma" content="no-cache">
  <meta http-equiv="Expires" content="0">
  <title>考勤管理系统</title>
  <link rel="stylesheet" href="css/app.css">
  <script src="https://cdn.jsdelivr.net/npm/vue@3.5.39/dist/vue.global.prod.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/axios@1.6.8/dist/axios.min.js"></script>
  <script>
    window._AMapSecurityConfig = { securityJsCode: 'f4be72b265fb3fff9cdbba427cbeb7ad' };
  </script>
  <script src="https://webapi.amap.com/maps?v=2.0&key=b8e5d2093f679d52a9107addbfe38048&plugin=AMap.Geocoder"></script>
</head>
<body>
  <div id="app" class="container">
    <div class="sidebar">
      <h2>考勤管理系统</h2>
      <nav class="sidebar-nav">
        <ul>
          <template v-if="userInfo.role === 'workstation'">
            <li><a :class="{active: currentModule === 'workstationMonitor'}" @click="switchModule('workstationMonitor')">工作台监控</a></li>
          </template>
          <template v-else>
            <li><a :class="{active: currentModule === 'checkin'}" @click="switchModule('checkin')">打卡记录</a></li>
            <li v-if="userInfo.role === 'admin'"><a :class="{active: currentModule === 'dashboard'}" @click="switchModule('dashboard')">数据看板</a></li>
            <li v-if="userInfo.role === 'admin'"><a :class="{active: currentModule === 'department'}" @click="switchModule('department')">部门管理</a></li>
            <li v-if="userInfo.role === 'admin'"><a :class="{active: currentModule === 'rule'}" @click="switchModule('rule')">考勤规则</a></li>
            <li v-if="userInfo.role === 'admin'"><a :class="{active: currentModule === 'company'}" @click="switchModule('company')">公司信息</a></li>
            <li v-if="userInfo.role === 'admin' || userInfo.role === 'user'"><a :class="{active: currentModule === 'leave'}" @click="switchModule('leave')">请假申请</a></li>
            <li v-if="userInfo.role === 'admin' || userInfo.role === 'user'"><a :class="{active: currentModule === 'makeup'}" @click="switchModule('makeup')">补卡申请</a></li>
            <li v-if="userInfo.role === 'admin' || userInfo.role === 'user'"><a :class="{active: currentModule === 'overtime'}" @click="switchModule('overtime')">加班申请</a></li>
            <li v-if="userInfo.role === 'admin'"><a :class="{active: currentModule === 'workstation'}" @click="switchModule('workstation')">工作台日志</a></li>
            <li v-if="userInfo.role === 'admin'"><a :class="{active: currentModule === 'user'}" @click="switchModule('user')">用户列表</a></li>
          </template>
        </ul>
      </nav>
    </div>
    <div class="main-content">
      <div class="header">
        <h1>{{ moduleTitle }}</h1>
        <div class="user-info">
          <span v-for="n in notifications" :key="n.msg" :class="'badge badge-' + n.type" style="margin-right:8px">{{ n.msg }}</span>
          <span>欢迎, {{ userInfo.realName || userInfo.username }}</span>
          <button class="logout-btn" @click="handleLogout">退出登录</button>
        </div>
      </div>
      <!-- 管理员统计条 -->
      <div v-if="userInfo.role === 'admin' && currentModule !== 'checkin'" class="stats-bar">
        <span>👥 {{ stats.totalUsers }} 员工</span>
        <span>✅ 今日打卡 {{ stats.todayCheckins }}</span>
        <span>⚠️ 异常 {{ stats.lateCount }}</span>
        <span>📋 待审批 {{ stats.pendingApprovals }}</span>
      </div>
      <div class="content-area">
        <div v-if="currentModule === 'checkin'">
          <!-- 顶部欢迎信息 -->
          <div class="welcome-card">
            <div class="welcome-header">
              <div class="welcome-greeting">早上好！{{ userInfo.realName || userInfo.username }}</div>
              <div class="welcome-date">{{ new Date().toLocaleDateString('zh-CN', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }) }}</div>
            </div>
            <div class="welcome-status">
              <div class="status-item">
                <div class="status-label">今日状态</div>
                <div class="status-badge" :class="statusClass">{{ todayStatus }}</div>
              </div>
              <div class="status-item" v-if="morningStatus === '已打卡'">
                <div class="status-label">上班时间</div>
                <div class="status-time">{{ formatDateTime(checkinTime) }}</div>
              </div>
              <div class="status-item" v-if="eveningStatus === '已打卡'">
                <div class="status-label">下班时间</div>
                <div class="status-time">{{ formatDateTime(checkoutTime) }}</div>
              </div>
            </div>
          </div>

          <!-- 打卡卡片 -->
          <div class="layout-grid">
            <div class="checkin-card" :class="{ 'checked': morningStatus === '已打卡' }">
              <div class="checkin-icon">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M12 2L2 7L12 12L22 7L12 2Z"></path>
                  <path d="M2 17L12 22L22 17"></path>
                  <path d="M2 12L12 17L22 12"></path>
                </svg>
              </div>
              <div class="checkin-title">上班打卡</div>
              <div class="checkin-status" :class="{ 'status-checked': morningStatus === '已打卡', 'status-pending': morningStatus === '未打卡' }">
                {{ morningStatus }}
              </div>
              <div class="checkin-time" v-if="morningStatus === '已打卡'">
                {{ formatDateTime(checkinTime) }}
              </div>
              <button
                v-if="morningStatus === '未打卡'"
                class="btn btn-primary btn-checkin"
                @click="handleCheckin('morning', $event)"
                :disabled="locationLoading"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                上班打卡
              </button>
              <div v-else class="checkin-success">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="#4caf50">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"></path>
                </svg>
                已打卡
              </div>
            </div>

            <div class="checkin-card" :class="{ 'checked': eveningStatus === '已打卡' }">
              <div class="checkin-icon">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z"></path>
                </svg>
              </div>
              <div class="checkin-title">下班打卡</div>
              <div class="checkin-status" :class="{ 'status-checked': eveningStatus === '已打卡', 'status-pending': eveningStatus === '未打卡' }">
                {{ eveningStatus }}
              </div>
              <div class="checkin-time" v-if="eveningStatus === '已打卡'">
                {{ formatDateTime(checkoutTime) }}
              </div>
              <button
                v-if="eveningStatus === '未打卡'"
                class="btn btn-warning btn-checkin"
                @click="handleCheckin('evening', $event)"
                :disabled="locationLoading"
              >
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                下班打卡
              </button>
              <div v-else class="checkin-success">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="#4caf50">
                  <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"></path>
                </svg>
                已打卡
              </div>
            </div>
          </div>

          <!-- 位置信息 -->
          <div class="card">
            <div class="location-info" v-if="location">
              当前位置：{{ currentAddress }}
            </div>
            <div class="location-info location-warning" v-else-if="!locationLoading">
              无法获取位置信息，请检查定位权限
            </div>
            <div class="location-info" v-else>
              正在获取位置信息...
            </div>
          </div>

          <!-- 打卡记录 -->
          <div class="card">
            <div class="card-title">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 11H3v10h6V11zm4-7H7v18h6V4zm4 3h-6v14h6V7zm4 3h-6v10h6V10z"></path>
              </svg>
              今日打卡记录
            </div>
            <div class="record-list">
              <div v-for="record in recentRecords" :key="record.id" class="record-item">
                <div class="record-icon">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#1976d2" stroke-width="2">
                    <circle cx="12" cy="12" r="10"></circle>
                    <polyline points="12 6 12 12 16 14"></polyline>
                  </svg>
                </div>
                <div class="record-details">
                  <div class="record-time">{{ formatDateTime(record.checkinTime) }}</div>
                  <div class="record-type">{{ record.type === 'morning' ? '上班打卡' : '下班打卡' }}</div>
                </div>
                <div class="record-status" :class="'status-' + (record.status === '正常' ? 'success' : 'warning')">
                  {{ record.status }}
                </div>
              </div>
              <div v-if="recentRecords.length === 0" class="empty-records">
                <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#ccc" stroke-width="2">
                  <path d="M9 11H3v10h6V11zm4-7H7v18h6V4zm4 3h-6v14h6V7zm4 3h-6v10h6V10z"></path>
                </svg>
                <p>今日暂无打卡记录</p>
              </div>
            </div>
          </div>
        </div>
        <div v-else-if="currentModule === 'dashboard'" class="stats-row">
          <div class="stat-card"><div class="stat-num">{{ stats.totalUsers }}</div><div class="stat-label">员工总数</div></div>
          <div class="stat-card success"><div class="stat-num">{{ stats.todayCheckins }}</div><div class="stat-label">今日打卡</div></div>
          <div class="stat-card warning"><div class="stat-num">{{ stats.lateCount }}</div><div class="stat-label">异常打卡</div></div>
          <div class="stat-card danger"><div class="stat-num">{{ stats.pendingApprovals }}</div><div class="stat-label">待审批</div></div>
        </div>
        <div v-else-if="currentModule === 'company'" class="card">
          <div class="card-title">公司信息</div>
          <div v-if="company"><p style="margin-bottom:12px">📍 {{ company._addr || '加载中...' }}</p><p style="color:var(--text-muted);font-size:13px">经度 {{ company.centerLongitude }}　纬度 {{ company.centerLatitude }}　半径 {{ company.radius }}米</p><button class="btn btn-primary" style="margin-top:12px" @click="showCompanyModal = true">编辑</button></div>
          <p v-else>加载中...</p>
        </div>
        <div v-if="showCompanyModal" class="modal-overlay" @click.self="showCompanyModal = false">
          <div class="modal"><div class="modal-title">编辑公司信息</div>
            <div class="form-group"><label>公司名称</label><input v-model="companyForm.name"></div>
            <div class="form-group"><label>经度</label><input v-model="companyForm.centerLongitude" inputmode="decimal"></div>
            <div class="form-group"><label>纬度</label><input v-model="companyForm.centerLatitude" inputmode="decimal"></div>
            <div class="form-group"><label>打卡半径(米)</label><input v-model="companyForm.radius" inputmode="numeric"></div>
            <div class="form-group"><label>地址</label><input v-model="companyForm.address"></div>
            <div class="modal-footer"><button class="btn btn-primary" @click="saveCompany">保存</button><button class="btn" @click="showCompanyModal = false">取消</button></div>
          </div>
        </div>
        <div v-else-if="currentModule === 'department'">
          <div class="card">
            <div class="card-title">部门列表</div>
            <button class="btn btn-primary" @click="addDepartment">添加部门</button>
            <table class="table">
              <thead>
                <tr><th>ID</th><th>部门名称</th><th>创建时间</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="dept in departments" :key="dept.id">
                  <td>{{ dept.id }}</td><td>{{ dept.name }}</td><td>{{ formatDate(dept.createTime) }}</td>
                  <td>
                    <button class="btn btn-sm btn-warning" @click="editDepartment(dept)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deleteDepartment(dept.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else-if="currentModule === 'rule'">
          <div class="card">
            <div class="card-title">考勤规则</div>
            <button class="btn btn-primary" @click="addRule">添加规则</button>
            <table class="table">
              <thead>
                <tr><th>ID</th><th>规则名称</th><th>上班时间</th><th>下班时间</th><th>考勤地点</th><th>半径(米)</th><th>创建时间</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="rule in rules" :key="rule.id">
                  <td>{{ rule.id }}</td><td>{{ rule.ruleName }}</td><td>{{ rule.workStartTime }}</td><td>{{ rule.workEndTime }}</td><td style="max-width:200px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap" :title="rule._addr">{{ rule._addr || '加载中...' }}</td><td>{{ rule.allowedRadius }}</td><td>{{ formatDate(rule.createTime) }}</td>
                  <td>
                    <button class="btn btn-sm btn-warning" @click="editRule(rule)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deleteRule(rule.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else-if="currentModule === 'leave'">
          <div class="card">
            <div class="card-title">请假申请</div>
            <button class="btn btn-primary" @click="showLeaveModal = true">申请请假</button>
            <table class="table">
              <thead>
                <tr><th>ID</th><th>申请人</th><th>请假类型</th><th>开始时间</th><th>结束时间</th><th>原因</th><th>状态</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="leave in leaveRequests" :key="leave.id">
                  <td>{{ leave.id }}</td><td>{{ getUserRealName(leave.userId) }}</td><td>{{ getLeaveTypeName(leave.leaveType) }}</td><td>{{ formatDateTime(leave.startTime) }}</td><td>{{ formatDateTime(leave.endTime) }}</td><td>{{ leave.reason || '-' }}</td>
                  <td :class="'status-' + getStatusClass(leave.status)">{{ leave.status }}</td>
                  <td>
                    <div v-if="userInfo.role === 'admin' && leave.status === '待审批'">
                      <button class="btn btn-sm btn-success" @click="approveLeave(leave.id, '已通过')">通过</button>
                      <button class="btn btn-sm btn-danger" @click="approveLeave(leave.id, '已驳回')">拒绝</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else-if="currentModule === 'makeup'">
          <div class="card">
            <div class="card-title">补卡申请</div>
            <button class="btn btn-primary" @click="showMakeupModal = true">申请补卡</button>
            <table class="table">
              <thead>
                <tr><th>ID</th><th>申请人</th><th>补卡日期</th><th>补卡时间</th><th>补卡类型</th><th>原因</th><th>状态</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="makeup in makeupRequests" :key="makeup.id">
                  <td>{{ makeup.id }}</td><td>{{ getUserRealName(makeup.userId) }}</td><td>{{ formatDate(makeup.recordDate) }}</td><td>{{ formatDateTime(makeup.makeupTime) }}</td><td>{{ makeup.makeupType === '上班补卡' ? '上班' : '下班' }}</td><td>{{ makeup.reason || '-' }}</td>
                  <td :class="'status-' + getStatusClass(makeup.status)">{{ makeup.status }}</td>
                  <td>
                    <div v-if="userInfo.role === 'admin' && makeup.status === '待审批'">
                      <button class="btn btn-sm btn-success" @click="approveMakeup(makeup.id, '已通过')">通过</button>
                      <button class="btn btn-sm btn-danger" @click="approveMakeup(makeup.id, '已驳回')">拒绝</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else-if="currentModule === 'overtime'">
          <div class="card">
            <div class="card-title">加班申请</div>
            <button class="btn btn-primary" @click="showOvertimeModal = true">申请加班</button>
            <table class="table">
              <thead>
                <tr><th>ID</th><th>申请人</th><th>开始时间</th><th>结束时间</th><th>原因</th><th>状态</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="overtime in overtimeRequests" :key="overtime.id">
                  <td>{{ overtime.id }}</td><td>{{ getUserRealName(overtime.userId) }}</td><td>{{ formatDateTime(overtime.startTime) }}</td><td>{{ formatDateTime(overtime.endTime) }}</td><td>{{ overtime.reason || '-' }}</td>
                  <td :class="'status-' + getStatusClass(overtime.status)">{{ overtime.status }}</td>
                  <td>
                    <div v-if="userInfo.role === 'admin' && overtime.status === '待审批'">
                      <button class="btn btn-sm btn-success" @click="approveOvertime(overtime.id, '已通过')">通过</button>
                      <button class="btn btn-sm btn-danger" @click="approveOvertime(overtime.id, '已驳回')">拒绝</button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else-if="currentModule === 'workstation'">
          <div class="card">
            <div class="card-title">工作台日志</div>
            <button class="btn btn-primary" @click="showWorkstationModal = true">添加日志</button>
            <table class="table">
              <thead>
                <tr><th>ID</th><th>工作台</th><th>日志详情</th><th>记录时间</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="log in workstationLogs" :key="log.id">
                  <td>{{ log.id }}</td><td>{{ log.workstationName || '-' }}</td><td>{{ log.actionDetail }}</td><td>{{ formatDateTime(log.createTime) }}</td>
                  <td>
                    <button class="btn btn-sm btn-danger" @click="deleteWorkstationLog(log.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-else-if="currentModule === 'user'">
          <div class="card">
            <div class="card-title">用户列表</div>
            <button class="btn btn-primary" @click="addUser">添加用户</button>
            <table class="table">
              <thead>
                <tr><th>ID</th><th>用户名</th><th>真实姓名</th><th>部门</th><th>角色</th><th>手机号</th><th>创建时间</th><th>操作</th></tr>
              </thead>
              <tbody>
                <tr v-for="user in users" :key="user.id">
                  <td>{{ user.id }}</td><td>{{ user.username }}</td><td>{{ user.realName }}</td><td>{{ getDepartmentName(user.departmentId) }}</td><td>{{ getRoleName(user.role) }}</td><td>{{ user.phone || '-' }}</td><td>{{ formatDate(user.createTime) }}</td>
                  <td>
                    <button class="btn btn-sm btn-warning" @click="editUser(user)">编辑</button>
                    <button class="btn btn-sm btn-danger" @click="deleteUser(user.id)">删除</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div v-if="currentModule === 'workstationMonitor'" class="workstation-monitor">
          <!-- 今日统计卡片 -->
          <div class="stats-row">
            <div class="stat-card">
              <div class="stat-num">{{ wsStats.total }}</div>
              <div class="stat-label">总员工数</div>
            </div>
            <div class="stat-card success">
              <div class="stat-num">{{ wsStats.checkedIn }}</div>
              <div class="stat-label">已打卡</div>
            </div>
            <div class="stat-card warning">
              <div class="stat-num">{{ wsStats.late }}</div>
              <div class="stat-label">异常打卡</div>
            </div>
            <div class="stat-card danger">
              <div class="stat-num">{{ wsStats.absent }}</div>
              <div class="stat-label">未打卡</div>
            </div>
          </div>

          <!-- 代打卡区 -->
          <div class="card">
            <div class="card-title">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
                <circle cx="8.5" cy="7" r="4"></circle>
                <polyline points="17 11 19 13 23 9"></polyline>
              </svg>
              代打卡
            </div>
            <div class="proxy-form">
              <div class="form-row">
                <div class="form-group">
                  <label>选择员工</label>
                  <select v-model="proxyForm.userId">
                    <option :value="null">请选择员工</option>
                    <option v-for="u in proxyUsers" :key="u.id" :value="u.id">{{ u.realName }} ({{ u.username }})</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>打卡类型</label>
                  <select v-model="proxyForm.type">
                    <option :value="1">上班打卡</option>
                    <option :value="2">下班打卡</option>
                  </select>
                </div>
                <div class="form-group">
                  <label>&nbsp;</label>
                  <button class="btn btn-primary" @click="handleProxyPunch" :disabled="proxyLoading || !proxyForm.userId">
                    {{ proxyLoading ? '打卡中...' : '确认打卡' }}
                  </button>
                </div>
              </div>
              <div class="location-info" v-if="location">
                当前位置：{{ currentAddress }}
              </div>
              <div class="location-info location-warning" v-else>正在获取位置...</div>
            </div>
          </div>

          <!-- 实时活动流 -->
          <div class="card">
            <div class="card-title">
              <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
              </svg>
              实时活动流
              <span class="refresh-badge" @click="loadWsActivities" style="cursor:pointer;font-size:12px;color:#1976d2;margin-left:10px;">
                🔄 刷新 ({{ wsCountdown }}s)
              </span>
            </div>
            <div class="activity-list">
              <div v-for="act in wsActivities" :key="act.id" class="activity-item" :class="{ 'activity-abnormal': act.actionDetail.includes('异常') }">
                <div class="activity-time">{{ formatDateTime(act.createTime) }}</div>
                <div class="activity-content">
                  <span class="activity-operator">{{ act.workstationName || '系统' }}</span>
                  <span class="activity-action">{{ act.actionDetail }}</span>
                </div>
              </div>
              <div v-if="wsActivities.length === 0" class="empty-records">
                <p>暂无活动记录</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="showDepartmentModal" class="modal-overlay" @click.self="showDepartmentModal = false">
      <div class="modal">
        <div class="modal-title">{{ editingDepartment ? '编辑部门' : '添加部门' }}</div>
        <div class="form-group">
          <label>部门名称</label>
          <input v-model="departmentForm.name" placeholder="请输入部门名称">
        </div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="saveDepartment">保存</button>
          <button class="btn" @click="showDepartmentModal = false">取消</button>
        </div>
      </div>
    </div>
    <div v-if="showRuleModal" class="modal-overlay" @click.self="showRuleModal = false">
      <div class="modal">
        <div class="modal-title">{{ editingRule ? '编辑规则' : '添加规则' }}</div>
        <div class="form-group"><label>规则名称</label><input v-model="ruleForm.ruleName"></div>
        <div class="form-group"><label>上班时间</label><input type="time" v-model="ruleForm.workStartTime"></div>
        <div class="form-group"><label>下班时间</label><input type="time" v-model="ruleForm.workEndTime"></div>
        <div class="form-group"><label>公司经度</label><input v-model="ruleForm.centerLongitude" placeholder="114.376899" inputmode="decimal"></div>
        <div class="form-group"><label>公司纬度</label><input v-model="ruleForm.centerLatitude" placeholder="30.500573" inputmode="decimal"></div>
        <div class="form-group"><label>允许半径(米)</label><input type="text" inputmode="numeric" v-model="ruleForm.allowedRadius" placeholder="1000"></div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="saveRule">保存</button>
          <button class="btn" @click="showRuleModal = false">取消</button>
        </div>
      </div>
    </div>
    <div v-if="showLeaveModal" class="modal-overlay" @click.self="showLeaveModal = false">
      <div class="modal">
        <div class="modal-title">申请请假</div>
        <div class="form-group"><label>请假类型</label><select v-model="leaveForm.leaveType"><option value="病假">病假</option><option value="事假">事假</option><option value="年假">年假</option><option value="产假">产假</option><option value="陪产假">陪产假</option></select></div>
        <div class="form-group"><label>开始时间</label><input type="datetime-local" v-model="leaveForm.startTime"></div>
        <div class="form-group"><label>结束时间</label><input type="datetime-local" v-model="leaveForm.endTime"></div>
        <div class="form-group"><label>原因</label><textarea v-model="leaveForm.reason"></textarea></div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="submitLeave">提交</button>
          <button class="btn" @click="showLeaveModal = false">取消</button>
        </div>
      </div>
    </div>
    <div v-if="showMakeupModal" class="modal-overlay" @click.self="showMakeupModal = false">
      <div class="modal">
        <div class="modal-title">申请补卡</div>
        <div class="form-group"><label>补卡日期</label><input type="date" v-model="makeupForm.recordDate"></div>
        <div class="form-group"><label>补卡时间</label><input type="datetime-local" v-model="makeupForm.makeupTime"></div>
        <div class="form-group"><label>补卡类型</label><select v-model="makeupForm.makeupType"><option value="上班补卡">上班</option><option value="下班补卡">下班</option></select></div>
        <div class="form-group"><label>原因</label><textarea v-model="makeupForm.reason"></textarea></div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="submitMakeup">提交</button>
          <button class="btn" @click="showMakeupModal = false">取消</button>
        </div>
      </div>
    </div>
    <div v-if="showOvertimeModal" class="modal-overlay" @click.self="showOvertimeModal = false">
      <div class="modal">
        <div class="modal-title">申请加班</div>
        <div class="form-group"><label>开始时间</label><input type="datetime-local" v-model="overtimeForm.startTime"></div>
        <div class="form-group"><label>结束时间</label><input type="datetime-local" v-model="overtimeForm.endTime"></div>
        <div class="form-group"><label>原因</label><textarea v-model="overtimeForm.reason"></textarea></div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="submitOvertime">提交</button>
          <button class="btn" @click="showOvertimeModal = false">取消</button>
        </div>
      </div>
    </div>
    <div v-if="showWorkstationModal" class="modal-overlay" @click.self="showWorkstationModal = false">
      <div class="modal">
        <div class="modal-title">添加工作台日志</div>
        <div class="form-group"><label>工作台</label><select v-model="workstationForm.workstationId"><option v-for="user in workstationUsers" :key="user.id" :value="user.id">{{ user.realName }} ({{ user.username }})</option></select></div>
        <div class="form-group"><label>日志详情</label><textarea v-model="workstationForm.actionDetail"></textarea></div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="addWorkstationLog">提交</button>
          <button class="btn" @click="showWorkstationModal = false">取消</button>
        </div>
      </div>
    </div>
    <div v-if="showUserModal" class="modal-overlay" @click.self="showUserModal = false">
      <div class="modal">
        <div class="modal-title">{{ editingUser ? '编辑用户' : '添加用户' }}</div>
        <div class="form-group"><label>用户名</label><input v-model="userForm.username"></div>
        <div class="form-group"><label>密码</label><input type="password" v-model="userForm.password" placeholder="默认123456"></div>
        <div class="form-group"><label>真实姓名</label><input v-model="userForm.realName"></div>
        <div class="form-group"><label>部门</label><select v-model="userForm.departmentId"><option :value="null">无</option><option v-for="dept in departments" :key="dept.id" :value="dept.id">{{ dept.name }}</option></select></div>
        <div class="form-group"><label>角色</label><select v-model="userForm.role"><option value="user">普通用户</option><option value="admin">管理员</option><option value="workstation">工作台</option></select></div>
        <div class="form-group"><label>手机号</label><input v-model="userForm.phone"></div>
        <div class="modal-footer">
          <button class="btn btn-primary" @click="saveUser">保存</button>
          <button class="btn" @click="showUserModal = false">取消</button>
        </div>
      </div>
    </div>
  </div>
  <script src="js/app.js?v=20260705"></script>
</body>
</html>