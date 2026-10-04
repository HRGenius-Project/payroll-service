package com.hrgenius.payroll_service.controller;

import com.hrgenius.payroll_service.dto.PayrollProcessRequest;
import com.hrgenius.payroll_service.dto.PayrollResponse;
import com.hrgenius.payroll_service.dto.SalaryStructure;
import com.hrgenius.payroll_service.service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
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

    @GetMapping(value = "/download/{payrollId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> downloadPayslip(@PathVariable Long payrollId) {
        ByteArrayInputStream bis = payrollService.downloadPayslipPdf(payrollId);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=payslip_" + payrollId + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}