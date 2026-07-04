package com.attendance.controller;

import com.attendance.annotation.RequireRole;
import com.attendance.common.Result;
import com.attendance.entity.AttendanceRecord;
import com.attendance.service.AttendanceRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/attendance-record")
public class AttendanceRecordController {

    private static final Logger log = LoggerFactory.getLogger(AttendanceRecordController.class);

    @Autowired
    private AttendanceRecordService attendanceRecordService;

    @PostMapping("/check-in")
    @RequireRole({"admin", "user"})
    public Result checkIn(HttpServletRequest request, @RequestBody Map<String, Double> params) {
        log.info("收到上班打卡请求，经度: {}, 纬度: {}", params.get("longitude"), params.get("latitude"));
        Integer userId = getCurrentUserId(request);
        AttendanceRecord record = attendanceRecordService.checkIn(userId, params.get("longitude"), params.get("latitude"));
        return Result.success("上班打卡成功", record);
    }

    @PostMapping("/check-out")
    @RequireRole({"admin", "user"})
    public Result checkOut(HttpServletRequest request, @RequestBody Map<String, Double> params) {
        log.info("收到下班打卡请求，经度: {}, 纬度: {}", params.get("longitude"), params.get("latitude"));
        Integer userId = getCurrentUserId(request);
        AttendanceRecord record = attendanceRecordService.checkOut(userId, params.get("longitude"), params.get("latitude"));
        return Result.success("下班打卡成功", record);
    }

    @GetMapping("/today")
    @RequireRole({"admin", "user"})
    public Result getTodayRecord(HttpServletRequest request) {
        Integer userId = getCurrentUserId(request);
        AttendanceRecord record = attendanceRecordService.getTodayRecord(userId);
        return Result.success(record);
    }

    @GetMapping("/date/{recordDate}")
    @RequireRole({"admin", "user"})
    public Result getRecordByDate(HttpServletRequest request, @PathVariable("recordDate") String recordDate) {
        Integer userId = getCurrentUserId(request);
        AttendanceRecord record = attendanceRecordService.getRecordByDate(userId, recordDate);
        return Result.success(record);
    }

    @GetMapping("/range")
    @RequireRole({"admin", "user"})
    public Result getRecordsByDateRange(HttpServletRequest request,
                                        @RequestParam("startDate") String startDate,
                                        @RequestParam("endDate") String endDate) {
        Integer userId = getCurrentUserId(request);
        List<AttendanceRecord> records = attendanceRecordService.getRecordsByDateRange(userId, startDate, endDate);
        return Result.success(records);
    }

    @GetMapping("/page")
    @RequireRole("admin")
    public Result getRecordsPage(@RequestParam(value = "userId", required = false) Integer userId,
                                 @RequestParam(value = "recordDate", required = false) String recordDate,
                                 @RequestParam(value = "status", required = false) String status,
                                 @RequestParam(value = "page", defaultValue = "1") Integer page,
                                 @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = attendanceRecordService.getRecordsPage(userId, recordDate, status, page, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @RequireRole({"admin", "user", "workstation"})
    public Result getRecordById(@PathVariable("id") Integer id) {
        AttendanceRecord record = attendanceRecordService.getRecordById(id);
        return Result.success(record);
    }

    @PutMapping("/update")
    @RequireRole("admin")
    public Result updateRecord(@RequestBody AttendanceRecord record) {
        attendanceRecordService.updateRecord(record);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result deleteRecord(@PathVariable("id") Integer id) {
        attendanceRecordService.deleteRecord(id);
        return Result.success("删除成功");
    }

    private Integer getCurrentUserId(HttpServletRequest request) {
        Object userIdObj = request.getAttribute("currentUserId");
        if (userIdObj == null) {
            throw new RuntimeException("用户未登录");
        }
        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        } else if (userIdObj instanceof Long) {
            return ((Long) userIdObj).intValue();
        }
        throw new IllegalArgumentException("无法获取用户ID，类型不匹配");
    }
}