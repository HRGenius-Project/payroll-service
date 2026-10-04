package com.hrgenius.payroll_service.dto;

import lombok.Data;

@Data
public class PayrollProcessRequest {
    private String employeeCode;
    private Integer month;
    private Integer year;
    private Integer totalWorkingDays;
    private Integer payableDays; // (Present + Approved Leaves)
}