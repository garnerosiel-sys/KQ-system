package com.attendance.service.impl;

import com.attendance.entity.User;
import com.attendance.exception.BusinessException;
import com.attendance.mapper.UserMapper;
import com.attendance.service.UserService;
import com.attendance.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Override
    public Map<String, Object> login(String username, String password) {
        log.info("用户登录，用户名: {}", username);

        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!password.equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String token = JwtUtil.generateToken(user.getId(), user.getUsername());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("realName", user.getRealName());
        result.put("role", user.getRole());

        log.info("用户登录成功，用户ID: {}", user.getId());
        return result;
    }

    @Override
    @Transactional
    public User register(String username, String password, String realName, String phone) {
        log.info("用户注册，用户名: {}", username);

        User existing = userMapper.selectByUsername(username);
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realName);
        user.setPhone(phone);
        user.setRole("user");
        user.setCreateTime(new Date());

        userMapper.insert(user);

        log.info("用户注册成功，用户名: {}", username);
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User getUserById(Integer id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    @Transactional
    public void addUser(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        if (user.getRole() == null) {
            user.setRole("user");
        }
        user.setCreateTime(new Date());
        userMapper.insert(user);
    }

    @Override
    @Transactional
    public void updateUser(User user) {
        userMapper.update(user);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        userMapper.deleteById(id);
    }

    @Override
    public String generateToken(User user) {
        if (user == null || user.getId() == null || user.getUsername() == null) {
            throw new BusinessException("用户信息不完整，无法生成Token");
        }
        return JwtUtil.generateToken(user.getId(), user.getUsername());
    }
}