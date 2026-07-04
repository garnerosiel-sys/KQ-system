package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.OvertimeRequest;
import com.attendance.service.OvertimeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeRequestController {

    @Autowired
    private OvertimeRequestService overtimeRequestService;

    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody OvertimeRequest request, HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        request.setUserId(userId);
        overtimeRequestService.submit(request);
        return Result.success(null);
    }

    @PutMapping("/approve")
    public Result<Void> approve(@RequestBody OvertimeRequest request, HttpServletRequest httpRequest) {
        Integer approverId = getCurrentUserId(httpRequest);
        overtimeRequestService.approve(request.getId(), request.getStatus(), null, approverId, null);
        return Result.success(null);
    }

    @GetMapping("/my")
    public Result<List<OvertimeRequest>> myRequests(HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        return Result.success(overtimeRequestService.getByUserId(userId));
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("currentUserId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        }
        return ((Long) userIdObj).intValue();
    }

    @GetMapping("/list")
    public Result<List<OvertimeRequest>> list() {
        return Result.success(overtimeRequestService.getAll());
    }

    @GetMapping("/pending")
    public Result<List<OvertimeRequest>> pending() {
        return Result.success(overtimeRequestService.getByStatus("待审批"));
    }
}