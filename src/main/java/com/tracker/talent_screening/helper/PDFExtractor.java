package com.tracker.talent_screening.helper;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.File;
import java.io.IOException;

public class PDFExtractor {

    /**
     * Extracts and returns all textual content from the given PDF file.
     *
     * Attempts to load the provided File as a PDF using Apache PDFBox and uses a
     * PDFTextStripper to extract text. The PDDocument is closed automatically.
     * If extraction fails (e.g., an IOException), the method returns an empty
     * string.
     *
     * @param file the PDF file to extract text from
     * @return the extracted text, or an empty string if extraction fails
     */
    public String extractTextFromPDF(File file) {
        String text = "";

        try (PDDocument document =  Loader.loadPDF(file);) {
            PDFTextStripper stripper = new PDFTextStripper();
            text = stripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text;
    }
}
