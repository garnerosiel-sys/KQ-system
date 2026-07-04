package com.attendance.mapper;

import com.attendance.entity.MakeupRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MakeupRequestMapper {
    int insert(MakeupRequest request);
    int updateById(MakeupRequest request);
    MakeupRequest selectById(@Param("id") Long id);
    List<MakeupRequest> selectByUserId(@Param("userId") Long userId);
    List<MakeupRequest> selectPending(@Param("approverId") Long approverId);
    List<MakeupRequest> selectAll(@Param("offset") int offset, @Param("limit") int limit);
    int countAll();
}
