package com.attendance.service.impl;

import com.attendance.entity.LeaveRequest;
import com.attendance.mapper.LeaveRequestMapper;
import com.attendance.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    @Override
    public void submit(LeaveRequest request) {
        request.setStatus("待审批");
        request.setCreateTime(new Date());
        leaveRequestMapper.insert(request);
    }

    @Override
    public void approve(Integer id, String status, String comment, Integer approverId, String approverName) {
        LeaveRequest request = leaveRequestMapper.selectById(id);
        if (request != null) {
            request.setStatus(status);
            request.setApproverId(approverId);
            leaveRequestMapper.update(request);
        }
    }

    @Override
    public LeaveRequest getById(Integer id) {
        return leaveRequestMapper.selectById(id);
    }

    @Override
    public List<LeaveRequest> getByUserId(Integer userId) {
        return leaveRequestMapper.selectByUserId(userId);
    }

    @Override
    public List<LeaveRequest> getAll() {
        return leaveRequestMapper.selectAll();
    }

    @Override
    public List<LeaveRequest> getByStatus(String status) {
        return leaveRequestMapper.selectByStatus(status);
    }
}