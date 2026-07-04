package com.attendance.service;

import com.attendance.entity.MakeupRequest;
import com.attendance.exception.BusinessException;
import com.attendance.mapper.MakeupRequestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MakeupRequestService {

    @Autowired
    private MakeupRequestMapper makeupRequestMapper;

    public MakeupRequest submit(MakeupRequest request) {
        makeupRequestMapper.insert(request);
        return request;
    }

    public void approve(Long id, Long approverId, boolean approved, String rejectReason) {
        MakeupRequest request = makeupRequestMapper.selectById(id);
        if (request == null) {
            throw new BusinessException("调休记录不存在");
        }
        if (request.getStatus() != 0) {
            throw new BusinessException("该申请已被处理");
        }
        MakeupRequest update = new MakeupRequest();
        update.setId(id);
        update.setApproverId(approverId);
        update.setStatus(approved ? 1 : 2);
        update.setRejectReason(rejectReason);
        makeupRequestMapper.updateById(update);
    }

    public List<MakeupRequest> getMyRecords(Long userId) {
        return makeupRequestMapper.selectByUserId(userId);
    }

    public List<MakeupRequest> getPending() {
        return makeupRequestMapper.selectPending(null);
    }

    public List<MakeupRequest> getAll(int page, int pageSize) {
        return makeupRequestMapper.selectAll((page - 1) * pageSize, pageSize);
    }

    public int countAll() {
        return makeupRequestMapper.countAll();
    }

    public MakeupRequest getById(Long id) {
        return makeupRequestMapper.selectById(id);
    }
}
