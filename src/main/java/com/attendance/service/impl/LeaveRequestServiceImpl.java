package com.attendance.service.impl;

import com.attendance.entity.LeaveRequest;
import com.attendance.entity.User;
import com.attendance.entity.WorkstationLog;
import com.attendance.mapper.LeaveRequestMapper;
import com.attendance.mapper.UserMapper;
import com.attendance.mapper.WorkstationLogMapper;
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

    @Autowired
    private WorkstationLogMapper workstationLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void submit(LeaveRequest request) {
        request.setStatus("待审批");
        request.setCreateTime(new Date());
        leaveRequestMapper.insert(request);

        // 自动写工作台日志
        try {
            User user = userMapper.selectById(request.getUserId());
            String userName = user != null ? user.getRealName() : "员工" + request.getUserId();
            WorkstationLog log = new WorkstationLog();
            log.setWorkstationId(request.getUserId());
            log.setActionDetail(userName + " 提交了请假申请，状态：待审批");
            log.setCreateTime(new Date());
            workstationLogMapper.insert(log);
        } catch (Exception e) { /* 日志失败不影响主流程 */ }
    }

    @Override
    public void approve(Integer id, String status, String comment, Integer approverId, String approverName) {
        LeaveRequest request = leaveRequestMapper.selectById(id);
        if (request != null) {
            request.setStatus(status);
            request.setApproverId(approverId);
            leaveRequestMapper.update(request);

            // 自动写工作台日志
            try {
                User user = userMapper.selectById(request.getUserId());
                String userName = user != null ? user.getRealName() : "员工" + request.getUserId();
                WorkstationLog log = new WorkstationLog();
                log.setWorkstationId(approverId);
                log.setActionDetail((approverName != null ? approverName : "管理员") + " 审批了" + userName + "的请假申请，状态：" + status);
                log.setCreateTime(new Date());
                workstationLogMapper.insert(log);
            } catch (Exception e) { /* 日志失败不影响主流程 */ }
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