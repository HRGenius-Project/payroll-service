package com.hrgenius.payroll_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String employeeCode;

    @Column(nullable = false)
    private Integer month; // 1 to 12

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer totalWorkingDays;

    @Column(nullable = false)
    private Integer payableDays; // (Present + Approved Leaves)

    @Column(nullable = false)
    private Double grossSalary;

    @Column(nullable = false)
    private Double totalDeductions;

    @Column(nullable = false)
    private Double netSalary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PayrollStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime generatedAt;
}