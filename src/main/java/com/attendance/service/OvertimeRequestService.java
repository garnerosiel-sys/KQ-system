package com.attendance.service;

import com.attendance.entity.OvertimeRequest;

import java.util.List;

public interface OvertimeRequestService {

    void submit(OvertimeRequest request);

    void approve(Integer id, String status, String comment, Integer approverId, String approverName);

    OvertimeRequest getById(Integer id);

    List<OvertimeRequest> getByUserId(Integer userId);

    List<OvertimeRequest> getAll();

    List<OvertimeRequest> getByStatus(String status);
}