package com.hrgenius.payroll_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollProcessedEvent implements Serializable {
    private Long payrollId;
    private String employeeCode;
    private Integer month;
    private Integer year;
    private Double netSalary;
    private String status;
}