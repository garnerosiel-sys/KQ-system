package com.attendance.mapper;

import com.attendance.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceRecordMapper {

    void insert(AttendanceRecord record);

    void update(AttendanceRecord record);

    AttendanceRecord selectById(Integer id);

    AttendanceRecord selectByUserIdAndDate(@Param("userId") Integer userId, @Param("recordDate") String recordDate);

    List<AttendanceRecord> selectByUserIdAndDateRange(
            @Param("userId") Integer userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate);

    List<AttendanceRecord> selectAll(@Param("offset") int offset, @Param("limit") int limit);

    int selectCount();

    List<AttendanceRecord> selectByCondition(
            @Param("userId") Integer userId,
            @Param("recordDate") String recordDate,
            @Param("status") String status,
            @Param("offset") int offset,
            @Param("limit") int limit);

    int selectCountByCondition(
            @Param("userId") Integer userId,
            @Param("recordDate") String recordDate,
            @Param("status") String status);

    void deleteById(Integer id);
}