package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.MakeupRequest;
import com.attendance.service.MakeupRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/makeup")
public class MakeupRequestController {

    @Autowired
    private MakeupRequestService makeupRequestService;

    /** 提交调休/补班申请 */
    @PostMapping("/submit")
    public Result submit(@RequestAttribute("currentUserId") Long userId,
                         @RequestBody MakeupRequest request) {
        request.setUserId(userId);
        MakeupRequest saved = makeupRequestService.submit(request);
        return Result.success("提交成功", saved);
    }

    /** 审批调休（管理员） */
    @PostMapping("/approve/{id}")
    public Result approve(@PathVariable Long id,
                          @RequestAttribute("currentUserId") Long approverId,
                          @RequestParam boolean approved,
                          @RequestParam(required = false) String rejectReason) {
        makeupRequestService.approve(id, approverId, approved, rejectReason);
        return Result.success(approved ? "已通过" : "已拒绝");
    }

    /** 我的调休记录 */
    @GetMapping("/my-records")
    public Result myRecords(@RequestAttribute("currentUserId") Long userId) {
        return Result.success(makeupRequestService.getMyRecords(userId));
    }

    /** 待审批列表（管理员） */
    @GetMapping("/pending")
    public Result pending() {
        return Result.success(makeupRequestService.getPending());
    }

    /** 所有调休记录（管理员） */
    @GetMapping("/all")
    public Result all(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("list", makeupRequestService.getAll(page, pageSize));
        data.put("total", makeupRequestService.countAll());
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }

    /** 获取调休详情 */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(makeupRequestService.getById(id));
    }
}
