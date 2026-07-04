package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.MakeupRequest;
import com.attendance.service.MakeupRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/makeup")
public class MakeupRequestController {

    @Autowired
    private MakeupRequestService makeupRequestService;

    @PostMapping("/submit")
    public Result<Void> submit(@RequestBody MakeupRequest request, HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        request.setUserId(userId);
        makeupRequestService.submit(request);
        return Result.success(null);
    }

    @PutMapping("/approve")
    public Result<Void> approve(@RequestBody MakeupRequest request, HttpServletRequest httpRequest) {
        Integer approverId = getCurrentUserId(httpRequest);
        makeupRequestService.approve(request.getId(), request.getStatus(), null, approverId, null);
        return Result.success(null);
    }

    @GetMapping("/my")
    public Result<List<MakeupRequest>> myRequests(HttpServletRequest httpRequest) {
        Integer userId = getCurrentUserId(httpRequest);
        return Result.success(makeupRequestService.getByUserId(userId));
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("currentUserId");
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        }
        return ((Long) userIdObj).intValue();
    }

    @GetMapping("/list")
    public Result<List<MakeupRequest>> list() {
        return Result.success(makeupRequestService.getAll());
    }

    @GetMapping("/pending")
    public Result<List<MakeupRequest>> pending() {
        return Result.success(makeupRequestService.getByStatus("待审批"));
    }
}