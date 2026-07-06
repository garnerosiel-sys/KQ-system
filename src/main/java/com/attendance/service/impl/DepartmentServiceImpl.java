package com.attendance.service.impl;

import com.attendance.entity.Department;
import com.attendance.mapper.DepartmentMapper;
import com.attendance.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentMapper departmentMapper;

    @Override
    public Department getById(Integer id) {
        return departmentMapper.selectById(id);
    }

    @Override
    public Department getByName(String name) {
        return departmentMapper.selectByName(name);
    }

    @Override
    public List<Department> getAll() {
        return departmentMapper.selectAll();
    }

    @Override
    public void add(Department department) {
        department.setCreateTime(new Date());
        departmentMapper.insert(department);
    }

    @Override
    public void update(Department department) {
        departmentMapper.update(department);
    }

    @Override
    public void delete(Integer id) {
        departmentMapper.deleteById(id);
    }
}