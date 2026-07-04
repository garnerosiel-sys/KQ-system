package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.LeaveRequest;
import com.attendance.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/leave")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    /** 提交请假申请 */
    @PostMapping("/submit")
    public Result submit(@RequestAttribute("currentUserId") Long userId,
                         @RequestBody LeaveRequest request) {
        request.setUserId(userId);
        LeaveRequest saved = leaveRequestService.submit(request);
        return Result.success("提交成功", saved);
    }

    /** 审批请假（管理员） */
    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id,
                          @RequestAttribute("currentUserId") Long approverId,
                          @RequestParam boolean approved,
                          @RequestParam(required = false) String rejectReason) {
        leaveRequestService.approve(id, approverId, approved, rejectReason);
        return Result.success(approved ? "已通过" : "已拒绝");
    }

    /** 我的请假记录 */
    @GetMapping("/my-records")
    public Result myRecords(@RequestAttribute("currentUserId") Long userId) {
        return Result.success(leaveRequestService.getMyRecords(userId));
    }

    /** 待审批列表（管理员） */
    @GetMapping("/pending")
    public Result pending(@RequestAttribute("currentUserId") Long userId) {
        return Result.success(leaveRequestService.getPending(userId));
    }

    /** 所有请假记录（管理员） */
    @GetMapping("/all")
    public Result all(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("list", leaveRequestService.getAll(page, pageSize));
        data.put("total", leaveRequestService.countAll());
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }

    /** 获取请假详情 */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(leaveRequestService.getById(id));
    }
}
