package com.attendance.mapper;

import com.attendance.entity.LeaveRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LeaveRequestMapper {
    int insert(LeaveRequest leaveRequest);
    int updateById(LeaveRequest leaveRequest);
    LeaveRequest selectById(@Param("id") Long id);
    List<LeaveRequest> selectByUserId(@Param("userId") Long userId);
    List<LeaveRequest> selectPending(@Param("approverId") Long approverId);
    List<LeaveRequest> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    int countAll();
}
