package com.attendance.service;

import com.attendance.entity.OvertimeRequest;
import com.attendance.exception.BusinessException;
import com.attendance.mapper.OvertimeRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OvertimeRequestService {

    @Autowired
    private OvertimeRequestMapper overtimeRequestMapper;

    public OvertimeRequest submit(OvertimeRequest request) {
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new BusinessException("开始时间不能晚于结束时间");
        }
        overtimeRequestMapper.insert(request);
        return request;
    }

    public void approve(Long id, Long approverId, boolean approved, String rejectReason) {
        OvertimeRequest request = overtimeRequestMapper.selectById(id);
        if (request == null) {
            throw new BusinessException("加班记录不存在");
        }
        if (request.getStatus() != 0) {
            throw new BusinessException("该申请已被处理");
        }
        OvertimeRequest update = new OvertimeRequest();
        update.setId(id);
        update.setApproverId(approverId);
        update.setStatus(approved ? 1 : 2);
        update.setRejectReason(rejectReason);
        overtimeRequestMapper.updateById(update);
    }

    public List<OvertimeRequest> getMyRecords(Long userId) {
        return overtimeRequestMapper.selectByUserId(userId);
    }

    public List<OvertimeRequest> getPending(Long approverId) {
        return overtimeRequestMapper.selectPending(approverId);
    }

    public List<OvertimeRequest> getAll(int page, int pageSize) {
        return overtimeRequestMapper.selectAll((page - 1) * pageSize, pageSize);
    }

    public int countAll() {
        return overtimeRequestMapper.countAll();
    }

    public OvertimeRequest getById(Long id) {
        return overtimeRequestMapper.selectById(id);
    }
}
