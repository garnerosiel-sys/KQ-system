package com.attendance.entity;

import java.time.LocalDateTime;

/**
 * 加班申请表
 */
public class OvertimeRequest {
    private Long id;
    private Long userId;           // 申请人ID
    private LocalDateTime overtimeDate;  // 加班日期
    private LocalDateTime startTime;     // 加班开始时间
    private LocalDateTime endTime;       // 加班结束时间
    private Double hours;          // 加班时长(小时)
    private String reason;         // 加班原因
    private Integer status;        // 状态：0=待审批 1=已通过 2=已拒绝
    private String rejectReason;   // 拒绝原因
    private Long approverId;       // 审批人ID
    private LocalDateTime approveTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getOvertimeDate() { return overtimeDate; }
    public void setOvertimeDate(LocalDateTime overtimeDate) { this.overtimeDate = overtimeDate; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Double getHours() { return hours; }
    public void setHours(Double hours) { this.hours = hours; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public Long getApproverId() { return approverId; }
    public void setApproverId(Long approverId) { this.approverId = approverId; }
    public LocalDateTime getApproveTime() { return approveTime; }
    public void setApproveTime(LocalDateTime approveTime) { this.approveTime = approveTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
