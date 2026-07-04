package com.attendance.controller;

import com.attendance.annotation.RequireRole;
import com.attendance.common.Result;
import com.attendance.entity.AttendanceRule;
import com.attendance.service.AttendanceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rule")
public class AttendanceRuleController {

    @Autowired
    private AttendanceRuleService attendanceRuleService;

    @GetMapping("/list")
    @RequireRole({"admin", "user", "workstation"})
    public Result<List<AttendanceRule>> list() {
        return Result.success(attendanceRuleService.getAll());
    }

    @GetMapping("/{id}")
    @RequireRole({"admin", "user", "workstation"})
    public Result<AttendanceRule> getById(@PathVariable Integer id) {
        return Result.success(attendanceRuleService.getById(id));
    }

    @GetMapping("/latest")
    @RequireRole({"admin", "user", "workstation"})
    public Result<AttendanceRule> getLatest() {
        return Result.success(attendanceRuleService.getLatest());
    }

    @PostMapping("/add")
    @RequireRole("admin")
    public Result<Void> add(@RequestBody AttendanceRule rule) {
        attendanceRuleService.add(rule);
        return Result.success(null);
    }

    @PutMapping("/update")
    @RequireRole("admin")
    public Result<Void> update(@RequestBody AttendanceRule rule) {
        attendanceRuleService.update(rule);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<Void> delete(@PathVariable Integer id) {
        attendanceRuleService.delete(id);
        return Result.success(null);
    }
}