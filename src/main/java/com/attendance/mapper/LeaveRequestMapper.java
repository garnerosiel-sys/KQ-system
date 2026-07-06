package com.attendance.mapper;

import com.attendance.entity.LeaveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LeaveRequestMapper {

    int insert(LeaveRequest leaveRequest);

    int update(LeaveRequest leaveRequest);

    LeaveRequest selectById(@Param("id") Integer id);

    List<LeaveRequest> selectByUserId(@Param("userId") Integer userId);

    List<LeaveRequest> selectAll();

    List<LeaveRequest> selectByStatus(@Param("status") String status);
}