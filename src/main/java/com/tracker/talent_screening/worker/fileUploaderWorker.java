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

    /**
     * Processes an Excel file of applicants, persists each row as application-related entities,
     * and starts a Zeebe workflow instance ("cv_evaluation") for each row.
     *
     * <p>For each row in the first sheet the method:
     * - saves applicant, job, status and document records (via repository calls), and
     * - creates a Zeebe process instance with the row's name and email as variables.
     *
     * @param job the activated Zeebe job that triggered this worker
     * @return a map containing processing results; contains the key "completedCount" with the number of rows processed
     * @throws IOException if the Excel file cannot be read or closed
     */
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

    /**
     * Creates and persists an Applicant, an associated Job, and an ApplicantDocument from the given Excel row.
     *
     * <p>Extracts fields from the row in this order: name (cell 0), email (cell 1), phone (cell 2),
     * department (cell 3), status code (cell 4), CV link (cell 5). The method:
     * - resolves ApplicantStatus by name and sets it on the Applicant,
     * - creates and saves a Job and associates it with the Applicant,
     * - saves the Applicant,
     * - creates and saves an ApplicantDocument with the CV link and current timestamp linked to the Applicant.</p>
     *
     * @param row the Apache POI Row containing applicant data as described above
     * @throws java.util.NoSuchElementException if no ApplicantStatus is found for the status code extracted from the row
     */
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
