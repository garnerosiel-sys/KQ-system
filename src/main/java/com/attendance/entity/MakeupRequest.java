package com.attendance.entity;

import java.time.LocalDateTime;

/**
 * 调休申请表
 */
public class MakeupRequest {
    private Long id;
    private Long userId;           // 申请人ID
    private LocalDateTime sourceDate;   // 调休来源日期（加班或异常考勤日期）
    private LocalDateTime targetDate;   // 调休目标日期（补班日期）
    private String reason;         // 申请原因
    private Integer type;          // 类型：1=加班调休 2=异常补班 3=换班
    private Integer status;        // 状态：0=待审批 1=已通过 2=已拒绝
    private String rejectReason;
    private Long approverId;
    private LocalDateTime approveTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public LocalDateTime getSourceDate() { return sourceDate; }
    public void setSourceDate(LocalDateTime sourceDate) { this.sourceDate = sourceDate; }
    public LocalDateTime getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDateTime targetDate) { this.targetDate = targetDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
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
