package com.attendance.service;

import com.attendance.entity.AttendanceRule;
import com.attendance.exception.BusinessException;
import com.attendance.mapper.AttendanceRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceRuleService {

    @Autowired
    private AttendanceRuleMapper attendanceRuleMapper;

    public AttendanceRule create(AttendanceRule rule) {
        attendanceRuleMapper.insert(rule);
        return rule;
    }

    public void update(AttendanceRule rule) {
        attendanceRuleMapper.updateById(rule);
    }

    public AttendanceRule getActive() {
        return attendanceRuleMapper.selectActive();
    }

    public AttendanceRule getById(Long id) {
        return attendanceRuleMapper.selectById(id);
    }

    public List<AttendanceRule> getAll(int page, int pageSize) {
        return attendanceRuleMapper.selectAll((page - 1) * pageSize, pageSize);
    }

    public int countAll() {
        return attendanceRuleMapper.countAll();
    }
}
