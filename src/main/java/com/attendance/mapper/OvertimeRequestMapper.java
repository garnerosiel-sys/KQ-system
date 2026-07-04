package com.attendance.mapper;

import com.attendance.entity.OvertimeRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OvertimeRequestMapper {

    int insert(OvertimeRequest overtimeRequest);

    int update(OvertimeRequest overtimeRequest);

    OvertimeRequest selectById(@Param("id") Integer id);

    List<OvertimeRequest> selectByUserId(@Param("userId") Integer userId);

    List<OvertimeRequest> selectAll();

    List<OvertimeRequest> selectByStatus(@Param("status") String status);
}