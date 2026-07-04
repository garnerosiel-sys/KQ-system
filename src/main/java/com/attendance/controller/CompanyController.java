package com.attendance.controller;

import com.attendance.annotation.RequireRole;
import com.attendance.common.Result;
import com.attendance.entity.Company;
import com.attendance.mapper.CompanyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyMapper companyMapper;

    @GetMapping("/info")
    @RequireRole({"admin", "workstation"})
    public Result<Company> info() {
        return Result.success(companyMapper.selectDefault());
    }

    @PutMapping("/update")
    @RequireRole("admin")
    public Result<Void> update(@RequestBody Company company) {
        companyMapper.update(company);
        return Result.success(null);
    }
}
