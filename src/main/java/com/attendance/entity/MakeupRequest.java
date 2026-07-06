package com.attendance.entity;

import java.util.Date;

public class MakeupRequest {

    private Integer id;

    private Integer userId;

    private Date recordDate;

    private Date makeupTime;

    private String makeupType;

    private String reason;

    private String status;

    private Integer approverId;

    private Date createTime;

    public MakeupRequest() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public Date getMakeupTime() {
        return makeupTime;
    }

    public void setMakeupTime(Date makeupTime) {
        this.makeupTime = makeupTime;
    }

    public String getMakeupType() {
        return makeupType;
    }

    public void setMakeupType(String makeupType) {
        this.makeupType = makeupType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getApproverId() {
        return approverId;
    }

    public void setApproverId(Integer approverId) {
        this.approverId = approverId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}