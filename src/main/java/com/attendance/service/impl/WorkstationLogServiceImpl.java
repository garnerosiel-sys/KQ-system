package com.attendance.service.impl;

import com.attendance.entity.WorkstationLog;
import com.attendance.mapper.WorkstationLogMapper;
import com.attendance.service.WorkstationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class WorkstationLogServiceImpl implements WorkstationLogService {

    @Autowired
    private WorkstationLogMapper workstationLogMapper;

    @Override
    public WorkstationLog getById(Integer id) {
        return workstationLogMapper.selectById(id);
    }

    @Override
    public List<WorkstationLog> getByWorkstationId(Integer workstationId) {
        return workstationLogMapper.selectByWorkstationId(workstationId);
    }

    @Override
    public List<WorkstationLog> getAll() {
        return workstationLogMapper.selectAll();
    }

    @Override
    public void add(WorkstationLog log) {
        log.setCreateTime(new Date());
        workstationLogMapper.insert(log);
    }

    @Override
    public void delete(Integer id) {
        workstationLogMapper.deleteById(id);
    }

    @Override
    public void deleteByWorkstationId(Integer workstationId) {
        workstationLogMapper.deleteByWorkstationId(workstationId);
    }

    @Override
    public List<WorkstationLog> getTodayActivities() {
        return workstationLogMapper.selectToday();
    }
}