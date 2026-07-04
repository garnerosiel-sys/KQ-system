package com.attendance.mapper;

import com.attendance.entity.OvertimeRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OvertimeRequestMapper {
    int insert(OvertimeRequest request);
    int updateById(OvertimeRequest request);
    OvertimeRequest selectById(@Param("id") Long id);
    List<OvertimeRequest> selectByUserId(@Param("userId") Long userId);
    List<OvertimeRequest> selectPending(@Param("approverId") Long approverId);
    List<OvertimeRequest> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    int countAll();
}
