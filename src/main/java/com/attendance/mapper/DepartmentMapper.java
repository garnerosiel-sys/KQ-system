package com.attendance.mapper;

import com.attendance.entity.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    Department selectById(@Param("id") Integer id);

    Department selectByName(@Param("name") String name);

    List<Department> selectAll();

    int insert(Department department);

    int update(Department department);

    int deleteById(@Param("id") Integer id);
}