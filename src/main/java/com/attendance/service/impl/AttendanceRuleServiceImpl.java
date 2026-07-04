package com.attendance.service.impl;

import com.attendance.entity.AttendanceRule;
import com.attendance.mapper.AttendanceRuleMapper;
import com.attendance.service.AttendanceRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AttendanceRuleServiceImpl implements AttendanceRuleService {

    @Autowired
    private AttendanceRuleMapper attendanceRuleMapper;

    @Override
    public AttendanceRule getById(Integer id) {
        return attendanceRuleMapper.selectById(id);
    }

    @Override
    public AttendanceRule getLatest() {
        return attendanceRuleMapper.selectLatest();
    }

    @Override
    public List<AttendanceRule> getAll() {
        return attendanceRuleMapper.selectAll();
    }

    @Override
    public void add(AttendanceRule rule) {
        rule.setCreateTime(new Date());
        attendanceRuleMapper.insert(rule);
    }

    @Override
    public void update(AttendanceRule rule) {
        attendanceRuleMapper.update(rule);
    }

    @Override
    public void delete(Integer id) {
        attendanceRuleMapper.deleteById(id);
    }
}