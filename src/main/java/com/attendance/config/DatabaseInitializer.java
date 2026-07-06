package com.attendance.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.attendance.entity.AttendanceRule;
import com.attendance.entity.Company;
import com.attendance.entity.Department;
import com.attendance.entity.User;
import com.attendance.mapper.AttendanceRuleMapper;
import com.attendance.mapper.CompanyMapper;
import com.attendance.mapper.DepartmentMapper;
import com.attendance.mapper.UserMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private static boolean initialized = false;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private AttendanceRuleMapper attendanceRuleMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initialized) {
            return;
        }

        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        initialized = true;
        log.info("开始数据库初始化...");

        try {
            // 尝试连接业务数据库
            if (!tryConnectBusinessDB()) {
                log.error("无法连接到业务数据库，请检查 MySQL 配置");
                log.error("常见原因：");
                log.error("1. MySQL 服务未启动");
                log.error("2. root 用户密码错误");
                log.error("3. 防火墙阻止了连接");

                // 如果连接失败，只记录错误，不阻止应用启动
                log.warn("应用将跳过数据库初始化，请手动创建数据库和表");
                return;
            }

            // 检查表是否存在
            if (!checkTablesExist()) {
                log.info("表不存在，请手动执行 create_database.sql 创建表结构");
                return;
            }

            initCompany();
            initDepartments();
            initAttendanceRule();
            initAdmin();
            initEmployees();
            initWorkstationUser();
            log.info("数据库初始化完成");
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
            // 不抛出异常，让应用继续运行
        }
    }

    private boolean tryConnectBusinessDB() {
        String[] possiblePasswords = {"root", "123456", "", "password"};

        for (String password : possiblePasswords) {
            try {
                log.info("尝试连接数据库，密码长度: " + (password == null ? 0 : password.length()));

                String url = "jdbc:mysql://localhost:3306/attendance_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true";
                Connection conn = DriverManager.getConnection(url, "root", password);

                log.info("数据库连接成功！");
                conn.close();

                // 如果密码不是配置文件中的，需要更新配置
                if (!"root".equals(password)) {
                    log.warn("检测到使用密码: " + (password.isEmpty() ? "空密码" : password));
                    log.warn("建议更新 application.properties 中的 jdbc.password");
                }

                return true;
            } catch (SQLException e) {
                log.info("密码尝试失败: " + (password.isEmpty() ? "空密码" : password));
            }
        }

        return false;
    }

    private boolean checkTablesExist() {
        try {
            // 检查 user 表是否存在
            userMapper.countUsers();
            return true;
        } catch (Exception e) {
            log.info("表不存在，需要先创建表结构");
            return false;
        }
    }

    private void initCompany() {
        Company existing = companyMapper.selectDefault();
        if (existing == null) {
            Company company = new Company();
            company.setName("科技有限公司");
            company.setCenterLatitude(30.500573);
            company.setCenterLongitude(114.376899);
            company.setRadius(1000);
            company.setAddress("武汉市");
            company.setCreateTime(LocalDateTime.now());
            companyMapper.insert(company);
            log.info("公司信息创建成功");
        } else {
            // 强制更新坐标（防止旧数据导致打卡距离错误）
            existing.setCenterLatitude(30.500573);
            existing.setCenterLongitude(114.376899);
            existing.setRadius(1000);
            existing.setAddress("武汉市");
            companyMapper.update(existing);
            log.info("公司信息已更新，经纬度: {}, {}，半径: {}米",
                    existing.getCenterLatitude(), existing.getCenterLongitude(), existing.getRadius());
        }
    }

    private void initDepartments() {
        String[] departments = {
            "总经办",
            "技术部",
            "人力资源部"
        };

        for (String name : departments) {
            Department existing = departmentMapper.selectByName(name);
            if (existing == null) {
                Department dept = new Department();
                dept.setName(name);
                dept.setCreateTime(new Date());

                departmentMapper.insert(dept);
                log.info("部门创建成功，名称: {}", name);
            } else {
                log.info("部门已存在，名称: {}", name);
            }
        }
    }

    private void initAdmin() {
        User admin = userMapper.selectByUsername("admin");
        if (admin == null) {
            log.info("未检测到管理员用户，开始创建...");

            User newAdmin = new User();
            newAdmin.setUsername("admin");
            newAdmin.setPassword("123456");
            newAdmin.setRole("admin");
            newAdmin.setRealName("系统管理员");
            newAdmin.setDepartmentId(1);
            newAdmin.setPhone("13800138000");
            newAdmin.setCreateTime(new Date());

            userMapper.insert(newAdmin);
            log.info("管理员用户创建成功，用户名: admin, 密码: 123456");
        } else {
            log.info("管理员用户已存在，用户名: admin");
        }
    }

    private void initEmployees() {
        String[][] employees = {
            {"zhangsan", "123456", "张三", "2", "13900139001"},
            {"lisi", "123456", "李四", "2", "13900139002"},
            {"wangwu", "123456", "王五", "2", "13900139003"},
            {"zhaoliu", "123456", "赵六", "3", "13900139004"},
            {"sunqi", "123456", "孙七", "3", "13900139005"},
            {"zhouba", "123456", "周八", "1", "13900139006"},
            {"wujiu", "123456", "吴九", "2", "13900139007"},
            {"zhengshi", "123456", "郑十", "2", "13900139008"}
        };

        String[] deptNames = {"总经办", "技术部", "人力资源部"};

        for (String[] employee : employees) {
            String username = employee[0];
            String password = employee[1];
            String realName = employee[2];
            int deptId = Integer.parseInt(employee[3]);
            String phone = employee[4];

            User existing = userMapper.selectByUsername(username);
            if (existing == null) {
                User user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.setRole("user");
                user.setRealName(realName);
                user.setDepartmentId(deptId);
                user.setPhone(phone);
                user.setCreateTime(new Date());

                userMapper.insert(user);
                log.info("员工创建成功，用户名: {}, 姓名: {}", username, realName);
            } else {
                log.info("员工已存在，用户名: {}", username);
            }
        }
    }

    private void initAttendanceRule() {
        try {
            java.util.List<AttendanceRule> rules = attendanceRuleMapper.selectAll();
            if (rules == null || rules.isEmpty()) {
                AttendanceRule rule = new AttendanceRule();
                rule.setRuleName("标准考勤规则");
                rule.setWorkStartTime("08:00");
                rule.setWorkEndTime("18:00");
                rule.setCenterLongitude(new java.math.BigDecimal("114.376899"));
                rule.setCenterLatitude(new java.math.BigDecimal("30.500573"));
                rule.setAllowedRadius(1000);
                rule.setCreateTime(new Date());

                attendanceRuleMapper.insert(rule);
                log.info("考勤规则创建成功: 08:00-18:00, 半径1000米, 坐标: 114.38/30.50");
            } else {
                log.info("考勤规则已存在，共{}条", rules.size());
            }
        } catch (Exception e) {
            log.warn("初始化考勤规则失败（表可能不存在）: {}", e.getMessage());
        }
    }

    private void initWorkstationUser() {
        User existing = userMapper.selectByUsername("workstation1");
        if (existing == null) {
            User ws = new User();
            ws.setUsername("workstation1");
            ws.setPassword("123456");
            ws.setRole("workstation");
            ws.setRealName("工作台1");
            ws.setDepartmentId(1);
            ws.setPhone("13900139003");
            ws.setCreateTime(new Date());

            userMapper.insert(ws);
            log.info("工作站用户创建成功，用户名: workstation1");
        }
    }
}