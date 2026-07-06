package com.attendance.service.impl;

import com.attendance.entity.MakeupRequest;
import com.attendance.entity.User;
import com.attendance.entity.WorkstationLog;
import com.attendance.mapper.MakeupRequestMapper;
import com.attendance.mapper.UserMapper;
import com.attendance.mapper.WorkstationLogMapper;
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

    @Autowired
    private WorkstationLogMapper workstationLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void submit(MakeupRequest request) {
        request.setStatus("待审批");
        request.setCreateTime(new Date());
        makeupRequestMapper.insert(request);

        try {
            User user = userMapper.selectById(request.getUserId());
            String userName = user != null ? user.getRealName() : "员工" + request.getUserId();
            WorkstationLog log = new WorkstationLog();
            log.setWorkstationId(request.getUserId());
            log.setActionDetail(userName + " 提交了补卡申请，状态：待审批");
            log.setCreateTime(new Date());
            workstationLogMapper.insert(log);
        } catch (Exception e) { /* ignore */ }
    }

    @Override
    public void approve(Integer id, String status, String comment, Integer approverId, String approverName) {
        MakeupRequest request = makeupRequestMapper.selectById(id);
        if (request != null) {
            request.setStatus(status);
            request.setApproverId(approverId);
            makeupRequestMapper.update(request);

            try {
                User user = userMapper.selectById(request.getUserId());
                String userName = user != null ? user.getRealName() : "员工" + request.getUserId();
                WorkstationLog log = new WorkstationLog();
                log.setWorkstationId(approverId);
                log.setActionDetail((approverName != null ? approverName : "管理员") + " 审批了" + userName + "的补卡申请，状态：" + status);
                log.setCreateTime(new Date());
                workstationLogMapper.insert(log);
            } catch (Exception e) { /* ignore */ }
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