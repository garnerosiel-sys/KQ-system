package com.attendance.controller;

import com.attendance.annotation.RequireRole;
import com.attendance.common.Result;
import com.attendance.entity.LeaveRequest;
import com.attendance.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping("/submit")
    @RequireRole({"admin", "user"})
    public Result<Void> submit(@RequestBody LeaveRequest request, HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        request.setUserId(userId);
        leaveRequestService.submit(request);
        return Result.success(null);
    }

    @PutMapping("/approve")
    @RequireRole({"admin", "workstation"})
    public Result<Void> approve(@RequestBody LeaveRequest request, HttpServletRequest httpRequest) {
        Integer approverId = getCurrentUserId(httpRequest);
        leaveRequestService.approve(request.getId(), request.getStatus(), null, approverId, null);
        return Result.success(null);
    }

    @GetMapping("/my")
    @RequireRole({"admin", "user"})
    public Result<List<LeaveRequest>> myRequests(HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        return Result.success(leaveRequestService.getByUserId(userId));
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("currentUserId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        } else if (userIdObj instanceof Long) {
            return ((Long) userIdObj).intValue();
        }
        throw new IllegalArgumentException("无法获取用户ID，类型不匹配");
    }

    @GetMapping("/list")
    @RequireRole({"admin", "workstation"})
    public Result<List<LeaveRequest>> list() {
        return Result.success(leaveRequestService.getAll());
    }

    @GetMapping("/pending")
    @RequireRole({"admin", "workstation"})
    public Result<List<LeaveRequest>> pending() {
        return Result.success(leaveRequestService.getByStatus("待审批"));
    }
}