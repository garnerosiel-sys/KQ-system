package com.attendance.entity;

import java.time.LocalDateTime;

/**
 * 考勤规则表
 */
public class AttendanceRule {
    private Long id;
    private String ruleName;       // 规则名称
    private LocalDateTime startTime;   // 上班时间
    private LocalDateTime endTime;     // 下班时间
    private Integer lateMinutes;   // 迟到宽容分钟数
    private Integer earlyMinutes;  // 早退宽容分钟数
    private Integer workDays;      // 工作制：5=五天 6=六天
    private Integer radius;        // 打卡有效半径(米)
    private Integer status;        // 状态：0=禁用 1=启用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Integer getLateMinutes() { return lateMinutes; }
    public void setLateMinutes(Integer lateMinutes) { this.lateMinutes = lateMinutes; }
    public Integer getEarlyMinutes() { return earlyMinutes; }
    public void setEarlyMinutes(Integer earlyMinutes) { this.earlyMinutes = earlyMinutes; }
    public Integer getWorkDays() { return workDays; }
    public void setWorkDays(Integer workDays) { this.workDays = workDays; }
    public Integer getRadius() { return radius; }
    public void setRadius(Integer radius) { this.radius = radius; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
