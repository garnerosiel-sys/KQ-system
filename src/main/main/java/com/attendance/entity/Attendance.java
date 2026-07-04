package com.attendance.entity;

import java.time.LocalDateTime;

public class Attendance {
    private Long id;
    private Long userId;
    private Integer type;          // 1=上班打卡, 2=下班打卡
    private Double latitude;       // 打卡纬度
    private Double longitude;      // 打卡经度
    private String address;        // 打卡地址描述
    private Integer status;        // 0=异常, 1=正常
    private String remark;         // 备注（异常原因等）
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
