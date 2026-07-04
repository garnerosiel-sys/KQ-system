package com.attendance.service;

import com.attendance.entity.AttendanceRule;

import java.util.List;

public interface AttendanceRuleService {

    AttendanceRule getById(Integer id);

    AttendanceRule getLatest();

    List<AttendanceRule> getAll();

    void add(AttendanceRule rule);

    void update(AttendanceRule rule);

    void delete(Integer id);
}