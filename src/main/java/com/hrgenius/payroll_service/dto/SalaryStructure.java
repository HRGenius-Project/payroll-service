package com.hrgenius.payroll_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryStructure {
    private String employeeCode;
    private Double basicSalary;
    private Double hra;
    private Double specialAllowance;
    private Double providentFund;
    private Double professionalTax;
}