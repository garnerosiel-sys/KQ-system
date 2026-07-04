package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.AttendanceRule;
import com.attendance.service.AttendanceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rule")
public class AttendanceRuleController {

    @Autowired
    private AttendanceRuleService attendanceRuleService;

    /** 创建考勤规则 */
    @PostMapping("/create")
    public Result create(@RequestBody AttendanceRule rule) {
        return Result.success(attendanceRuleService.create(rule));
    }

    /** 更新考勤规则 */
    @PostMapping("/update")
    public Result update(@RequestBody AttendanceRule rule) {
        attendanceRuleService.update(rule);
        return Result.success("更新成功");
    }

    /** 获取当前启用的规则 */
    @GetMapping("/active")
    public Result getActive() {
        return Result.success(attendanceRuleService.getActive());
    }

    /** 获取规则详情 */
    @GetMapping("/{id}")
    public Result getById(@PathVariable Long id) {
        return Result.success(attendanceRuleService.getById(id));
    }

    /** 所有考勤规则（管理员） */
    @GetMapping("/all")
    public Result all(@RequestParam(defaultValue = "1") int page,
                      @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> data = new HashMap<>();
        data.put("list", attendanceRuleService.getAll(page, pageSize));
        data.put("total", attendanceRuleService.countAll());
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }
}
