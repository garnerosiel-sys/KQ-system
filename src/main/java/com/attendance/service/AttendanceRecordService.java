package com.attendance.service;

import com.attendance.entity.AttendanceRecord;

import java.util.List;
import java.util.Map;

public interface AttendanceRecordService {

    AttendanceRecord checkIn(Integer userId, Double longitude, Double latitude);

    AttendanceRecord checkOut(Integer userId, Double longitude, Double latitude);

    AttendanceRecord getTodayRecord(Integer userId);

    AttendanceRecord getRecordByDate(Integer userId, String recordDate);

    List<AttendanceRecord> getRecordsByDateRange(Integer userId, String startDate, String endDate);

    Map<String, Object> getRecordsPage(Integer userId, String recordDate, String status, Integer page, Integer pageSize);

    void updateRecord(AttendanceRecord record);

    void deleteRecord(Integer id);

    AttendanceRecord getRecordById(Integer id);
}