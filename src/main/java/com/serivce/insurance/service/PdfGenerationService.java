package com.serivce.insurance.service;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.serivce.insurance.entity.Customer;
import com.serivce.insurance.entity.Policy;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfGenerationService {

    // public ByteArrayOutputStream generatePdf(Policy policy) throws DocumentException {
    //     ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //     Document document = new Document();
    //     PdfWriter.getInstance(document, baos);

    //   document.open();
    //     document.add(new Paragraph("Policy Details"));
    //     document.add(new Paragraph("Policy Name: " + policy.getPolicyName()));
    //     document.add(new Paragraph("Policy Type: " + policy.getPolicyType()));
    //     document.add(new Paragraph("Start Date: " + policy.getStartDate()));
    //     document.add(new Paragraph("Duration: " + policy.getDuration() + " months"));
    //     document.add(new Paragraph("Coverage Amount: " + policy.getCoverageAmount()));
    //     document.add(new Paragraph("Coverage Options: " + policy.getCoverageOptions()));
    //     document.add(new Paragraph("Deductible: " + policy.getDeductible()));
    //     document.add(new Paragraph("Beneficiary Name: " + policy.getBeneficiaryName()));
    //     document.add(new Paragraph("Beneficiary Relationship: " + policy.getBeneficiaryRelationship()));

    //     // Adding customer details
    //     Customer customer = policy.getCustomer();
    //     document.add(new Paragraph("Customer Details"));
    //     document.add(new Paragraph("Name: " + policy.getCustomer().getFullName()));
    //     document.add(new Paragraph("Email: " + policy.getCustomer().getEmail()));
    //     document.add(new Paragraph("Gender: " + policy.getCustomer().getGender()));
    //     document.add(new Paragraph("Occupation: " + policy.getCustomer().getOccupation()));
    //     document.add(new Paragraph("numberOfDependents: " + policy.getCustomer().getNumberOfDependents()));
    //     document.add(new Paragraph("maritalStatus: " + policy.getCustomer().getMaritalStatus()));
    //     document.add(new Paragraph("Date Of Birth: " + policy.getCustomer().getDateOfBirth()));
    //     document.add(new Paragraph("Phone: " + policy.getCustomer().getPhone()));

    //     document.add(new Paragraph("Address: " + policy.getCustomer().getAddress().toString()));

    //     document.close();

    //     return baos;
    // }

public ByteArrayOutputStream generatePdf(Policy policy) throws DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);

        document.open();

        // Create a title font and apply it to the title paragraph
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph titleParagraph = new Paragraph("Policy Details", titleFont);
        titleParagraph.setSpacingAfter(10); // Add some spacing after the title
        document.add(titleParagraph);

        // Create a table for policy details
        PdfPTable policyTable = new PdfPTable(2); // 2 columns
        policyTable.setWidthPercentage(100); // Table width is 100% of the page width

        // Add cells to the table
        policyTable.addCell(createTableCell("Policy Name:", policy.getPolicyName()));
        policyTable.addCell(createTableCell("Policy Type:", policy.getPolicyType().toString()));
        policyTable.addCell(createTableCell("Start Date:", policy.getStartDate().toString()));
        policyTable.addCell(createTableCell("Duration:", policy.getDuration() + " months"));
        policyTable.addCell(createTableCell("Coverage Amount:", String.valueOf(policy.getCoverageAmount())));
        policyTable.addCell(createTableCell("Coverage Options:", policy.getCoverageOptions()));
        policyTable.addCell(createTableCell("Deductible:", String.valueOf(policy.getDeductible())));
        policyTable.addCell(createTableCell("Beneficiary Name:", policy.getBeneficiaryName()));
        policyTable.addCell(createTableCell("Beneficiary Relationship:", policy.getBeneficiaryRelationship()));

        // Add the policy table to the document
        document.add(policyTable);

        // Add a section break
        document.add(new Paragraph("\n"));

        // Adding customer details
        Customer customer = policy.getCustomer();
        Paragraph customerTitleParagraph = new Paragraph("Customer Details", titleFont);
        customerTitleParagraph.setSpacingAfter(10); // Add some spacing after the title
        document.add(customerTitleParagraph);

        // Create a table for customer details
        PdfPTable customerTable = new PdfPTable(2); // 2 columns
        customerTable.setWidthPercentage(100); // Table width is 100% of the page width

        // Add cells to the table
        customerTable.addCell(createTableCell("Name:", customer.getFullName()));
        customerTable.addCell(createTableCell("Email:", customer.getEmail()));
        customerTable.addCell(createTableCell("Gender:", customer.getGender().toString()));
        customerTable.addCell(createTableCell("Occupation:", customer.getOccupation()));
        customerTable.addCell(createTableCell("Number of Dependents:", customer.getNumberOfDependents().toString()));
        customerTable.addCell(createTableCell("Marital Status:", customer.getMaritalStatus().toString()));
        customerTable.addCell(createTableCell("Date of Birth:", customer.getDateOfBirth().toString()));
        customerTable.addCell(createTableCell("Phone:", customer.getPhone()));
        customerTable.addCell(createTableCell("Address:", customer.getAddress().toString()));

        // Add the customer table to the document
        document.add(customerTable);

        document.close();

        return baos;
    }
private PdfPCell createTableCell(String label, String value) {
    PdfPCell cell = new PdfPCell();

    // Create label phrase with a bold font
    Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    Phrase labelPhrase = new Phrase(label, labelFont);

    // Create value phrase with a regular font
    Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    Phrase valuePhrase = new Phrase(value, valueFont);

    // Add label and value to the cell
    cell.addElement(labelPhrase);
    cell.addElement(valuePhrase);

    // Set cell padding
    cell.setPadding(5);

    return cell;
}
private PdfPCell createTableCell(String label, List<String> values) {
    PdfPCell cell = new PdfPCell();

    // Create label phrase with a bold font
    Font labelFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    Phrase labelPhrase = new Phrase(label, labelFont);
    cell.addElement(labelPhrase);

    // Add values as separate paragraphs
    Font valueFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    for (String value : values) {
        Paragraph valueParagraph = new Paragraph(value, valueFont);
        cell.addElement(valueParagraph);
    }

    // Set cell padding
    cell.setPadding(5);

    return cell;
}

 

}
