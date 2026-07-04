package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.OvertimeRequest;
import com.attendance.service.OvertimeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/overtime")
public class OvertimeRequestController {

    @Autowired
    private OvertimeRequestService overtimeRequestService;

    /** 提交加班申请 */
    @PostMapping("/submit")
    public Result submit(@RequestAttribute("currentUserId") Long userId,
                         @RequestBody OvertimeRequest request) {
        request.setUserId(userId);
        OvertimeRequest saved = overtimeRequestService.submit(request);
        return Result.success("提交成功", saved);
    }

    /** 审批加班（管理员） */
    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id,
                          @RequestAttribute("currentUserId") Long approverId,
                          @RequestParam boolean approved,
                          @RequestParam(required = false) String rejectReason) {
        overtimeRequestService.approve(id, approverId, approved, rejectReason);
        return Result.success(approved ? "已通过" : "已拒绝");
    }

    /** 我的加班记录 */
    @GetMapping("/my-records")
    public Result myRecords(@RequestAttribute("currentUserId") Long userId) {
        return Result.success(overtimeRequestService.getMyRecords(userId));
    }

    /** 待审批列表（管理员） */
    @GetMapping("/pending")
    public Result pending(@RequestAttribute("currentUserId") Long userId) {
        return Result.success(overtimeRequestService.getPending(userId));
    }

    /** 所有加班记录（管理员） */
    @GetMapping("/all")
    public Result all(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("list", overtimeRequestService.getAll(page, pageSize));
        data.put("total", overtimeRequestService.countAll());
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }

    /** 获取加班详情 */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(overtimeRequestService.getById(id));
    }
}
