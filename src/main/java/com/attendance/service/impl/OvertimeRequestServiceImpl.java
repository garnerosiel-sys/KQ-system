package com.attendance.service.impl;

import com.attendance.entity.OvertimeRequest;
import com.attendance.entity.User;
import com.attendance.entity.WorkstationLog;
import com.attendance.mapper.OvertimeRequestMapper;
import com.attendance.mapper.UserMapper;
import com.attendance.mapper.WorkstationLogMapper;
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

    @Autowired
    private WorkstationLogMapper workstationLogMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void submit(OvertimeRequest request) {
        request.setStatus("待审批");
        request.setCreateTime(new Date());
        overtimeRequestMapper.insert(request);

        try {
            User user = userMapper.selectById(request.getUserId());
            String userName = user != null ? user.getRealName() : "员工" + request.getUserId();
            WorkstationLog log = new WorkstationLog();
            log.setWorkstationId(request.getUserId());
            log.setActionDetail(userName + " 提交了加班申请，状态：待审批");
            log.setCreateTime(new Date());
            workstationLogMapper.insert(log);
        } catch (Exception e) { /* ignore */ }
    }

    @Override
    public void approve(Integer id, String status, String comment, Integer approverId, String approverName) {
        OvertimeRequest request = overtimeRequestMapper.selectById(id);
        if (request != null) {
            request.setStatus(status);
            request.setApproverId(approverId);
            overtimeRequestMapper.update(request);

            try {
                User user = userMapper.selectById(request.getUserId());
                String userName = user != null ? user.getRealName() : "员工" + request.getUserId();
                WorkstationLog log = new WorkstationLog();
                log.setWorkstationId(approverId);
                log.setActionDetail((approverName != null ? approverName : "管理员") + " 审批了" + userName + "的加班申请，状态：" + status);
                log.setCreateTime(new Date());
                workstationLogMapper.insert(log);
            } catch (Exception e) { /* ignore */ }
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