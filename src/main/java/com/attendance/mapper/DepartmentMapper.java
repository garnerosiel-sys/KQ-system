package com.attendance.mapper;

import com.attendance.entity.Department;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DepartmentMapper {
    int insert(Department department);
    int updateById(Department department);
    int deleteById(@Param("id") Long id);
    Department selectById(@Param("id") Long id);
    List<Department> selectAll();
    List<Department> selectByParentId(@Param("parentId") Long parentId);
}
