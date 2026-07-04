package com.attendance.service.impl;

import com.attendance.entity.MakeupRequest;
import com.attendance.mapper.MakeupRequestMapper;
import com.attendance.service.MakeupRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class MakeupRequestServiceImpl implements MakeupRequestService {

    @Autowired
    private MakeupRequestMapper makeupRequestMapper;

    @Override
    public void submit(MakeupRequest request) {
        request.setStatus("待审批");
        request.setCreateTime(new Date());
        makeupRequestMapper.insert(request);
    }

    @Override
    public void approve(Integer id, String status, String comment, Integer approverId, String approverName) {
        MakeupRequest request = makeupRequestMapper.selectById(id);
        if (request != null) {
            request.setStatus(status);
            request.setApproverId(approverId);
            makeupRequestMapper.update(request);
        }
    }

    @Override
    public MakeupRequest getById(Integer id) {
        return makeupRequestMapper.selectById(id);
    }

    @Override
    public List<MakeupRequest> getByUserId(Integer userId) {
        return makeupRequestMapper.selectByUserId(userId);
    }

    @Override
    public List<MakeupRequest> getAll() {
        return makeupRequestMapper.selectAll();
    }

    @Override
    public List<MakeupRequest> getByStatus(String status) {
        return makeupRequestMapper.selectByStatus(status);
    }
}