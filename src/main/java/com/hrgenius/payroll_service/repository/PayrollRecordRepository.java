package com.hrgenius.payroll_service.repository;

import com.hrgenius.payroll_service.entity.PayrollRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRecordRepository extends JpaRepository<PayrollRecord, Long> {
    Optional<PayrollRecord> findByEmployeeCodeAndMonthAndYear(String employeeCode, Integer month, Integer year);
    List<PayrollRecord> findByEmployeeCodeOrderByYearDescMonthDesc(String employeeCode);
}