package com.attendance.service.impl;

import com.attendance.entity.AttendanceRecord;
import com.attendance.entity.AttendanceRule;
import com.attendance.entity.User;
import com.attendance.exception.BusinessException;
import com.attendance.mapper.AttendanceRecordMapper;
import com.attendance.mapper.AttendanceRuleMapper;
import com.attendance.mapper.UserMapper;
import com.attendance.service.AttendanceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("attendanceRecordService")
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private static final Logger log = LoggerFactory.getLogger(AttendanceRecordServiceImpl.class);

    private static final String STATUS_NORMAL = "正常";
    private static final String STATUS_LATE = "迟到";
    private static final String STATUS_EARLY_LEAVE = "早退";
    private static final String STATUS_MISSING = "缺卡";
    private static final String STATUS_ABNORMAL = "异常";

    private static final int DEFAULT_VALID_RADIUS = 100;
    private static final String DEFAULT_WORK_START_TIME = "09:00:00";
    private static final String DEFAULT_WORK_END_TIME = "18:00:00";

    @Autowired
    private AttendanceRecordMapper attendanceRecordMapper;

    @Autowired
    private AttendanceRuleMapper attendanceRuleMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional
    public AttendanceRecord checkIn(Integer userId, Double longitude, Double latitude) {
        log.info("用户 {} 上班打卡，经度: {}, 纬度: {}", userId, longitude, latitude);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        AttendanceRecord existingRecord = attendanceRecordMapper.selectByUserIdAndDate(userId, today);

        if (existingRecord != null && existingRecord.getCheckInTime() != null) {
            throw new BusinessException("今日已完成上班打卡");
        }

        AttendanceRule rule = getAttendanceRule();
        double distance = calculateDistance(
                longitude, latitude,
                rule.getCenterLongitude().doubleValue(),
                rule.getCenterLatitude().doubleValue()
        );

        String status = determineStatus(distance, rule.getAllowedRadius(), new Date(), rule.getWorkStartTime(), true);

        if (existingRecord == null) {
            existingRecord = new AttendanceRecord();
            existingRecord.setUserId(userId);
            existingRecord.setRecordDate(java.sql.Date.valueOf(today));
            existingRecord.setCreateTime(new Date());
        }

        existingRecord.setCheckInTime(new Date());
        existingRecord.setCheckInLongitude(BigDecimal.valueOf(longitude));
        existingRecord.setCheckInLatitude(BigDecimal.valueOf(latitude));
        existingRecord.setStatus(status);

        if (existingRecord.getId() == null) {
            attendanceRecordMapper.insert(existingRecord);
        } else {
            attendanceRecordMapper.update(existingRecord);
        }

        log.info("上班打卡成功，用户: {}, 距离: {}米, 状态: {}", userId, String.format("%.2f", distance), status);
        return existingRecord;
    }

    @Override
    @Transactional
    public AttendanceRecord checkOut(Integer userId, Double longitude, Double latitude) {
        log.info("用户 {} 下班打卡，经度: {}, 纬度: {}", userId, longitude, latitude);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        AttendanceRecord existingRecord = attendanceRecordMapper.selectByUserIdAndDate(userId, today);

        if (existingRecord == null) {
            throw new BusinessException("今日未进行上班打卡");
        }

        if (existingRecord.getCheckOutTime() != null) {
            throw new BusinessException("今日已完成下班打卡");
        }

        AttendanceRule rule = getAttendanceRule();
        double distance = calculateDistance(
                longitude, latitude,
                rule.getCenterLongitude().doubleValue(),
                rule.getCenterLatitude().doubleValue()
        );

        String status = determineStatus(distance, rule.getAllowedRadius(), new Date(), rule.getWorkEndTime(), false);

        existingRecord.setCheckOutTime(new Date());
        existingRecord.setCheckOutLongitude(BigDecimal.valueOf(longitude));
        existingRecord.setCheckOutLatitude(BigDecimal.valueOf(latitude));
        existingRecord.setStatus(status);

        attendanceRecordMapper.update(existingRecord);

        log.info("下班打卡成功，用户: {}, 距离: {}米, 状态: {}", userId, String.format("%.2f", distance), status);
        return existingRecord;
    }

    @Override
    public AttendanceRecord getTodayRecord(Integer userId) {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return attendanceRecordMapper.selectByUserIdAndDate(userId, today);
    }

    @Override
    public AttendanceRecord getRecordByDate(Integer userId, String recordDate) {
        return attendanceRecordMapper.selectByUserIdAndDate(userId, recordDate);
    }

    @Override
    public List<AttendanceRecord> getRecordsByDateRange(Integer userId, String startDate, String endDate) {
        return attendanceRecordMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public Map<String, Object> getRecordsPage(Integer userId, String recordDate, String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<AttendanceRecord> records = attendanceRecordMapper.selectByCondition(userId, recordDate, status, offset, pageSize);
        int total = attendanceRecordMapper.selectCountByCondition(userId, recordDate, status);

        Map<String, Object> result = new HashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return result;
    }

    @Override
    @Transactional
    public void updateRecord(AttendanceRecord record) {
        AttendanceRecord existing = attendanceRecordMapper.selectById(record.getId());
        if (existing == null) {
            throw new BusinessException("打卡记录不存在");
        }
        attendanceRecordMapper.update(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Integer id) {
        AttendanceRecord record = attendanceRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException("打卡记录不存在");
        }
        attendanceRecordMapper.deleteById(id);
    }

    @Override
    public AttendanceRecord getRecordById(Integer id) {
        return attendanceRecordMapper.selectById(id);
    }

    private AttendanceRule getAttendanceRule() {
        AttendanceRule rule = attendanceRuleMapper.selectLatest();
        if (rule == null) {
            log.warn("未找到考勤规则，使用默认配置");
            rule = new AttendanceRule();
            rule.setCenterLongitude(new BigDecimal("116.4074"));
            rule.setCenterLatitude(new BigDecimal("39.9042"));
            rule.setAllowedRadius(DEFAULT_VALID_RADIUS);
            rule.setWorkStartTime(DEFAULT_WORK_START_TIME);
            rule.setWorkEndTime(DEFAULT_WORK_END_TIME);
        }
        return rule;
    }

    private double calculateDistance(double lon1, double lat1, double lon2, double lat2) {
        final int EARTH_RADIUS = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private String determineStatus(double distance, Integer allowedRadius, Date checkTime, String thresholdTime, boolean isCheckIn) {
        if (allowedRadius == null) {
            allowedRadius = DEFAULT_VALID_RADIUS;
        }

        if (distance > allowedRadius) {
            return STATUS_ABNORMAL;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            Date threshold = sdf.parse(thresholdTime);

            Calendar checkCal = Calendar.getInstance();
            checkCal.setTime(checkTime);

            Calendar thresholdCal = Calendar.getInstance();
            Calendar thresholdDateCal = Calendar.getInstance();
            thresholdDateCal.setTime(threshold);
            thresholdCal.set(Calendar.HOUR_OF_DAY, thresholdDateCal.get(Calendar.HOUR_OF_DAY));
            thresholdCal.set(Calendar.MINUTE, thresholdDateCal.get(Calendar.MINUTE));
            thresholdCal.set(Calendar.SECOND, thresholdDateCal.get(Calendar.SECOND));

            if (isCheckIn && checkCal.after(thresholdCal)) {
                return STATUS_LATE;
            }

            if (!isCheckIn && checkCal.before(thresholdCal)) {
                return STATUS_EARLY_LEAVE;
            }
        } catch (Exception e) {
            log.error("解析时间失败", e);
        }

        return STATUS_NORMAL;
    }
}