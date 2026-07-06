package com.attendance.service.impl;

import com.attendance.entity.Attendance;
import com.attendance.entity.Company;
import com.attendance.entity.WorkstationLog;
import com.attendance.exception.BusinessException;
import com.attendance.mapper.AttendanceMapper;
import com.attendance.mapper.CompanyMapper;
import com.attendance.mapper.WorkstationLogMapper;
import com.attendance.service.AttendanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private WorkstationLogMapper workstationLogMapper;

    private static final double EARTH_RADIUS = 6371000.0; // 地球半径（米）

    /**
     * 计算两点间的距离（Haversine公式）
     * @return 距离（米）
     */
    public double calcDistance(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    /**
     * 上班打卡核心逻辑
     * @param userId    用户ID
     * @param latitude  用户纬度
     * @param longitude 用户经度
     * @param address   地址描述
     */
    public Attendance checkIn(Integer userId, Double latitude,
                             Double longitude, String address) {
        return punch(userId, 1, latitude, longitude, address);
    }

    /**
     * 下班打卡核心逻辑
     * @param userId    用户ID
     * @param latitude  用户纬度
     * @param longitude 用户经度
     * @param address   地址描述
     */
    public Attendance checkOut(Integer userId, Double latitude,
                              Double longitude, String address) {
        return punch(userId, 2, latitude, longitude, address);
    }

    /**
     * GPS 打卡核心逻辑
     * @param userId    用户ID
     * @param type      打卡类型（1=上班，2=下班）
     * @param latitude  用户纬度
     * @param longitude 用户经度
     * @param address   地址描述
     */
    public Attendance punch(Integer userId, Integer type, Double latitude,
                            Double longitude, String address) {
        try {
            Company company = companyMapper.selectDefault();
            if (company == null) {
                throw new BusinessException("公司信息未配置，请联系管理员");
            }

            double distance = calcDistance(latitude, longitude,
                    company.getCenterLatitude(), company.getCenterLongitude());

            Attendance record = new Attendance();
            record.setUserId(userId);
            record.setType(type);
            record.setLatitude(latitude);
            record.setLongitude(longitude);
            record.setAddress(address);

            int radius = company.getRadius() != null ? company.getRadius() : 100;

            if (distance <= radius) {
                record.setStatus(1);
                record.setRemark("正常打卡，距离公司约 " + String.format("%.0f", distance) + " 米");
            } else {
                record.setStatus(0);
                record.setRemark("异常打卡，距离公司约 " + String.format("%.0f", distance) + " 米（超出允许范围 " + radius + " 米）");
            }

            attendanceMapper.insert(record);

            // 自动写入工作台日志
            try {
                WorkstationLog wsLog = new WorkstationLog();
                wsLog.setWorkstationId(userId);
                wsLog.setActionDetail((type == 1 ? "上班打卡" : "下班打卡")
                        + " - " + (record.getStatus() == 1 ? "正常" : "异常")
                        + " - 距离约" + String.format("%.0f", distance) + "米");
                wsLog.setCreateTime(new java.util.Date());
                workstationLogMapper.insert(wsLog);
            } catch (Exception logEx) {
                log.warn("写入工作台日志失败: {}", logEx.getMessage());
            }

            return record;
        } catch (Exception e) {
            log.error("打卡失败，用户ID: {}, 类型: {}, 位置: {}, {}", userId, type, latitude, longitude, e);
            throw new BusinessException("打卡失败: " + e.getMessage());
        }
    }

    /**
     * 代打卡（工作台/管理员替员工打卡）
     */
    @Transactional
    public Attendance punchFor(Integer operatorId, String operatorName, Integer targetUserId,
                               Integer type, Double latitude, Double longitude, String address) {
        // 以目标员工身份打卡
        Attendance record = punch(targetUserId, type, latitude, longitude, address);

        // 覆盖工作台日志：标记为代打卡
        try {
            WorkstationLog wsLog = new WorkstationLog();
            wsLog.setWorkstationId(operatorId);
            wsLog.setActionDetail((type == 1 ? "代上班打卡" : "代下班打卡")
                    + " - 员工ID:" + targetUserId
                    + " - " + (record.getStatus() == 1 ? "正常" : "异常"));
            wsLog.setCreateTime(new java.util.Date());
            workstationLogMapper.insert(wsLog);
        } catch (Exception logEx) {
            log.warn("写入代打卡日志失败: {}", logEx.getMessage());
        }

        return record;
    }

    public List<Attendance> getMyRecords(Integer userId) {
        return attendanceMapper.selectByUserId(userId);
    }

    public List<Attendance> getTodayRecords(Integer userId) {
        return attendanceMapper.selectByUserIdAndDate(userId, LocalDate.now().toString());
    }

    public List<Attendance> getAllRecords(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return attendanceMapper.selectAll(offset, pageSize);
    }

    public int countAll() {
        return attendanceMapper.countAll();
    }

    /** 获取单条考勤记录 */
    public Attendance getById(Integer id) {
        return attendanceMapper.selectById(id);
    }

    /** 补卡（仅管理员可操作） */
    @Transactional
    public void makeup(Integer recordId, Integer userId, String remark) {
        try {
            Attendance record = attendanceMapper.selectById(recordId);
            if (record == null) {
                throw new BusinessException("记录不存在");
            }
            record.setStatus(1);
            record.setRemark(remark != null ? remark : "管理员补卡");
            attendanceMapper.updateById(record);
            log.info("补卡成功，记录ID: {}, 操作人: {}", recordId, userId);
        } catch (Exception e) {
            log.error("补卡失败，记录ID: {}, 操作人: {}", recordId, userId, e);
            throw new BusinessException("补卡失败: " + e.getMessage());
        }
    }

    /** 更新打卡状态 */
    @Transactional
    public void updateStatus(Integer id, Integer status, String remark) {
        try {
            Attendance record = attendanceMapper.selectById(id);
            if (record == null) {
                throw new BusinessException("记录不存在");
            }
            record.setStatus(status);
            if (remark != null) {
                record.setRemark(remark);
            }
            attendanceMapper.updateById(record);
            log.info("更新打卡状态成功，记录ID: {}, 新状态: {}", id, status);
        } catch (Exception e) {
            log.error("更新打卡状态失败，记录ID: {}, 状态: {}", id, status, e);
            throw new BusinessException("更新状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户今日打卡状态
     */
    public Map<String, Object> getPunchStatus(Integer userId) {
        try {
            // 获取今日是否已上班打卡
            LocalDate today = LocalDate.now();
            List<Attendance> todayRecords = attendanceMapper.selectByUserIdAndDate(userId, today.toString());

            Map<String, Object> status = new java.util.HashMap<>();
            status.put("date", today);

            if (todayRecords.isEmpty()) {
                status.put("checkInTime", null);
                status.put("checkOutTime", null);
                status.put("morningStatus", "未打卡");
                status.put("eveningStatus", "未打卡");
                status.put("status", "未打卡");
            } else {
                Attendance morningRecord = null;
                Attendance eveningRecord = null;

                for (Attendance record : todayRecords) {
                    if (record.getType() == 1) {
                        morningRecord = record;
                    } else if (record.getType() == 2) {
                        eveningRecord = record;
                    }
                }

                status.put("checkInTime", morningRecord != null ? morningRecord.getCreateTime() : null);
                status.put("checkOutTime", eveningRecord != null ? eveningRecord.getCreateTime() : null);
                status.put("morningStatus", morningRecord != null ? (morningRecord.getStatus() == 1 ? "已打卡" : "打卡异常") : "未打卡");
                status.put("eveningStatus", eveningRecord != null ? (eveningRecord.getStatus() == 1 ? "已打卡" : "打卡异常") : "未打卡");

                // 判断今日整体状态
                if (morningRecord != null && morningRecord.getStatus() == 1) {
                    status.put("status", "正常");
                } else if (morningRecord != null && morningRecord.getStatus() == 0) {
                    status.put("status", "迟到");
                } else {
                    status.put("status", "未打卡");
                }
            }

            return status;
        } catch (Exception e) {
            log.error("获取打卡状态失败，用户ID: {}", userId, e);
            throw new BusinessException("获取打卡状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户今日打卡记录
     */
    public Map<String, Object> getTodayRecord(Integer userId) {
        try {
            LocalDate today = LocalDate.now();
            List<Attendance> todayRecords = attendanceMapper.selectByUserIdAndDate(userId, today.toString());

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("date", today);

            if (todayRecords.isEmpty()) {
                result.put("checkInTime", null);
                result.put("checkOutTime", null);
                result.put("morningStatus", "未打卡");
                result.put("eveningStatus", "未打卡");
                result.put("status", "未打卡");
            } else {
                Attendance morningRecord = null;
                Attendance eveningRecord = null;

                for (Attendance record : todayRecords) {
                    if (record.getType() == 1) {
                        morningRecord = record;
                    } else if (record.getType() == 2) {
                        eveningRecord = record;
                    }
                }

                result.put("checkInTime", morningRecord != null ? morningRecord.getCreateTime() : null);
                result.put("checkOutTime", eveningRecord != null ? eveningRecord.getCreateTime() : null);
                result.put("morningStatus", morningRecord != null ? (morningRecord.getStatus() == 1 ? "已打卡" : "打卡异常") : "未打卡");
                result.put("eveningStatus", eveningRecord != null ? (eveningRecord.getStatus() == 1 ? "已打卡" : "打卡异常") : "未打卡");

                // 判断今日整体状态
                if (morningRecord != null && morningRecord.getStatus() == 1) {
                    result.put("status", "正常");
                } else if (morningRecord != null && morningRecord.getStatus() == 0) {
                    result.put("status", "迟到");
                } else {
                    result.put("status", "未打卡");
                }
            }

            return result;
        } catch (Exception e) {
            log.error("获取今日打卡记录失败，用户ID: {}", userId, e);
            throw new BusinessException("获取今日打卡记录失败: " + e.getMessage());
        }
    }

}