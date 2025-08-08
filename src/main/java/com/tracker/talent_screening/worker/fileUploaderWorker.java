package com.tracker.talent_screening.worker;


import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class fileUploaderWorker {
    @Autowired
    private ZeebeClient zeebeClient;
    @JobWorker(type = "process-excel-file")
    public void processExcelFile(ActivatedJob job) {
        String filePath = "C:/Users/osama/OneDrive/Desktop/hrSheet.xlsx";

        try {
            FileInputStream file = new FileInputStream(new File(filePath));
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0); // Get the first sheet

            for (Row row : sheet) {

                saveApplicants(row);

                zeebeClient.newCreateInstanceCommand()
                        .bpmnProcessId("cv_evaluation")
                        .latestVersion()
                        .variables("{\"name\": \"" + row.getCell(0).getStringCellValue() + "\", \"email\": \"" + row.getCell(1).getStringCellValue() + "\"}")
                        .send()
                        .join();
            }

            workbook.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error (e.g., log it, throw a custom exception)
        }
    }

    private void saveApplicants(Row row) {
        String name = row.getCell(0).getStringCellValue();
        String email = row.getCell(1).getStringCellValue();
        String phone = row.getCell(2).getStringCellValue();
        String Department = row.getCell(3).getStringCellValue();
        String statusCode = row.getCell(4).getStringCellValue();
        String CvLink = row.getCell(5).getStringCellValue();
        
    }

}
