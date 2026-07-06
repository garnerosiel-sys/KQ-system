-- ==================================================
-- 考勤管理系统 - H2 数据库初始化脚本
-- H2 兼容模式 (MODE=MySQL)，去掉 COMMENT / ON UPDATE 等 H2 不支持的语法
-- ==================================================

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

CREATE TABLE company (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    center_latitude DECIMAL(10,6) NOT NULL,
    center_longitude DECIMAL(10,6) NOT NULL,
    radius INT NOT NULL DEFAULT 100,
    address VARCHAR(255),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE department (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    real_name VARCHAR(30),
    department_id INT,
    phone VARCHAR(15),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES department(id)
);

CREATE TABLE attendance_rule (
    id INT PRIMARY KEY AUTO_INCREMENT,
    rule_name VARCHAR(50) NOT NULL,
    work_start_time TIME NOT NULL,
    work_end_time TIME NOT NULL,
    center_longitude DECIMAL(10,6) NOT NULL,
    center_latitude DECIMAL(10,6) NOT NULL,
    allowed_radius INT NOT NULL DEFAULT 100,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE attendance_record (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    record_date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_in_longitude DECIMAL(10,6),
    check_in_latitude DECIMAL(10,6),
    check_out_time TIMESTAMP,
    check_out_longitude DECIMAL(10,6),
    check_out_latitude DECIMAL(10,6),
    status VARCHAR(20) DEFAULT '正常',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE leave_request (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    leave_type VARCHAR(20) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) DEFAULT '待审批',
    approver_id INT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

CREATE TABLE makeup_request (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    record_date DATE NOT NULL,
    makeup_time TIMESTAMP NOT NULL,
    makeup_type VARCHAR(20) NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) DEFAULT '待审批',
    approver_id INT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

CREATE TABLE overtime_request (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    reason VARCHAR(255),
    status VARCHAR(20) DEFAULT '待审批',
    approver_id INT,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (approver_id) REFERENCES user(id)
);

CREATE TABLE workstation_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    workstation_id INT NOT NULL,
    action_detail VARCHAR(255) NOT NULL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (workstation_id) REFERENCES user(id)
);

CREATE TABLE attendance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type INT NOT NULL,
    latitude DECIMAL(10,6) NOT NULL,
    longitude DECIMAL(10,6) NOT NULL,
    address VARCHAR(255),
    status INT NOT NULL DEFAULT 0,
    remark VARCHAR(500),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_attendance_user_id ON attendance(user_id);
CREATE INDEX idx_attendance_create_time ON attendance(create_time);

-- 种子数据
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

INSERT INTO company (name, center_latitude, center_longitude, radius, address)
VALUES ('考勤系统演示公司', 30.500573, 114.376899, 1000, '武汉市');

INSERT INTO attendance_rule (rule_name, work_start_time, work_end_time, center_longitude, center_latitude, allowed_radius)
VALUES ('标准考勤规则', '08:00:00', '18:00:00', 114.376899, 30.500573, 1000);
