-- 一键重置数据库（删除旧库 + 重建 + 种子数据）
-- 在 MySQL 客户端执行：source reset_db.sql

DROP DATABASE IF EXISTS attendance_system;
CREATE DATABASE attendance_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE attendance_system;

-- 公司表
CREATE TABLE company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    center_latitude DECIMAL(10,6) NOT NULL,
    center_longitude DECIMAL(10,6) NOT NULL,
    radius INT NOT NULL DEFAULT 100,
    address VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 部门表
CREATE TABLE department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'user',
    real_name VARCHAR(50),
    phone VARCHAR(20),
    department_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES department(id)
);

-- 考勤规则表
CREATE TABLE attendance_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(100) NOT NULL,
    work_start_time TIME NOT NULL,
    work_end_time TIME NOT NULL,
    center_longitude DECIMAL(10,6) NOT NULL DEFAULT 114.376899,
    center_latitude DECIMAL(10,6) NOT NULL DEFAULT 30.500573,
    allowed_radius INT NOT NULL DEFAULT 1000,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 打卡记录表
CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type INT NOT NULL COMMENT '1=上班 2=下班',
    latitude DECIMAL(10,6),
    longitude DECIMAL(10,6),
    address VARCHAR(255),
    status INT NOT NULL DEFAULT 0 COMMENT '0=异常 1=正常',
    remark VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 打卡明细表
CREATE TABLE attendance_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    check_in_time DATETIME,
    check_out_time DATETIME,
    check_in_longitude DECIMAL(10,6),
    check_in_latitude DECIMAL(10,6),
    check_out_longitude DECIMAL(10,6),
    check_out_latitude DECIMAL(10,6),
    status VARCHAR(20) DEFAULT '正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 请假表
CREATE TABLE leave_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT '待审批',
    approver_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 加班表
CREATE TABLE overtime_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT '待审批',
    approver_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 补卡表
CREATE TABLE makeup_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    record_date DATE NOT NULL,
    makeup_time DATETIME NOT NULL,
    makeup_type VARCHAR(20) NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT '待审批',
    approver_id BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 工作台日志表
CREATE TABLE workstation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    workstation_id BIGINT NOT NULL,
    action_detail VARCHAR(255) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workstation_id) REFERENCES user(id)
);

-- ====== 种子数据 ======

INSERT INTO company (name, center_latitude, center_longitude, radius, address) VALUES
('科技有限公司', 30.500573, 114.376899, 1000, '武汉市');

INSERT INTO department (name) VALUES ('总经办'), ('技术部'), ('人力资源部');

INSERT INTO user (username, password, role, real_name, department_id, phone) VALUES
('admin', '123456', 'admin', '系统管理员', 1, '13800138000'),
('zhangsan', '123456', 'user', '张三', 2, '13900139001'),
('lisi', '123456', 'user', '李四', 2, '13900139002'),
('wangwu', '123456', 'user', '王五', 2, '13900139003'),
('zhaoliu', '123456', 'user', '赵六', 3, '13900139004'),
('sunqi', '123456', 'user', '孙七', 3, '13900139005'),
('zhouba', '123456', 'user', '周八', 1, '13900139006'),
('wujiu', '123456', 'user', '吴九', 2, '13900139007'),
('zhengshi', '123456', 'user', '郑十', 2, '13900139008'),
('workstation1', '123456', 'workstation', '工作台1', 1, '13900139003');

INSERT INTO attendance_rule (rule_name, work_start_time, work_end_time, center_longitude, center_latitude, allowed_radius) VALUES
('标准考勤规则', '08:00:00', '18:00:00', 114.376899, 30.500573, 1000);
