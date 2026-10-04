package com.hrgenius.payroll_service.controller;

import com.hrgenius.payroll_service.dto.PayrollProcessRequest;
import com.hrgenius.payroll_service.dto.PayrollResponse;
import com.hrgenius.payroll_service.dto.SalaryStructure;
import com.hrgenius.payroll_service.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollService;

    @PostMapping("/salary-structure")
    public ResponseEntity<SalaryStructure> saveSalaryStructure(@RequestBody SalaryStructure dto) {
        return ResponseEntity.ok(payrollService.createOrUpdateSalaryStructure(dto));
    }

    @GetMapping("/salary-structure/{employeeCode}")
    public ResponseEntity<SalaryStructure> getSalaryStructure(@PathVariable String employeeCode) {
        return ResponseEntity.ok(payrollService.getSalaryStructure(employeeCode));
    }

    @PostMapping("/process")
    public ResponseEntity<PayrollResponse> processPayroll(@RequestBody PayrollProcessRequest request) {
        return ResponseEntity.ok(payrollService.processPayroll(request));
    }

    @GetMapping("/history/{employeeCode}")
    public ResponseEntity<List<PayrollResponse>> getPayrollHistory(@PathVariable String employeeCode) {
        return ResponseEntity.ok(payrollService.getPayrollHistory(employeeCode));
    }
}