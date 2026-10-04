package com.hrgenius.payroll_service.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "salary_structures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String employeeCode;

    @Column(nullable = false)
    private Double basicSalary;

    @Column(nullable = false)
    private Double hra; // House Rent Allowance

    @Column(nullable = false)
    private Double specialAllowance;

    @Column(nullable = false)
    private Double providentFund; // PF Deduction

    @Column(nullable = false)
    private Double professionalTax; // PT Deduction
}