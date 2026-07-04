package com.attendance.mapper;

import com.attendance.entity.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    User selectByUsername(@Param("username") String username);
    User selectById(@Param("id") Long id);
    int insert(User user);
    int updateById(User user);
}
