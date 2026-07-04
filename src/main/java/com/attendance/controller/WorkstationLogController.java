package com.attendance.controller;

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
    public Result<List<WorkstationLog>> list() {
        return Result.success(workstationLogService.getAll());
    }

    @GetMapping("/workstation/{workstationId}")
    public Result<List<WorkstationLog>> getByWorkstationId(@PathVariable Integer workstationId) {
        return Result.success(workstationLogService.getByWorkstationId(workstationId));
    }

    @GetMapping("/{id}")
    public Result<WorkstationLog> getById(@PathVariable Integer id) {
        return Result.success(workstationLogService.getById(id));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody WorkstationLog log) {
        workstationLogService.add(log);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        workstationLogService.delete(id);
        return Result.success(null);
    }
}