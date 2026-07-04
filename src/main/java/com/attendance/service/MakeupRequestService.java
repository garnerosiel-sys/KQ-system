package com.attendance.service;

import com.attendance.entity.MakeupRequest;

import java.util.List;

public interface MakeupRequestService {

    void submit(MakeupRequest request);

    void approve(Integer id, String status, String comment, Integer approverId, String approverName);

    MakeupRequest getById(Integer id);

    List<MakeupRequest> getByUserId(Integer userId);

    List<MakeupRequest> getAll();

    List<MakeupRequest> getByStatus(String status);
}