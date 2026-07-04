package com.attendance.controller;

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

    /** 创建部门 */
    @PostMapping("/create")
    public Result create(@RequestBody Department department) {
        return Result.success(departmentService.create(department));
    }

    /** 更新部门 */
    @PostMapping("/update")
    public Result update(@RequestBody Department department) {
        departmentService.update(department);
        return Result.success("更新成功");
    }

    /** 删除部门 */
    @PostMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        departmentService.delete(id);
        return Result.success("删除成功");
    }

    /** 部门详情 */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(departmentService.getById(id));
    }

    /** 所有部门 */
    @GetMapping("/all")
    public Result all() {
        List<Department> list = departmentService.getAll();
        return Result.success(list);
    }

    /** 子部门列表 */
    @GetMapping("/children/{parentId}")
    public Result children(@PathVariable Long parentId) {
        return Result.success(departmentService.getChildren(parentId));
    }
}
