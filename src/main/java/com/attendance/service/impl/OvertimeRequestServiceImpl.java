package com.attendance.service.impl;

import com.attendance.entity.OvertimeRequest;
import com.attendance.mapper.OvertimeRequestMapper;
import com.attendance.service.OvertimeRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class OvertimeRequestServiceImpl implements OvertimeRequestService {

    @Autowired
    private OvertimeRequestMapper overtimeRequestMapper;

    @Override
    public void submit(OvertimeRequest request) {
        request.setStatus("待审批");
        request.setCreateTime(new Date());
        overtimeRequestMapper.insert(request);
    }

    @Override
    public void approve(Integer id, String status, String comment, Integer approverId, String approverName) {
        OvertimeRequest request = overtimeRequestMapper.selectById(id);
        if (request != null) {
            request.setStatus(status);
            request.setApproverId(approverId);
            overtimeRequestMapper.update(request);
        }
    }

    @Override
    public OvertimeRequest getById(Integer id) {
        return overtimeRequestMapper.selectById(id);
    }

    @Override
    public List<OvertimeRequest> getByUserId(Integer userId) {
        return overtimeRequestMapper.selectByUserId(userId);
    }

    @Override
    public List<OvertimeRequest> getAll() {
        return overtimeRequestMapper.selectAll();
    }

    @Override
    public List<OvertimeRequest> getByStatus(String status) {
        return overtimeRequestMapper.selectByStatus(status);
    }
}