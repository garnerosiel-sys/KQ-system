package com.attendance.mapper;

import com.attendance.entity.Company;

public interface CompanyMapper {
    Company selectDefault();
    int insert(Company company);
    int update(Company company);
}
