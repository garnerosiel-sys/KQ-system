# KQ考勤管理系统 - 角色测试指南

## 🎯 测试前准备

### 1. 数据库初始化

```bash
# 执行初始化脚本（MySQL服务需要先启动）
mysql -uroot -proot < src/main/resources/init.sql
```

### 2. 启动应用

```bash
# 使用Maven Jetty插件启动
mvn jetty:run

# 或打包后部署
mvn clean package
# 将target/KQ-system.war部署到Tomcat
```

应用启动后访问: http://localhost:8080

---

## 👥 默认测试账户

| 角色 | 用户名 | 密码 | 真实姓名 | 部门 |
|------|--------|------|----------|------|
| 系统管理员 | admin | 123456 | 系统管理员 | 总经办 |
| 普通员工 | zhangsan | 123456 | 张三 | 技术部 |
| 普通员工 | lisi | 123456 | 李四 | 技术部 |
| 工作台 | workstation1 | 123456 | 工作台1 | 总经办 |

---

## 🔐 角色权限矩阵

| 功能 | admin | user | workstation |
|------|-------|------|-------------|
| **用户管理** |
| - 查看用户列表 | ✅ | ❌ | ❌ |
| - 添加用户 | ✅ | ❌ | ❌ |
| - 编辑用户 | ✅ | ❌ | ❌ |
| - 删除用户 | ✅ | ❌ | ❌ |
| **部门管理** |
| - 查看部门列表 | ✅ | ✅ | ✅ |
| - 添加部门 | ✅ | ❌ | ❌ |
| - 编辑部门 | ✅ | ❌ | ❌ |
| - 删除部门 | ✅ | ❌ | ❌ |
| **考勤规则** |
| - 查看规则 | ✅ | ✅ | ✅ |
| - 添加规则 | ✅ | ❌ | ❌ |
| - 编辑规则 | ✅ | ❌ | ❌ |
| - 删除规则 | ✅ | ❌ | ❌ |
| **打卡功能** |
| - 上班打卡 | ✅ | ✅ | ❌ |
| - 下班打卡 | ✅ | ✅ | ❌ |
| - 查看我的记录 | ✅ | ✅ | ❌ |
| - 查看所有记录 | ✅ | ❌ | ❌ |
| - 编辑记录 | ✅ | ❌ | ❌ |
| - 删除记录 | ✅ | ❌ | ❌ |
| **请假申请** |
| - 提交申请 | ✅ | ✅ | ❌ |
| - 查看我的申请 | ✅ | ✅ | ❌ |
| - 审批申请 | ✅ | ❌ | ✅ |
| - 查看所有申请 | ✅ | ❌ | ✅ |
| **补卡申请** |
| - 提交申请 | ✅ | ✅ | ❌ |
| - 查看我的申请 | ✅ | ✅ | ❌ |
| - 审批申请 | ✅ | ❌ | ✅ |
| - 查看所有申请 | ✅ | ❌ | ✅ |
| **加班申请** |
| - 提交申请 | ✅ | ✅ | ❌ |
| - 查看我的申请 | ✅ | ✅ | ❌ |
| - 审批申请 | ✅ | ❌ | ✅ |
| - 查看所有申请 | ✅ | ❌ | ✅ |
| **工作台日志** |
| - 查看日志 | ✅ | ❌ | ✅ |
| - 添加日志 | ✅ | ❌ | ✅ |
| - 删除日志 | ✅ | ❌ | ❌ |

---

## 🧪 测试用例

### 测试1: Admin权限测试

```bash
# 1. 登录
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 保存返回的token
TOKEN="返回的token值"

# 2. 测试管理员专用接口（应成功）
curl -X GET http://localhost:8080/api/user/list \
  -H "Authorization: Bearer $TOKEN"

# 3. 测试修改密码（应成功）
curl -X PUT http://localhost:8080/api/user/update \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"id":1,"password":"newpass123"}'
```

### 测试2: User权限测试

```bash
# 1. 登录
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}'

TOKEN="返回的token值"

# 2. 测试用户可访问接口（应成功）
curl -X GET http://localhost:8080/api/department/list \
  -H "Authorization: Bearer $TOKEN"

# 3. 测试打卡（应成功）
curl -X POST http://localhost:8080/api/attendance/check-in \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"longitude":116.4074,"latitude":39.9042}'

# 4. 测试管理员接口（应拒绝 - 403）
curl -X GET http://localhost:8080/api/user/list \
  -H "Authorization: Bearer $TOKEN"
# 预期: {"code":403,"message":"权限不足，拒绝访问"}
```

### 测试3: Workstation权限测试

```bash
# 1. 登录
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username":"workstation1","password":"123456"}'

TOKEN="返回的token值"

# 2. 测试查看待审批申请（应成功）
curl -X GET http://localhost:8080/api/leave/pending \
  -H "Authorization: Bearer $TOKEN"

# 3. 测试审批（应成功）
curl -X PUT http://localhost:8080/api/leave/approve \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"id":1,"status":"已通过"}'

# 4. 测试打卡（应拒绝 - 403）
curl -X POST http://localhost:8080/api/attendance/check-in \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"longitude":116.4074,"latitude":39.9042}'
# 预期: {"code":403,"message":"权限不足，拒绝访问"}
```

### 测试4: 未登录访问测试

```bash
# 不带token访问需要权限的接口（应拒绝 - 401）
curl -X GET http://localhost:8080/api/user/list
# 预期: {"code":401,"message":"Token 缺失，请先登录"}
```

---

## 🔍 预期响应状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 业务错误（参数错误、用户不存在等） |
| 401 | 未登录或Token无效 |
| 403 | 权限不足 |
| 500 | 服务器内部错误 |

---

## 🐛 常见问题排查

### 1. MySQL连接失败
```bash
# 检查MySQL服务状态
net start | grep mysql

# 检查端口占用
netstat -ano | findstr :3306
```

### 2. 权限检查不生效
- 检查 `src/main/java/com/attendance/aspect/RoleAspect.java` 是否存在
- 检查 `@RequireRole` 注解是否正确添加
- 检查 Token 是否有效

### 3. 应用启动失败
```bash
# 查看详细错误
mvn jetty:run -X
```

---

## ✅ 测试检查清单

- [ ] 数据库初始化成功
- [ ] 应用启动成功
- [ ] admin可以管理用户
- [ ] user可以打卡和提交申请
- [ ] workstation可以审批申请
- [ ] user无法访问管理员接口
- [ ] workstation无法打卡
- [ ] 未登录访问被拒绝
- [ ] Token过期后无法访问

---

## 📝 测试完成后

### 重置测试数据
```bash
mysql -uroot -proot attendance_system < src/main/resources/init.sql
```

### 停止应用
按 `Ctrl+C` 停止Jetty服务