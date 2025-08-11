package com.tracker.talent_screening.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

// PDFBox imports used only within test helper methods to generate PDFs
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

class PDFExtractorTest {

    private PDFExtractor extractor;
    private File tempDir;

    @BeforeEach
    void setUp() throws IOException {
        extractor = new PDFExtractor();
        tempDir = Files.createTempDirectory("pdf-extractor-test-" + UUID.randomUUID()).toFile();
    }

    @AfterEach
    void tearDown() {
        if (tempDir != null && tempDir.exists()) {
            deleteRecursively(tempDir);
        }
    }

    private void deleteRecursively(File f) {
        if (f == null || !f.exists()) return;
        if (f.isDirectory()) {
            File[] children = f.listFiles();
            if (children != null) {
                for (File c : children) {
                    deleteRecursively(c);
                }
            }
        }
        try {
            Files.deleteIfExists(f.toPath());
        } catch (IOException ignored) {
        }
    }

    private File createPdfWithSinglePageText(String text) throws IOException {
        File pdf = new File(tempDir, "single-page-" + UUID.randomUUID() + ".pdf");
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                // Start writing near the top-left with a margin.
                contentStream.newLineAtOffset(72, PDRectangle.LETTER.getHeight() - 72);
                // Allow newlines in text by splitting and issuing new lines.
                for (String line : text.split("\\r?\\n")) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -16);
                }
                contentStream.endText();
            }

            doc.save(pdf);
        }
        return pdf;
    }

    private File createPdfWithMultiplePages(String[] pagesText) throws IOException {
        File pdf = new File(tempDir, "multi-page-" + UUID.randomUUID() + ".pdf");
        try (PDDocument doc = new PDDocument()) {
            for (String pageText : pagesText) {
                PDPage page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(doc, page)) {
                    contentStream.beginText();
                    contentStream.setFont(PDType1Font.TIMES_ROMAN, 14);
                    contentStream.newLineAtOffset(72, PDRectangle.LETTER.getHeight() - 72);
                    for (String line : pageText.split("\\r?\\n")) {
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -18);
                    }
                    contentStream.endText();
                }
            }
            doc.save(pdf);
        }
        return pdf;
    }

    private File createEmptyPdf() throws IOException {
        File pdf = new File(tempDir, "empty-" + UUID.randomUUID() + ".pdf");
        try (PDDocument doc = new PDDocument()) {
            // Intentionally no pages or an empty page.
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);
            doc.save(pdf);
        }
        return pdf;
    }

    @Nested
    @DisplayName("extractTextFromPDF happy paths")
    class HappyPathTests {

        @Test
        @DisplayName("Extracts exact text from a simple single-page PDF")
        void extractsTextFromSinglePagePdf() throws IOException {
            String expected = "Hello World from PDF\nSecond line";
            File pdf = createPdfWithSinglePageText(expected);

            String result = extractor.extractTextFromPDF(pdf);

            // PDFTextStripper adds line breaks; assert contains all notable parts.
            assertNotNull(result, "Result should not be null");
            assertTrue(result.contains("Hello World from PDF"), "Should contain first line");
            assertTrue(result.contains("Second line"), "Should contain second line");
            // Ensure some non-empty output
            assertFalse(result.trim().isEmpty(), "Extracted text should not be empty");
        }

        @Test
        @DisplayName("Extracts text across multiple pages preserving content from all pages")
        void extractsTextFromMultiplePages() throws IOException {
            String[] pages = {
                "Page 1: Alpha\nLine 2: Beta",
                "Page 2: Gamma",
                "Page 3: Delta\nEpsilon"
            };
            File pdf = createPdfWithMultiplePages(pages);

            String result = extractor.extractTextFromPDF(pdf);

            assertNotNull(result);
            for (String pageText : pages) {
                for (String segment : pageText.split("\\r?\\n")) {
                    assertTrue(result.contains(segment), "Should contain segment: " + segment);
                }
            }
        }

        @Test
        @DisplayName("Returns empty string for an empty PDF")
        void returnsEmptyStringForEmptyPdf() throws IOException {
            File pdf = createEmptyPdf();

            String result = extractor.extractTextFromPDF(pdf);

            // PDF with no drawn text should yield empty or whitespace. The implementation returns text directly.
            assertNotNull(result);
            assertTrue(result.trim().isEmpty(), "Empty PDF should yield empty text");
        }
    }

    @Nested
    @DisplayName("extractTextFromPDF error and edge cases")
    class ErrorAndEdgeCaseTests {

        @Test
        @DisplayName("Returns empty string when file does not exist")
        void returnsEmptyWhenFileDoesNotExist() {
            File nonExisting = new File(tempDir, "does-not-exist-" + UUID.randomUUID() + ".pdf");
            assertFalse(nonExisting.exists());

            String result = extractor.extractTextFromPDF(nonExisting);

            assertNotNull(result);
            assertTrue(result.isEmpty(), "Non-existent file should return empty string");
        }

        @Test
        @DisplayName("Returns empty string for an unreadable file (permission denied)")
        void returnsEmptyWhenFileUnreadable() throws IOException {
            File pdf = createPdfWithSinglePageText("Some content");
            assertTrue(pdf.setReadable(false, false), "Should be able to mark file as unreadable");

            String result;
            try {
                result = extractor.extractTextFromPDF(pdf);
            } finally {
                // Restore readability for cleanup on platforms that enforce it
                // Ignore result of setReadable; best-effort
                pdf.setReadable(true, true);
            }

            assertNotNull(result);
            assertTrue(result.isEmpty(), "Unreadable file should return empty string");
        }

        @Test
        @DisplayName("Returns empty string for a non-PDF file")
        void returnsEmptyForNonPdf() throws IOException {
            File txt = new File(tempDir, "not-a-pdf-" + UUID.randomUUID() + ".txt");
            try (FileWriter fw = new FileWriter(txt)) {
                fw.write("This is not a PDF");
            }

            String result = extractor.extractTextFromPDF(txt);

            assertNotNull(result);
            assertTrue(result.isEmpty(), "Non-PDF input should return empty string");
        }
    }
}