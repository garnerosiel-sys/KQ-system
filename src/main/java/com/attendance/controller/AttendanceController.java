package com.attendance.controller;

import com.attendance.common.Result;
import com.attendance.entity.Attendance;
import com.attendance.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    /**
     * GPS 打卡接口
     * 前端传来经纬度，后端计算距离并判断是否在有效范围内
     */
    @PostMapping("/punch")
    public Result punch(@RequestAttribute("currentUserId") Long userId,
                        @RequestBody Map<String, Object> params) {
        Integer type = (Integer) params.get("type");
        Double latitude = Double.valueOf(params.get("latitude").toString());
        Double longitude = Double.valueOf(params.get("longitude").toString());
        String address = (String) params.getOrDefault("address", "");

        Attendance record = attendanceService.punch(userId, type, latitude, longitude, address);
        return Result.success("打卡成功", record);
    }

    /** 我的打卡记录 */
    @GetMapping("/my-records")
    public Result myRecords(@RequestAttribute("currentUserId") Long userId) {
        return Result.success(attendanceService.getMyRecords(userId));
    }

    /** 今日打卡记录 */
    @GetMapping("/today")
    public Result todayRecords(@RequestAttribute("currentUserId") Long userId) {
        return Result.success(attendanceService.getTodayRecords(userId));
    }

    /** 所有打卡记录（管理员） */
    @GetMapping("/all")
    public Result allRecords(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("list", attendanceService.getAllRecords(page, pageSize));
        data.put("total", attendanceService.countAll());
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }
}
