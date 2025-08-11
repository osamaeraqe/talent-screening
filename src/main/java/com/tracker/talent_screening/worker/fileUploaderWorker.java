package com.tracker.talent_screening.worker;


import com.tracker.talent_screening.model.Applicant;
import com.tracker.talent_screening.model.ApplicantDocument;
import com.tracker.talent_screening.model.ApplicantStatus;
import com.tracker.talent_screening.model.Job;
import com.tracker.talent_screening.repository.ApplicantDocumentRepository;
import com.tracker.talent_screening.repository.ApplicantRepository;
import com.tracker.talent_screening.repository.ApplicantStatusRepository;
import com.tracker.talent_screening.repository.JobRepository;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class fileUploaderWorker {
    @Autowired
    private ZeebeClient zeebeClient;

    @Autowired
    private ApplicantStatusRepository applicantStatusRepository;

    @Autowired
    private ApplicantDocumentRepository applicantDocumentRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JobRepository jobRepository;

    @JobWorker(type = "process-excel-file")
    public Map<String , Object> processExcelFile(ActivatedJob job) throws IOException {
        String filePath = "C:/Users/osama/OneDrive/Desktop/hrSheet.xlsx";

        try {
            Map<String , Object> resultVariables =new HashMap<>();
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
            resultVariables.put("completedCount" , sheet.getPhysicalNumberOfRows());
            workbook.close();
            file.close();
            return resultVariables;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
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

        Applicant applicant = new Applicant();

        applicant.setEmail(email);
        applicant.setName(name);
        applicant.setPhonenumber(phone);



        ApplicantStatus applicantStatus = new ApplicantStatus();
        applicantStatus =   applicantStatusRepository.findByName(statusCode).get();



        applicant.setStatus(applicantStatus);

        Job job = new Job();
        job.setDepartment(Department);
        jobRepository.save(job);

        applicant.setApplyedJob(job);

        applicant = applicantRepository.save(applicant);

        ApplicantDocument applicantDocument = new ApplicantDocument();
        applicantDocument.setFilePath(CvLink);
        applicantDocument.setDocumentType("CV");
        applicantDocument.setUploadedAt(LocalDateTime.now());
        applicantDocument.setApplicant(applicant);

        applicantDocumentRepository.save(applicantDocument);


    }

}
