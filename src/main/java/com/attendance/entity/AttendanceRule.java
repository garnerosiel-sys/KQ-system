package com.attendance.entity;

import java.math.BigDecimal;
import java.util.Date;

public class AttendanceRule {

    private Integer id;

    private String ruleName;

    private String workStartTime;

    private String workEndTime;

    private BigDecimal centerLongitude;

    private BigDecimal centerLatitude;

    private Integer allowedRadius;

    private Date createTime;

    public AttendanceRule() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getWorkStartTime() {
        return workStartTime;
    }

    public void setWorkStartTime(String workStartTime) {
        this.workStartTime = workStartTime;
    }

    public String getWorkEndTime() {
        return workEndTime;
    }

    public void setWorkEndTime(String workEndTime) {
        this.workEndTime = workEndTime;
    }

    public BigDecimal getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(BigDecimal centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public BigDecimal getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(BigDecimal centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public Integer getAllowedRadius() {
        return allowedRadius;
    }

    public void setAllowedRadius(Integer allowedRadius) {
        this.allowedRadius = allowedRadius;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}