package com.tracker.talent_screening.helper;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
@Component
public class PDFExtractor {
    private static final Logger LOG = LoggerFactory.getLogger(PDFExtractor.class);

    public String extractTextFromPDF(File file) {
                if (file == null || !file.exists() || !file.isFile()) {
                        LOG.warn("Invalid PDF file: {}", file);
                        return "";
                    }
                try (PDDocument document = Loader.loadPDF(file)) {
                        PDFTextStripper stripper = new PDFTextStripper();
                        return stripper.getText(document);
                    } catch (IOException e) {
                        LOG.error("Failed to extract text from PDF: {}", file.getAbsolutePath(), e);
                        return "";
                    }
            }
}
