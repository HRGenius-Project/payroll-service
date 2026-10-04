package com.hrgenius.payroll_service.service;

import com.hrgenius.payroll_service.dto.PayrollProcessRequest;
import com.hrgenius.payroll_service.dto.PayrollProcessedEvent;
import com.hrgenius.payroll_service.dto.PayrollResponse;
import com.hrgenius.payroll_service.dto.SalaryStructure;
import com.hrgenius.payroll_service.entity.PayrollRecord;
import com.hrgenius.payroll_service.entity.PayrollStatus;
import com.hrgenius.payroll_service.repository.PayrollRecordRepository;
import com.hrgenius.payroll_service.repository.SalaryStructureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayrollService {

    private final SalaryStructureRepository salaryStructureRepository;
    private final PayrollRecordRepository payrollRecordRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange:hrgenius.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key-payroll-processed:payroll.processed}")
    private String routingKeyPayrollProcessed;

    @Transactional
    public SalaryStructure createOrUpdateSalaryStructure(SalaryStructure dto) {
        com.hrgenius.payroll_service.entity.SalaryStructure structure = salaryStructureRepository
                .findByEmployeeCode(dto.getEmployeeCode())
                .orElse(com.hrgenius.payroll_service.entity.SalaryStructure.builder()
                        .employeeCode(dto.getEmployeeCode())
                        .build());

        structure.setBasicSalary(dto.getBasicSalary());
        structure.setHra(dto.getHra());
        structure.setSpecialAllowance(dto.getSpecialAllowance());
        structure.setProvidentFund(dto.getProvidentFund());
        structure.setProfessionalTax(dto.getProfessionalTax());

        com.hrgenius.payroll_service.entity.SalaryStructure saved = salaryStructureRepository.save(structure);
        log.info("Saved salary structure for employee: {}", saved.getEmployeeCode());

        return mapToSalaryStructure(saved);
    }

    public SalaryStructure getSalaryStructure(String employeeCode) {
        com.hrgenius.payroll_service.entity.SalaryStructure structure = salaryStructureRepository
                .findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Salary structure not found for: " + employeeCode));
        return mapToSalaryStructure(structure);
    }

    @Transactional
    public PayrollResponse processPayroll(PayrollProcessRequest request) {
        com.hrgenius.payroll_service.entity.SalaryStructure structure = salaryStructureRepository
                .findByEmployeeCode(request.getEmployeeCode())
                .orElseThrow(() -> new RuntimeException("Salary structure not defined for: " + request.getEmployeeCode()));

        if (request.getTotalWorkingDays() == null || request.getTotalWorkingDays() <= 0) {
            throw new IllegalArgumentException("Total working days must be greater than zero");
        }

        double fixedGross = structure.getBasicSalary() + structure.getHra() + structure.getSpecialAllowance();
        double perDayPay = fixedGross / request.getTotalWorkingDays();
        double earnedGross = Math.round(perDayPay * request.getPayableDays() * 100.0) / 100.0;

        double totalDeductions = structure.getProvidentFund() + structure.getProfessionalTax();
        double netSalary = Math.max(0.0, Math.round((earnedGross - totalDeductions) * 100.0) / 100.0);

        PayrollRecord record = payrollRecordRepository
                .findByEmployeeCodeAndMonthAndYear(request.getEmployeeCode(), request.getMonth(), request.getYear())
                .orElse(PayrollRecord.builder()
                        .employeeCode(request.getEmployeeCode())
                        .month(request.getMonth())
                        .year(request.getYear())
                        .build());

        record.setTotalWorkingDays(request.getTotalWorkingDays());
        record.setPayableDays(request.getPayableDays());
        record.setGrossSalary(earnedGross);
        record.setTotalDeductions(totalDeductions);
        record.setNetSalary(netSalary);
        record.setStatus(PayrollStatus.PROCESSED);

        PayrollRecord saved = payrollRecordRepository.save(record);
        log.info("Processed payroll ID: {} for employee: {}", saved.getId(), saved.getEmployeeCode());

        PayrollProcessedEvent event = PayrollProcessedEvent.builder()
                .payrollId(saved.getId())
                .employeeCode(saved.getEmployeeCode())
                .month(saved.getMonth())
                .year(saved.getYear())
                .netSalary(saved.getNetSalary())
                .status(saved.getStatus().name())
                .build();

        rabbitTemplate.convertAndSend(exchange, routingKeyPayrollProcessed, event);
        log.info("Published payroll.processed event for employee: {}", saved.getEmployeeCode());

        return mapToPayrollResponse(saved);
    }

    public List<PayrollResponse> getPayrollHistory(String employeeCode) {
        return payrollRecordRepository.findByEmployeeCodeOrderByYearDescMonthDesc(employeeCode)
                .stream()
                .map(this::mapToPayrollResponse)
                .toList();
    }

    private SalaryStructure mapToSalaryStructure(com.hrgenius.payroll_service.entity.SalaryStructure entity) {
        return SalaryStructure.builder()
                .employeeCode(entity.getEmployeeCode())
                .basicSalary(entity.getBasicSalary())
                .hra(entity.getHra())
                .specialAllowance(entity.getSpecialAllowance())
                .providentFund(entity.getProvidentFund())
                .professionalTax(entity.getProfessionalTax())
                .build();
    }

    private PayrollResponse mapToPayrollResponse(PayrollRecord entity) {
        return PayrollResponse.builder()
                .id(entity.getId())
                .employeeCode(entity.getEmployeeCode())
                .month(entity.getMonth())
                .year(entity.getYear())
                .totalWorkingDays(entity.getTotalWorkingDays())
                .payableDays(entity.getPayableDays())
                .grossSalary(entity.getGrossSalary())
                .totalDeductions(entity.getTotalDeductions())
                .netSalary(entity.getNetSalary())
                .status(entity.getStatus())
                .generatedAt(entity.getGeneratedAt())
                .build();
    }
}