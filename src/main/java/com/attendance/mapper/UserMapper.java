package com.attendance.mapper;

import com.attendance.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectByUsername(String username);

    User selectById(Integer id);

    void insert(User user);

    void update(User user);

    void deleteById(Integer id);

    java.util.List<User> selectAll();
}