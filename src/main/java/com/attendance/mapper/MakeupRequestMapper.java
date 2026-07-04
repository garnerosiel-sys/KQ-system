package com.attendance.mapper;

import com.attendance.entity.MakeupRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MakeupRequestMapper {

    int insert(MakeupRequest makeupRequest);

    int update(MakeupRequest makeupRequest);

    MakeupRequest selectById(@Param("id") Integer id);

    List<MakeupRequest> selectByUserId(@Param("userId") Integer userId);

    List<MakeupRequest> selectAll();

    List<MakeupRequest> selectByStatus(@Param("status") String status);
}