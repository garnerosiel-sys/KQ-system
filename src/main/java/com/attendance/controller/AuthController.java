package com.attendance.controller;

import com.attendance.annotation.RequireRole;
import com.attendance.common.Result;
import com.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        Map<String, Object> result = userService.login(username, password);
        return Result.success("登录成功", result);
    }

    @PostMapping("/auth/register")
    @RequireRole({"admin", "user"})
    public Result register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "realName", required = false) String realName,
                           @RequestParam(value = "phone", required = false) String phone) {
        com.attendance.entity.User user = userService.register(username, password, realName, phone);
        return Result.success("注册成功", user);
    }

    @GetMapping("/user/info")
    public Result getUserInfo(@RequestAttribute("currentUserId") Long userId) {
        com.attendance.entity.User user = userService.getUserById(userId.intValue());
        Map<String, Object> data = new java.util.HashMap<>();
        data.put("userId", user.getId());
        data.put("username", user.getUsername());
        data.put("realName", user.getRealName());
        data.put("phone", user.getPhone());
        data.put("role", user.getRole());
        return Result.success(data);
    }
}
