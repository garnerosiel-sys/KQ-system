package com.attendance.service;

import com.attendance.entity.User;

import java.util.Map;

public interface UserService {

    Map<String, Object> login(String username, String password);

    User register(String username, String password, String realName, String phone);

    User getUserByUsername(String username);

    User getUserById(Integer id);

    java.util.List<User> getAllUsers();

    void addUser(User user);

    void updateUser(User user);

    void deleteUser(Integer id);

    /**
     * 根据用户信息生成JWT Token
     *
     * @param user 用户对象
     * @return JWT Token字符串
     */
    String generateToken(User user);
}