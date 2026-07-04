package com.attendance.mapper;

import com.attendance.entity.AttendanceRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceRuleMapper {

    AttendanceRule selectById(Integer id);

    AttendanceRule selectLatest();

    List<AttendanceRule> selectAll();

    int insert(AttendanceRule rule);

    int update(AttendanceRule rule);

    int deleteById(@Param("id") Integer id);
}