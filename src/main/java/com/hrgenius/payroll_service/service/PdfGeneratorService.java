package com.hrgenius.payroll_service.service;

import com.hrgenius.payroll_service.entity.PayrollRecord;
import com.hrgenius.payroll_service.entity.SalaryStructure;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    public ByteArrayInputStream generatePayslipPdf(PayrollRecord record, SalaryStructure structure) {
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Fonts setup
            Font companyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, new Color(41, 128, 185));
            Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.DARK_GRAY);
            Font boldCellFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);
            Font normalCellFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

            // Company Title
            Paragraph companyTitle = new Paragraph("HRGenius Systems", companyFont);
            companyTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(companyTitle);

            Paragraph title = new Paragraph("MONTHLY SALARY PAYSLIP", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph period = new Paragraph(String.format("Pay Period: %02d/%d  |  Status: %s",
                    record.getMonth(), record.getYear(), record.getStatus()), subHeaderFont);
            period.setAlignment(Element.ALIGN_CENTER);
            period.setSpacingAfter(18);
            document.add(period);

            // Employee Summary Details Table
            PdfPTable empTable = new PdfPTable(4);
            empTable.setWidthPercentage(100);
            empTable.setWidths(new float[]{25, 25, 25, 25});
            empTable.setSpacingAfter(15);

            addInfoCell(empTable, "Employee Code:", boldCellFont);
            addInfoCell(empTable, record.getEmployeeCode(), normalCellFont);
            addInfoCell(empTable, "Total Working Days:", boldCellFont);
            addInfoCell(empTable, String.valueOf(record.getTotalWorkingDays()), normalCellFont);

            addInfoCell(empTable, "Pay Period Month:", boldCellFont);
            addInfoCell(empTable, String.valueOf(record.getMonth()), normalCellFont);
            addInfoCell(empTable, "Payable Days:", boldCellFont);
            addInfoCell(empTable, String.valueOf(record.getPayableDays()), normalCellFont);

            document.add(empTable);

            // Earnings & Deductions Table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{30, 20, 30, 20});
            table.setSpacingAfter(20);

            // Headers ("Amount (INR)" use kiya hai taaki glyph missing na aaye)
            addHeaderCell(table, "Earnings Component", boldCellFont);
            addHeaderCell(table, "Amount (INR)", boldCellFont);
            addHeaderCell(table, "Deduction Component", boldCellFont);
            addHeaderCell(table, "Amount (INR)", boldCellFont);

            // Row 1
            addCell(table, "Basic Salary", normalCellFont);
            addCell(table, String.format("%.2f", structure.getBasicSalary()), normalCellFont);
            addCell(table, "Provident Fund (PF)", normalCellFont);
            addCell(table, String.format("%.2f", structure.getProvidentFund()), normalCellFont);

            // Row 2
            addCell(table, "House Rent Allowance (HRA)", normalCellFont);
            addCell(table, String.format("%.2f", structure.getHra()), normalCellFont);
            addCell(table, "Professional Tax (PT)", normalCellFont);
            addCell(table, String.format("%.2f", structure.getProfessionalTax()), normalCellFont);

            // Row 3
            addCell(table, "Special Allowance", normalCellFont);
            addCell(table, String.format("%.2f", structure.getSpecialAllowance()), normalCellFont);
            addCell(table, "-", normalCellFont);
            addCell(table, "-", normalCellFont);

            // Summary Sub-total Row
            addHeaderCell(table, "Gross Salary (Earned)", boldCellFont);
            addHeaderCell(table, String.format("%.2f", record.getGrossSalary()), boldCellFont);
            addHeaderCell(table, "Total Deductions", boldCellFont);
            addHeaderCell(table, String.format("%.2f", record.getTotalDeductions()), boldCellFont);

            document.add(table);

            // Net Take-Home Box
            PdfPTable netTable = new PdfPTable(1);
            netTable.setWidthPercentage(100);
            PdfPCell netCell = new PdfPCell(new Phrase(
                    String.format("NET TAKE-HOME SALARY: INR %.2f", record.getNetSalary()),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, Color.WHITE)
            ));
            netCell.setBackgroundColor(new Color(41, 128, 185));
            netCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            netCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            netCell.setPadding(12);
            netTable.addCell(netCell);
            document.add(netTable);

            document.close();
        } catch (DocumentException e) {
            throw new RuntimeException("Error generating payslip PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(236, 240, 241));
        cell.setPadding(7);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addInfoCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(4);
        table.addCell(cell);
    }
}