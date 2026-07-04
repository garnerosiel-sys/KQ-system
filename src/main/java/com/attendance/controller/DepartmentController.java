package com.attendance.controller;

import com.attendance.annotation.RequireRole;
import com.attendance.common.Result;
import com.attendance.entity.Department;
import com.attendance.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/list")
    @RequireRole({"admin", "user", "workstation"})
    public Result<List<Department>> list() {
        return Result.success(departmentService.getAll());
    }

    @GetMapping("/{id}")
    @RequireRole({"admin", "user", "workstation"})
    public Result<Department> getById(@PathVariable Integer id) {
        return Result.success(departmentService.getById(id));
    }

    @PostMapping("/add")
    @RequireRole("admin")
    public Result<Void> add(@RequestBody Department department) {
        departmentService.add(department);
        return Result.success(null);
    }

    @PutMapping("/update")
    @RequireRole("admin")
    public Result<Void> update(@RequestBody Department department) {
        departmentService.update(department);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<Void> delete(@PathVariable Integer id) {
        departmentService.delete(id);
        return Result.success(null);
    }
}