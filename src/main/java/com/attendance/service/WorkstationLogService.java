package com.attendance.service;

import com.attendance.entity.WorkstationLog;

import java.util.List;

public interface WorkstationLogService {

    WorkstationLog getById(Integer id);

    List<WorkstationLog> getByWorkstationId(Integer workstationId);

    List<WorkstationLog> getAll();

    void add(WorkstationLog log);

    void delete(Integer id);

    void deleteByWorkstationId(Integer workstationId);

    List<WorkstationLog> getTodayActivities();
}