package com.attendance.mapper;

import com.attendance.entity.WorkstationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WorkstationLogMapper {

    WorkstationLog selectById(@Param("id") Integer id);

    List<WorkstationLog> selectByWorkstationId(@Param("workstationId") Integer workstationId);

    List<WorkstationLog> selectAll();

    int insert(WorkstationLog log);

    int deleteById(@Param("id") Integer id);

    int deleteByWorkstationId(@Param("workstationId") Integer workstationId);
}