package com.attendance.controller;

import com.attendance.annotation.RequireRole;
import com.attendance.common.Result;
import com.attendance.entity.User;
import com.attendance.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户控制器
 * <p>
 * 处理用户认证相关的 HTTP 请求，包括登录、注册等功能。
 * </p>
 *
 * @author KQ-system
 */
@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        log.info("收到登录请求，用户名: {}", username);

        Map<String, Object> result = userService.login(username, password);
        return Result.success("登录成功", result);
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param realName 真实姓名
     * @param phone    手机号
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam(value = "realName", required = false) String realName,
                           @RequestParam(value = "phone", required = false) String phone) {
        log.info("收到注册请求，用户名: {}", username);

        User user = userService.register(username, password, realName, phone);
        return Result.success("注册成功", user);
    }

    @GetMapping("/user/list")
    @RequireRole({"admin", "workstation"})
    public Result userList() {
        return Result.success(userService.getAllUsers());
    }

    @GetMapping("/user/{id}")
    @RequireRole("admin")
    public Result getUser(@PathVariable Integer id) {
        return Result.success(userService.getUserById(id));
    }

    @PostMapping("/user/add")
    @RequireRole("admin")
    public Result addUser(@RequestBody User user) {
        userService.addUser(user);
        return Result.success("添加成功");
    }

    @PutMapping("/user/update")
    @RequireRole("admin")
    public Result updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success("更新成功");
    }

    @DeleteMapping("/user/{id}")
    @RequireRole("admin")
    public Result deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return Result.success("删除成功");
    }
}