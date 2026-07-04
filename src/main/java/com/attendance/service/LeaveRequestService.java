package com.attendance.service;

import com.attendance.entity.LeaveRequest;
import com.attendance.exception.BusinessException;
import com.attendance.mapper.LeaveRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    public LeaveRequest submit(LeaveRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        leaveRequestMapper.insert(request);
        return request;
    }

    public void approve(Long id, Long approverId, boolean approved, String rejectReason) {
        LeaveRequest request = leaveRequestMapper.selectById(id);
        if (request == null) {
            throw new BusinessException("请假记录不存在");
        }
        if (request.getStatus() != 0) {
            throw new BusinessException("该申请已被处理");
        }
        LeaveRequest update = new LeaveRequest();
        update.setId(id);
        update.setApproverId(approverId);
        update.setStatus(approved ? 1 : 2);
        update.setRejectReason(rejectReason);
        leaveRequestMapper.updateById(update);
    }

    public List<LeaveRequest> getMyRecords(Long userId) {
        return leaveRequestMapper.selectByUserId(userId);
    }

    public List<LeaveRequest> getPending(Long approverId) {
        return leaveRequestMapper.selectPending(approverId);
    }

    public List<LeaveRequest> getAll(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return leaveRequestMapper.selectAll(offset, pageSize);
    }

    public int countAll() {
        return leaveRequestMapper.countAll();
    }

    public LeaveRequest getById(Long id) {
        return leaveRequestMapper.selectById(id);
    }
}
