package com.attendance.service;

import com.attendance.entity.Attendance;

import java.util.List;
import java.util.Map;

/**
 * 考勤服务接口
 */
public interface AttendanceService {

    /**
     * 上班打卡
     */
    Attendance checkIn(Integer userId, Double latitude, Double longitude, String address);

    /**
     * 下班打卡
     */
    Attendance checkOut(Integer userId, Double latitude, Double longitude, String address);

    /**
     * GPS 打卡（兼容旧版）
     */
    Attendance punch(Integer userId, Integer type, Double latitude, Double longitude, String address);

    /**
     * 代打卡（工作台/管理员为其他员工打卡）
     */
    Attendance punchFor(Integer operatorId, String operatorName, Integer targetUserId, Integer type,
                        Double latitude, Double longitude, String address);

    /**
     * 获取我的打卡记录
     */
    List<Attendance> getMyRecords(Integer userId);

    /**
     * 获取今日打卡记录列表
     */
    List<Attendance> getTodayRecords(Integer userId);

    /**
     * 分页获取所有打卡记录
     */
    List<Attendance> getAllRecords(int page, int pageSize);

    /**
     * 统计总记录数
     */
    int countAll();

    /**
     * 获取单条考勤记录
     */
    Attendance getById(Integer id);

    /**
     * 补卡（管理员操作）
     */
    void makeup(Integer recordId, Integer userId, String remark);

    /**
     * 更新打卡状态
     */
    void updateStatus(Integer id, Integer status, String remark);

    /**
     * 获取用户今日打卡状态
     */
    Map<String, Object> getPunchStatus(Integer userId);

    /**
     * 获取用户今日打卡记录详情
     */
    Map<String, Object> getTodayRecord(Integer userId);
}
