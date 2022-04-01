package com.example.SoftbinatorProject.utils;

import com.example.SoftbinatorProject.models.ReceiptDetails;
import com.example.SoftbinatorProject.models.ReceiptTableHeaders;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class ReceiptUtility {
    public static String generateReceipt(Map<String, String> receiptInfo) throws FileNotFoundException, DocumentException {
        //TODO: Exceptions
        String docName = "factura_" + receiptInfo.get(ReceiptDetails.NR.name()) + "_" + receiptInfo.get(ReceiptDetails.NUME.name()) + ".pdf";
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(docName));
        document.open();

        // Generare titlu document
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph titleParagraph = new Paragraph("FACTURA", font);
        document.add(titleParagraph);

        // Generare informatii factura
        font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        for(ReceiptDetails field : ReceiptDetails.values()) {
            Paragraph paragraph = new Paragraph(field.toString() + ": " + receiptInfo.get(field.name())
                    .toUpperCase()
                    .replace('_', ' '), font);
            document.add(paragraph);
        }

        document.add(Chunk.NEWLINE);
        // Generare tabel detalii plata
        font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(4);

        // Crearea primei linii
        for(ReceiptTableHeaders field : ReceiptTableHeaders.values()) {
            table.addCell(new Phrase(field.name(), font));
        }
        // Crearea celei de a doua linii
        for(ReceiptTableHeaders field : ReceiptTableHeaders.values()) {
            table.addCell(new Phrase(receiptInfo.get(field.name()), font));
        }
        // Crearea celei de a treia linii
        table.addCell(new Phrase("TOTAL:", font));
        table.addCell("");
        table.addCell("");
        //TODO: Verificat ca merge bine totalul
        table.addCell(new Phrase(receiptInfo.get("TOTAL"), font));


        // Adaugarea tabelului in document
        document.add(table);

        document.close();

        return docName;
    }
}
