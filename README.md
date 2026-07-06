# 考勤管理系统（KQ-system）

基于 GPS 定位的企业考勤管理系统，支持员工打卡、请假/加班/补卡申请、工作台实时监控、管理员数据看板。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring 5.3 + Spring MVC + MyBatis 3.5 |
| 数据库 | MySQL 8.0 + Druid 连接池 |
| 前端 | Vue 3 + Axios + JSP |
| 地图服务 | 高德地图（逆地理编码） |
| 认证 | JWT（jjwt 0.9.1） |
| 构建工具 | Maven + Jetty 插件 |
| 运行环境 | Java 17 + Servlet 4.0 |

## 快速开始

### 1. 创建数据库

在 MySQL 中执行 `reset_db.sql`（或 `create_database.sql`）：

```sql
source G:\KQ-system\reset_db.sql
```

### 2. 启动项目

```bash
cd G:\KQ-system
mvn jetty:run
```

### 3. 访问

浏览器打开 `http://localhost:8080/`

## 预设账号

| 角色 | 用户名 | 密码 | 说明 |
|------|--------|------|------|
| 管理员 | admin | 123456 | 完整后台管理权限 |
| 普通员工 | zhangsan | 123456 | 打卡 + 申请 |
| 工作台 | workstation1 | 123456 | 代打卡 + 实时监控 |

## 功能模块

### 前台（普通员工）
- GPS 打卡（上班/下班）+ 高德地名显示
- 请假 / 加班 / 补卡申请
- 查看个人打卡记录和申请状态

### 后台（管理员）
- 数据看板（出勤统计）
- 部门管理 / 考勤规则管理
- 用户管理 / 公司信息编辑
- 所有申请的审批
- 工作台日志查看

### 工作台（审核员）
- 实时活动流（10 秒自动刷新）
- 代员工打卡
- 今日打卡统计

## 项目结构

```
src/main/java/com/attendance/
├── annotation/     # 自定义注解（@RequireRole）
├── aspect/         # AOP 切面（角色权限）
├── common/         # 通用类（Result）
├── config/         # Spring 配置 + 数据库初始化
├── controller/     # 控制器（10 个）
├── entity/         # 实体类（10 个）
├── exception/      # 全局异常处理
├── interceptor/    # JWT 登录拦截器
├── mapper/         # MyBatis 映射器（10 个）
├── service/        # 服务层
└── util/           # 工具类（JwtUtil）

src/main/webapp/
├── WEB-INF/        # web.xml + Spring MVC 配置
├── css/            # 样式
├── js/             # Vue 3 前端逻辑
├── login.html      # 登录页
├── register.html   # 注册页
├── index.jsp       # 主页面（根据角色切换视图）
└── index.html      # 备用主页
```

## 公司坐标配置

默认坐标：`30.500573, 114.376899`（武汉），打卡半径 `1000` 米。

管理员登录后可在「公司信息」页面修改，或直接改 `reset_db.sql` 的种子数据后重建数据库。
