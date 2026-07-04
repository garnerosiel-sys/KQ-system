package com.attendance.mapper;

import com.attendance.entity.AttendanceRule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AttendanceRuleMapper {
    int insert(AttendanceRule rule);
    int updateById(AttendanceRule rule);
    AttendanceRule selectById(@Param("id") Long id);
    AttendanceRule selectActive();
    List<AttendanceRule> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    int countAll();
}
