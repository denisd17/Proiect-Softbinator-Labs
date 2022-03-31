package com.example.SoftbinatorProject.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.Produces;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/user")
    public String userTest(){
        return "ok test";
    }

    @GetMapping("/admin")
    public String adminTest(){
        return "ok admin";
    }

    @GetMapping("/pdf")
    @Produces({"application/pdf"})
    public byte[] getPdf() throws IOException, DocumentException, TransformerConfigurationException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("test.pdf"));
        document.open();

        // Generare titlu document
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph titleParagraph = new Paragraph("FACTURA", font);
        //titleParagraph.setAlignment(Element.ALIGN_LEFT);

        document.add(titleParagraph);

        // Generare informatii factura
        font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        Paragraph paragraph = new Paragraph("Nr. Factura: " + "numar", font);
        document.add(paragraph);

        paragraph = new Paragraph("Data: " + "data", font);
        document.add(paragraph);

        paragraph = new Paragraph("Client: " + "Nume" + " " + "Prenume", font);
        document.add(paragraph);

        paragraph = new Paragraph("Catre: " + "Nume Organizatie", font);
        document.add(paragraph);

        paragraph = new Paragraph("Pentru: " + "Nume Proiect", font);
        document.add(paragraph);

        document.add(Chunk.NEWLINE);

        // Generare tabel detalii plata
        font = FontFactory.getFont(FontFactory.COURIER, 12, BaseColor.BLACK);
        PdfPTable table = new PdfPTable(4);
        PdfPCell c1 = new PdfPCell(new Phrase("Nr. crt", font));
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Servicii", font));
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Cantitate", font));
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Valoare (RON)", font));
        table.addCell(c1);

        table.addCell("1");
        table.addCell("donatie");
        table.addCell("1");
        table.addCell("100");

        table.addCell("TOTAL");
        table.addCell("");
        table.addCell("");
        table.addCell("100");


        document.add(table);

        document.close();

        Path pdfPath = Paths.get("test.pdf");
        byte[] pdf = Files.readAllBytes(pdfPath);

        return pdf;
    }
}
