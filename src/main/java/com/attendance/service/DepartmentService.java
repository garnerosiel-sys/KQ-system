package com.attendance.service;

import com.attendance.entity.Department;

import java.util.List;

public interface DepartmentService {

    Department getById(Integer id);

    Department getByName(String name);

    List<Department> getAll();

    void add(Department department);

    void update(Department department);

    void delete(Integer id);
}