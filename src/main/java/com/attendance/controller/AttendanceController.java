package com.attendance.controller;

import com.attendance.annotation.RequireRole;
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
     * 上班打卡接口
     */
    @PostMapping("/check-in")
    @RequireRole({"admin", "user"})
    public Result checkIn(@RequestAttribute("currentUserId") Integer userId,
                          @RequestBody Map<String, Object> params) {
        Double latitude = Double.valueOf(params.get("latitude").toString());
        Double longitude = Double.valueOf(params.get("longitude").toString());
        String address = (String) params.getOrDefault("address", "");

        Attendance record = attendanceService.checkIn(userId, latitude, longitude, address);
        return Result.success("上班打卡成功", record);
    }

    /**
     * 下班打卡接口
     */
    @PostMapping("/check-out")
    @RequireRole({"admin", "user"})
    public Result checkOut(@RequestAttribute("currentUserId") Integer userId,
                           @RequestBody Map<String, Object> params) {
        Double latitude = Double.valueOf(params.get("latitude").toString());
        Double longitude = Double.valueOf(params.get("longitude").toString());
        String address = (String) params.getOrDefault("address", "");

        Attendance record = attendanceService.checkOut(userId, latitude, longitude, address);
        return Result.success("下班打卡成功", record);
    }

    /**
     * GPS 打卡接口（兼容旧版）
     */
    @PostMapping("/punch")
    @RequireRole({"admin", "user"})
    public Result punch(@RequestAttribute("currentUserId") Integer userId,
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
    @RequireRole({"admin", "user"})
    public Result myRecords(@RequestAttribute("currentUserId") Integer userId) {
        return Result.success(attendanceService.getMyRecords(userId));
    }

    /** GPS打卡状态 */
    @GetMapping("/status")
    @RequireRole({"admin", "user"})
    public Result getStatus(@RequestAttribute("currentUserId") Integer userId) {
        return Result.success(attendanceService.getPunchStatus(userId));
    }

    /** 今日打卡记录 */
    @GetMapping("/today")
    @RequireRole({"admin", "user"})
    public Result today(@RequestAttribute("currentUserId") Integer userId) {
        return Result.success(attendanceService.getTodayRecord(userId));
    }

    /** 所有打卡记录（管理员） */
    @GetMapping("/all")
    @RequireRole("admin")
    public Result allRecords(@RequestParam(defaultValue = "1") int page,
                             @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("list", attendanceService.getAllRecords(page, pageSize));
        data.put("total", attendanceService.countAll());
        data.put("page", page);
        data.put("pageSize", pageSize);
        return Result.success(data);
    }
    /** 获取考勤详情 */
    @GetMapping("/{id}")
    @RequireRole({"admin", "user", "workstation"})
    public Result getById(@PathVariable Integer id) {
        return Result.success(attendanceService.getById(id));
    }

    /** 补卡申请（管理员） */
    @PostMapping("/makeup")
    @RequireRole("admin")
    public Result makeup(@RequestAttribute("currentUserId") Integer userId,
                         @RequestParam Integer recordId,
                         @RequestParam String remark) {
        attendanceService.makeup(recordId, userId, remark);
        return Result.success("补卡成功");
    }

    /** 代打卡（工作台/管理员替员工打卡） */
    @PostMapping("/punch-for")
    @RequireRole({"admin", "workstation"})
    public Result punchFor(@RequestAttribute("currentUserId") Integer operatorId,
                           @RequestBody Map<String, Object> params) {
        Integer targetUserId = (Integer) params.get("userId");
        Integer type = (Integer) params.get("type");
        Double latitude = Double.valueOf(params.get("latitude").toString());
        Double longitude = Double.valueOf(params.get("longitude").toString());
        String address = (String) params.getOrDefault("address", "");
        String operatorName = (String) params.getOrDefault("operatorName", "工作台");

        Attendance record = attendanceService.punchFor(operatorId, operatorName,
                targetUserId, type, latitude, longitude, address);
        return Result.success("代打卡成功", record);
    }

    /** 修改打卡记录状态（管理员） */
    @PostMapping("/update-status")
    @RequireRole("admin")
    public Result updateStatus(@RequestParam Integer id,
                                @RequestParam Integer status,
                                @RequestParam(required = false) String remark) {
        attendanceService.updateStatus(id, status, remark);
        return Result.success("更新成功");
    }
}
