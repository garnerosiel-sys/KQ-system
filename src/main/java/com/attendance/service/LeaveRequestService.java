package com.attendance.service;

import com.attendance.entity.LeaveRequest;

import java.util.List;

public interface LeaveRequestService {

    void submit(LeaveRequest request);

    void approve(Integer id, String status, String comment, Integer approverId, String approverName);

    LeaveRequest getById(Integer id);

    List<LeaveRequest> getByUserId(Integer userId);

    List<LeaveRequest> getAll();

    List<LeaveRequest> getByStatus(String status);
}