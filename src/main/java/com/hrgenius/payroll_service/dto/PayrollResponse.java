package com.hrgenius.payroll_service.dto;

import com.hrgenius.payroll_service.entity.PayrollStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollResponse {
    private Long id;
    private String employeeCode;
    private Integer month;
    private Integer year;
    private Integer totalWorkingDays;
    private Integer payableDays;
    private Double grossSalary;
    private Double totalDeductions;
    private Double netSalary;
    private PayrollStatus status;
    private LocalDateTime generatedAt;
}