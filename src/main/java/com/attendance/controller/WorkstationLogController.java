package com.attendance.controller;

import com.attendance.annotation.RequireRole;
import com.attendance.common.Result;
import com.attendance.entity.WorkstationLog;
import com.attendance.service.WorkstationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workstation-log")
public class WorkstationLogController {

    @Autowired
    private WorkstationLogService workstationLogService;

    @GetMapping("/list")
    @RequireRole({"admin", "workstation"})
    public Result<List<WorkstationLog>> list() {
        return Result.success(workstationLogService.getAll());
    }

    @GetMapping("/workstation/{workstationId}")
    @RequireRole({"admin", "workstation"})
    public Result<List<WorkstationLog>> getByWorkstationId(@PathVariable Integer workstationId) {
        return Result.success(workstationLogService.getByWorkstationId(workstationId));
    }

    @GetMapping("/{id}")
    @RequireRole({"admin", "workstation"})
    public Result<WorkstationLog> getById(@PathVariable Integer id) {
        return Result.success(workstationLogService.getById(id));
    }

    @PostMapping("/add")
    @RequireRole({"admin", "workstation"})
    public Result<Void> add(@RequestBody WorkstationLog log) {
        workstationLogService.add(log);
        return Result.success(null);
    }

    /** 今日活动流（工作台实时监控用） */
    @GetMapping("/today")
    @RequireRole({"admin", "workstation"})
    public Result<List<WorkstationLog>> today() {
        return Result.success(workstationLogService.getTodayActivities());
    }

    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<Void> delete(@PathVariable Integer id) {
        workstationLogService.delete(id);
        return Result.success(null);
    }
}