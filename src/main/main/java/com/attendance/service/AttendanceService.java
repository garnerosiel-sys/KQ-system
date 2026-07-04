package com.attendance.service;

import com.attendance.entity.Attendance;
import com.attendance.entity.Company;
import com.attendance.exception.BusinessException;
import com.attendance.mapper.AttendanceMapper;
import com.attendance.mapper.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceMapper attendanceMapper;

    @Autowired
    private CompanyMapper companyMapper;

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
     * GPS 打卡核心逻辑
     * @param userId    用户ID
     * @param type      打卡类型（1=上班, 2=下班）
     * @param latitude  用户纬度
     * @param longitude 用户经度
     * @param address   地址描述
     */
    public Attendance punch(Long userId, Integer type, Double latitude,
                            Double longitude, String address) {
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
        return record;
    }

    public List<Attendance> getMyRecords(Long userId) {
        return attendanceMapper.selectByUserId(userId);
    }

    public List<Attendance> getTodayRecords(Long userId) {
        return attendanceMapper.selectByUserIdAndDate(userId, LocalDate.now().toString());
    }

    public List<Attendance> getAllRecords(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return attendanceMapper.selectAll(offset, pageSize);
    }

    public int countAll() {
        return attendanceMapper.countAll();
    }
}
