package com.attendance.config;

import com.attendance.entity.Department;
import com.attendance.entity.User;
import com.attendance.mapper.DepartmentMapper;
import com.attendance.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DatabaseInitializer implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private static boolean initialized = false;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (initialized) {
            return;
        }
        
        if (event.getApplicationContext().getParent() != null) {
            return;
        }

        initialized = true;
        log.info("开始检查数据库初始化状态...");

        try {
            initDepartments();
            initAdmin();
            initEmployees();
            log.info("数据库初始化完成");
        } catch (Exception e) {
            log.error("数据库初始化失败", e);
            throw e;
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
            log.info("管理员用户创建成功，用户名: admin");
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
}