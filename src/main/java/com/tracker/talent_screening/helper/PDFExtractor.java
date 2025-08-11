package com.tracker.talent_screening.helper;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


import java.io.File;
import java.io.IOException;

public class PDFExtractor {

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
