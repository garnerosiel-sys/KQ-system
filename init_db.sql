-- 考勤系统数据库初始化脚本
-- 请使用 MySQL 客户端工具执行

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS attendance_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE attendance_system;

-- 2. 删除已存在的表（如果存在）
DROP TABLE IF EXISTS workstation_log;
DROP TABLE IF EXISTS attendance_record;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS leave_request;
DROP TABLE IF EXISTS makeup_request;
DROP TABLE IF EXISTS overtime_request;
DROP TABLE IF EXISTS attendance_rule;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS company;

-- 3. 创建公司表
CREATE TABLE company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公司ID',
    name VARCHAR(100) NOT NULL COMMENT '公司名称',
    center_latitude DECIMAL(10,6) NOT NULL COMMENT '公司中心点纬度',
    center_longitude DECIMAL(10,6) NOT NULL COMMENT '公司中心点经度',
    radius INT NOT NULL DEFAULT 100 COMMENT '有效打卡半径（米）',
    address VARCHAR(255) COMMENT '公司地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
);

-- 4. 创建部门表
CREATE TABLE department (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    name VARCHAR(50) NOT NULL COMMENT '部门名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 5. 创建用户表
CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT '登录密码',
    role VARCHAR(20) NOT NULL COMMENT '角色: admin/user/workstation',
    real_name VARCHAR(30) COMMENT '真实姓名',
    department_id INT COMMENT '所属部门ID',
    phone VARCHAR(15) COMMENT '联系电话',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (department_id) REFERENCES department(id)
);

-- 6. 创建考勤规则表
CREATE TABLE attendance_rule (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    rule_name VARCHAR(50) NOT NULL COMMENT '规则名称',
    work_start_time TIME NOT NULL COMMENT '上班时间',
    work_end_time TIME NOT NULL COMMENT '下班时间',
    center_longitude DECIMAL(10,6) NOT NULL COMMENT '公司中心点经度',
    center_latitude DECIMAL(10,6) NOT NULL COMMENT '公司中心点纬度',
    allowed_radius INT NOT NULL DEFAULT 100 COMMENT '允许打卡半径(米)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

-- 7. 创建考勤记录表（GPS打卡）
CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '打卡ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type INT NOT NULL COMMENT '打卡类型: 1=上班, 2=下班',
    latitude DECIMAL(10,6) NOT NULL COMMENT '打卡纬度',
    longitude DECIMAL(10,6) NOT NULL COMMENT '打卡经度',
    address VARCHAR(255) COMMENT '打卡地址描述',
    status INT NOT NULL DEFAULT 0 COMMENT '打卡状态: 0=异常, 1=正常',
    remark VARCHAR(500) COMMENT '备注（异常原因等）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_user_id (user_id),
    INDEX idx_create_time (create_time)
);

-- 8. 创建传统考勤记录表
CREATE TABLE attendance_record (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id INT NOT NULL COMMENT '用户ID',
    record_date DATE NOT NULL COMMENT '打卡日期',
    check_in_time DATETIME COMMENT '上班打卡时间',
    check_in_longitude DECIMAL(10,6) COMMENT '上班打卡经度',
    check_in_latitude DECIMAL(10,6) COMMENT '上班打卡纬度',
    check_out_time DATETIME COMMENT '下班打卡时间',
    check_out_longitude DECIMAL(10,6) COMMENT '下班打卡经度',
    check_out_latitude DECIMAL(10,6) COMMENT '下班打卡纬度',
    status VARCHAR(20) DEFAULT '正常' COMMENT '状态: 正常/迟到/早退/缺卡',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 9. 创建请假申请表
CREATE TABLE leave_request (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '请假ID',
    user_id INT NOT NULL COMMENT '申请人ID',
    leave_type VARCHAR(20) NOT NULL COMMENT '请假类型',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    reason VARCHAR(255) COMMENT '请假事由',
    status VARCHAR(20) DEFAULT '待审批' COMMENT '状态: 待审批/已通过/已驳回',
    approver_id INT COMMENT '审批人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 10. 创建补卡申请表
CREATE TABLE makeup_request (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '补卡ID',
    user_id INT NOT NULL COMMENT '申请人ID',
    record_date DATE NOT NULL COMMENT '需要补卡的日期',
    makeup_time DATETIME NOT NULL COMMENT '实际补卡时间',
    makeup_type VARCHAR(20) NOT NULL COMMENT '补卡类型: 上班补卡/下班补卡',
    reason VARCHAR(255) COMMENT '补卡事由',
    status VARCHAR(20) DEFAULT '待审批' COMMENT '状态: 待审批/已通过/已驳回',
    approver_id INT COMMENT '审批人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 11. 创建加班申请表
CREATE TABLE overtime_request (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '加班ID',
    user_id INT NOT NULL COMMENT '申请人ID',
    start_time DATETIME NOT NULL COMMENT '加班开始时间',
    end_time DATETIME NOT NULL COMMENT '加班结束时间',
    reason VARCHAR(255) COMMENT '加班事由',
    status VARCHAR(20) DEFAULT '待审批' COMMENT '状态: 待审批/已通过/已驳回',
    approver_id INT COMMENT '审批人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 12. 创建工作台日志表
CREATE TABLE workstation_log (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    workstation_id INT NOT NULL COMMENT '工作台账号ID',
    action_detail VARCHAR(255) NOT NULL COMMENT '日志详情',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    FOREIGN KEY (workstation_id) REFERENCES user(id)
);

-- 13. 插入初始数据

-- 默认部门
INSERT INTO department (name) VALUES ('总经办');
INSERT INTO department (name) VALUES ('技术部');
INSERT INTO department (name) VALUES ('人力资源部');

-- 默认公司信息
INSERT INTO company (name, center_latitude, center_longitude, radius, address)
VALUES ('考勤系统演示公司', 39.9042, 116.4074, 500, '北京市东城区长安街');

-- 默认用户（管理员、普通员工、工作台）
INSERT INTO user (username, password, role, real_name, department_id, phone)
VALUES ('admin', '123456', 'admin', '系统管理员', 1, '13800138000');

INSERT INTO user (username, password, role, real_name, department_id, phone)
VALUES ('zhangsan', '123456', 'user', '张三', 2, '13900139001');

INSERT INTO user (username, password, role, real_name, department_id, phone)
VALUES ('lisi', '123456', 'user', '李四', 2, '13900139002');

INSERT INTO user (username, password, role, real_name, department_id, phone)
VALUES ('workstation1', '123456', 'workstation', '工作台1', 1, '13900139003');

-- 默认考勤规则
INSERT INTO attendance_rule (rule_name, work_start_time, work_end_time, center_longitude, center_latitude, allowed_radius)
VALUES ('标准考勤规则', '09:00:00', '18:00:00', 116.4074, 39.9042, 100);

-- 初始化完成
-- 数据库创建完成！
-- 可以在IDEA中使用连接信息连接到数据库：
-- 数据库名: attendance_system
-- 用户名: root
-- 密码: root
-- 端口: 3306