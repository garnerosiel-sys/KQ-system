CREATE DATABASE IF NOT EXISTS attendance_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE attendance_system;

DROP TABLE IF EXISTS workstation_log;
DROP TABLE IF EXISTS attendance_record;
DROP TABLE IF EXISTS leave_request;
DROP TABLE IF EXISTS makeup_request;
DROP TABLE IF EXISTS overtime_request;
DROP TABLE IF EXISTS attendance_rule;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS department;

CREATE TABLE department (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    name VARCHAR(50) NOT NULL COMMENT '部门名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

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

CREATE TABLE workstation_log (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '日志ID',
    workstation_id INT NOT NULL COMMENT '工作台账号ID',
    action_detail VARCHAR(255) NOT NULL COMMENT '日志详情',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
    FOREIGN KEY (workstation_id) REFERENCES user(id)
);

INSERT INTO department (name) VALUES ('总经办');
INSERT INTO department (name) VALUES ('技术部');
INSERT INTO department (name) VALUES ('人力资源部');

INSERT INTO user (username, password, role, real_name, department_id, phone) 
VALUES ('admin', '123456', 'admin', '系统管理员', 1, '13800138000');

INSERT INTO user (username, password, role, real_name, department_id, phone) 
VALUES ('zhangsan', '123456', 'user', '张三', 2, '13900139001');

INSERT INTO user (username, password, role, real_name, department_id, phone) 
VALUES ('lisi', '123456', 'user', '李四', 2, '13900139002');

INSERT INTO user (username, password, role, real_name, department_id, phone) 
VALUES ('workstation1', '123456', 'workstation', '工作台1', 1, '13900139003');

INSERT INTO attendance_rule (rule_name, work_start_time, work_end_time, center_longitude, center_latitude, allowed_radius) 
VALUES ('标准考勤规则', '09:00:00', '18:00:00', 116.4074, 39.9042, 100);