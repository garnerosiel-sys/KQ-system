-- 手动创建数据库和表的 SQL 脚本
-- 请在 MySQL 客户端中执行

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS attendance_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE attendance_system;

-- 2. 创建表结构
-- 公司表
CREATE TABLE company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '公司ID',
    name VARCHAR(100) NOT NULL COMMENT '公司名称',
    center_latitude DECIMAL(10,6) NOT NULL COMMENT '公司中心点纬度',
    center_longitude DECIMAL(10,6) NOT NULL COMMENT '公司中心点经度',
    radius INT NOT NULL DEFAULT 100 COMMENT '有效打卡半径（米）',
    address VARCHAR(255) COMMENT '公司地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 部门表
CREATE TABLE department (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '部门ID',
    name VARCHAR(100) NOT NULL COMMENT '部门名称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 用户表
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role VARCHAR(20) NOT NULL DEFAULT 'user' COMMENT '角色',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    department_id BIGINT COMMENT '部门ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES department(id)
);

-- 考勤规则表
CREATE TABLE attendance_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '规则ID',
    rule_name VARCHAR(100) NOT NULL COMMENT '规则名称',
    work_start_time TIME NOT NULL COMMENT '上班时间',
    work_end_time TIME NOT NULL COMMENT '下班时间',
    center_longitude DECIMAL(10,6) NOT NULL DEFAULT 116.4074 COMMENT '公司中心点经度',
    center_latitude DECIMAL(10,6) NOT NULL DEFAULT 39.9042 COMMENT '公司中心点纬度',
    allowed_radius INT NOT NULL DEFAULT 100 COMMENT '允许打卡半径(米)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 考勤记录表
CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    type INT NOT NULL COMMENT '类型：1=上班打卡，2=下班打卡',
    latitude DECIMAL(10,6) COMMENT '打卡纬度',
    longitude DECIMAL(10,6) COMMENT '打卡经度',
    address VARCHAR(255) COMMENT '打卡地址描述',
    status INT NOT NULL DEFAULT 0 COMMENT '状态：0=异常，1=正常',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 打卡记录表
CREATE TABLE attendance_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_date DATE NOT NULL COMMENT '打卡日期',
    check_in_time DATETIME COMMENT '上班打卡时间',
    check_out_time DATETIME COMMENT '下班打卡时间',
    check_in_longitude DECIMAL(10,6) COMMENT '上班打卡经度',
    check_in_latitude DECIMAL(10,6) COMMENT '上班打卡纬度',
    check_out_longitude DECIMAL(10,6) COMMENT '下班打卡经度',
    check_out_latitude DECIMAL(10,6) COMMENT '下班打卡纬度',
    status VARCHAR(20) DEFAULT '正常' COMMENT '状态: 正常/迟到/早退/缺卡',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 请假申请表
CREATE TABLE leave_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    leave_type VARCHAR(20) NOT NULL COMMENT '类型：病假/事假/年假/产假/陪产假',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    reason TEXT COMMENT '请假原因',
    status VARCHAR(20) NOT NULL DEFAULT '待审批' COMMENT '状态：待审批/已通过/已驳回',
    approver_id BIGINT COMMENT '审批人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 加班申请表
CREATE TABLE overtime_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    reason TEXT COMMENT '加班原因',
    status VARCHAR(20) NOT NULL DEFAULT '待审批' COMMENT '状态：待审批/已通过/已驳回',
    approver_id BIGINT COMMENT '审批人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 调休申请表
CREATE TABLE makeup_request (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '申请人ID',
    record_date DATE NOT NULL COMMENT '补卡日期',
    makeup_time DATETIME NOT NULL COMMENT '补卡时间',
    makeup_type VARCHAR(20) NOT NULL COMMENT '类型：上班补卡/下班补卡',
    reason TEXT COMMENT '调休原因',
    status VARCHAR(20) NOT NULL DEFAULT '待审批' COMMENT '状态：待审批/已通过/已驳回',
    approver_id BIGINT COMMENT '审批人ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

-- 工作台记录表
CREATE TABLE workstation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    workstation_id BIGINT NOT NULL COMMENT '工作台账号ID',
    action_detail VARCHAR(255) NOT NULL COMMENT '日志详情',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workstation_id) REFERENCES user(id)
);

-- 插入初始数据
INSERT INTO department (name) VALUES
('总经办'),
('技术部'),
('人力资源部');

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

INSERT INTO company (name, center_latitude, center_longitude, radius, address) VALUES
('科技有限公司', 30.500573, 114.376899, 1000, '武汉市');

INSERT INTO attendance_rule (rule_name, work_start_time, work_end_time, center_longitude, center_latitude, allowed_radius) VALUES
('标准考勤规则', '08:00:00', '18:00:00', 114.376899, 30.500573, 1000);