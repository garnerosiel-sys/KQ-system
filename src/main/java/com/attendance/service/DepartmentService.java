package com.attendance.service;

import com.attendance.entity.Department;
import com.attendance.mapper.DepartmentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    public Department create(Department department) {
        departmentMapper.insert(department);
        return department;
    }

    public void update(Department department) {
        departmentMapper.updateById(department);
    }

    public void delete(Long id) {
        departmentMapper.deleteById(id);
    }

    public Department getById(Long id) {
        return departmentMapper.selectById(id);
    }

    public List<Department> getAll() {
        return departmentMapper.selectAll();
    }

    public List<Department> getChildren(Long parentId) {
        return departmentMapper.selectByParentId(parentId);
    }
}
