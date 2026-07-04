package com.attendance.mapper;

import com.attendance.entity.Attendance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttendanceMapper {
    int insert(Attendance attendance);
    List<Attendance> selectByUserId(@Param("userId") Long userId);
    List<Attendance> selectByUserIdAndDate(@Param("userId") Long userId, @Param("date") String date);
    List<Attendance> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    int countAll();
}
