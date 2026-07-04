package com.attendance.entity;

import java.time.LocalDateTime;

/**
 * 请假申请表
 */
public class LeaveRequest {
    private Long id;
    private Long userId;           // 申请人ID
    private Integer type;          // 请假类型：1=事假 2=病假 3=年假 4=婚假 5=丧假 6=产假
    private String reason;         // 请假原因
    private LocalDateTime startTime;   // 开始时间
    private LocalDateTime endTime;     // 结束时间
    private Double days;           // 请假天数
    private Integer status;        // 状态：0=待审批 1=已通过 2=已拒绝
    private String rejectReason;   // 拒绝原因
    private Long approverId;       // 审批人ID
    private LocalDateTime approveTime; // 审批时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // getters/setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Double getDays() { return days; }
    public void setDays(Double days) { this.days = days; }
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
